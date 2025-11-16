package com.gkmonk.pos.rules.engine;

import com.gkmonk.pos.model.order.CourierOption;
import com.gkmonk.pos.model.rule.Carrier;
import com.gkmonk.pos.model.rule.Destination;
import com.gkmonk.pos.model.rule.Ranked;
import com.gkmonk.pos.model.rule.Rule;
import com.gkmonk.pos.model.rule.SimpleRule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CourierEngine {

    public static final Set<String> DELHI_NCR = Set.of(
            "new delhi","delhi","gurgaon","gurugram","noida","ghaziabad","faridabad","greater noida"
    );

    public static final Set<String> NORTH_EAST = Set.of(
            "arunachal pradesh","assam","manipur","meghalaya","mizoram","nagaland","tripura","sikkim"
    );

    public static class Weights { public int ruleBoost=10, priceRank=3, etaRank=2; }

    private final List<Rule> rules;
    private final Weights weights;

    public CourierEngine(List<Rule> rules, Weights weights) {
        this.rules = List.copyOf(rules);
        this.weights = weights;
    }


    public static CourierEngine withDefaultRules() {
        List<Rule> rules = new ArrayList<>();

        // 1) Delhi NCR → choose (prefer) DTDC
        rules.add(new SimpleRule(
                d -> d.city!=null && DELHI_NCR.contains(d.city.toLowerCase(Locale.ROOT)),
                Set.of(),
                List.of(Carrier.DTDC),
                false,
                "Delhi NCR → prefer DTDC"
        ));

        // 2) Bangalore → do not ship via DTDC
        rules.add(new SimpleRule(
                d -> d.city!=null && d.city.equalsIgnoreCase("Bangalore"),
                Set.of(Carrier.DTDC),
                List.of(),
                false,
                "Bengaluru → ban DTDC"
        ));

        // 3) North-East → prefer Delhivery or BlueDart
        rules.add(new SimpleRule(
                d -> d.state!=null && NORTH_EAST.contains(d.state.toLowerCase(Locale.ROOT)),
                Set.of(),
                List.of(Carrier.DELHIVERY, Carrier.BLUEDART),
                false,
                "North-East → prefer Delhivery/BlueDart"
        ));

        // 4) Rajasthan → Delhivery or XpressBees; if not available then BlueDart (as fallback preference)
        rules.add(new SimpleRule(
                d -> d.state!=null && d.state.equalsIgnoreCase("Rajasthan"),
                Set.of(),
                List.of(Carrier.DELHIVERY, Carrier.XPRESSBEES, Carrier.BLUEDART),
                false,
                "Rajasthan → prefer Delhivery/XpressBees (fallback BlueDart)"
        ));

        // 5) Dehradun → show Self Delivery option
        rules.add(new SimpleRule(
                d -> d.city!=null && d.city.equalsIgnoreCase("Dehradun"),
                Set.of(),
                List.of(),
                true,
                "Dehradun → allow Self Delivery"
        ));

        var w = new Weights();
        return new CourierEngine(rules, w);
    }

    /* ========= Engine ========= */

    public List<Ranked> sort(Destination destination, List<CourierOption> raw) {
        Objects.requireNonNull(destination);
        Objects.requireNonNull(raw);

        // find active rules
        List<Rule> active = rules.stream().filter(r -> r.appliesTo(destination)).collect(Collectors.toList());

        // prepare list (+ inject self, if any)
        List<CourierOption> options = new ArrayList<>(raw);
        if (active.stream().anyMatch(Rule::injectSelfDelivery)) {
            options.add(0, selfDelivery());
        }

        // compute bans
        Set<Carrier> bans = active.stream()
                .flatMap(r -> r.hardBans().stream())
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Carrier.class)));

        // enrich, filter banned carriers
        record Enriched(CourierOption o, Carrier carrier, double price, int etaDays) {}
        List<Enriched> enriched = options.stream()
                .map(o -> new Enriched(o, detectCarrier(o.getName()), safePrice(o), parseEta(o.getEta())))
                .filter(e -> !bans.contains(e.carrier))
                .collect(Collectors.toList());
        if (enriched.isEmpty()) return List.of();

        // preference boost map
        Map<Carrier,Integer> prefBoost = new EnumMap<>(Carrier.class);
        for (Enriched e : enriched) prefBoost.putIfAbsent(e.carrier, 0);
        for (Rule r : active) {
            List<Carrier> prefs = r.preferredOrder();
            for (int i=0; i<prefs.size(); i++) {
                int inc = prefs.size()-i; // earlier = stronger
                prefBoost.compute(prefs.get(i), (k,v)->(v==null?0:v)+inc);
            }
        }

        // ranks for cheaper/faster → invert
        List<Enriched> priceAsc = new ArrayList<>(enriched);
        priceAsc.sort(Comparator.comparingDouble(e->e.price));
        Map<String,Integer> priceRank = new HashMap<>();
        for (int i=0;i<priceAsc.size();i++) priceRank.put(priceAsc.get(i).o.getId(), i+1);

        List<Enriched> etaAsc = new ArrayList<>(enriched);
        etaAsc.sort(Comparator.comparingInt(e->e.etaDays));
        Map<String,Integer> etaRank = new HashMap<>();
        for (int i=0;i<etaAsc.size();i++) etaRank.put(etaAsc.get(i).o.getId(), i+1);

        int n = enriched.size();
        List<Ranked> out = new ArrayList<>(n);

        for (Enriched e : enriched) {
            int rb = prefBoost.getOrDefault(e.carrier, 0);
            int invPrice = n - priceRank.get(e.o.getId()) + 1;
            int invEta   = n - etaRank.get(e.o.getId()) + 1;

            double score = (weights.ruleBoost * rb)
                    + (weights.priceRank * invPrice)
                    + (weights.etaRank   * invEta);

            List<String> why = new ArrayList<>();
            if (rb > 0) {
                for (Rule r : active) if (r.preferredOrder().contains(e.carrier)) why.add(r.note());
            }
            why.add("₹" + String.format(Locale.US, "%.2f", e.price));
            why.add("ETA " + (e.etaDays == 999 ? "N/A" : (e.etaDays + " day(s)")));

            out.add(new Ranked(e.o, e.carrier, score, why));
        }

        out.sort(Comparator
                .comparingDouble((Ranked r)->r.score).reversed()
                .thenComparingDouble(r->r.option.getCost())
                .thenComparing(r->parseEta(r.option.getEta())));

        return out;
    }

    /* ========= Helpers ========= */

    private static CourierOption selfDelivery() {
        CourierOption c = new CourierOption();
        c.setId("SELF-DELIVERY");
        c.setName("Self Delivery");
        c.setService("Local");
        c.setEta("Same Day");
        c.setCost(0.0);
        c.setAggregator("In-house");
        return c;
    }

    private static final Pattern ETA_NUM = Pattern.compile("(\\d+)\\s*day", Pattern.CASE_INSENSITIVE);
    private static int parseEta(String eta) {
        if (eta == null || eta.isBlank()) return 999;
        Matcher m = ETA_NUM.matcher(eta);
        return m.find() ? Integer.parseInt(m.group(1)) : 999;
    }

    private static Carrier detectCarrier(String name) {
        if (name == null) return Carrier.OTHER;
        String n = name.toLowerCase(Locale.ROOT);
        if (n.startsWith("bluedart")) return Carrier.BLUEDART;
        if (n.startsWith("delhivery")) return Carrier.DELHIVERY;
        if (n.startsWith("dtdc")) return Carrier.DTDC;
        if (n.startsWith("xpressbees")) return Carrier.XPRESSBEES;
        if (n.startsWith("ekart")) return Carrier.EKART;
        if (n.startsWith("amazon")) return Carrier.AMAZON;
        return Carrier.OTHER;
    }

    private static double safePrice(CourierOption o) {
        double p = o.getCost();
        return Double.isFinite(p) ? p : Double.POSITIVE_INFINITY;
    }


    public static void main(String[] args) {
        // Minimal demo mapping (pretend we parsed your JSON into CourierOption)
        List<CourierOption> options = new ArrayList<>();
        options.add(make("206","BlueDart 0.5KG","2 Days",43.66));
        options.add(make("301","BlueDart Air 0.5KG","2 Days",51.92));
        options.add(make("11","Delhivery Surface 0.5Kg","1 Days",42.48));
        options.add(make("102","DTDC Air 0.5Kg","4 Days",29.50));
        options.add(make("333","DTDC Surface 0.5KG","2 Days",29.50));
        options.add(make("29","XpressBees 0.5 Kg","2 Days",37.76));

        CourierEngine engine = CourierEngine.withDefaultRules();

        // Try different destinations quickly:
        Destination d1 = new Destination("Gurugram","Haryana","122001",null);      // Delhi NCR → prefer DTDC
        Destination d2 = new Destination("Bangalore","Karnataka","560001",null);   // Ban DTDC
        Destination d3 = new Destination("Dehradun","Uttarakhand","248001",null);  // Inject Self Delivery
        Destination d4 = new Destination("Guwahati","Assam","781001",null);        // North-East preference
        Destination d5 = new Destination("Jaipur","Rajasthan","302001",null);      // RJ pref → Delhivery/XB/BD

        System.out.println("=== Delhi NCR ===");
        engine.sort(d1, options).forEach(r -> System.out.println(r.option.getId()+" "+r.option.getName()+" → "+r.why));

        System.out.println("\n=== Bengaluru ===");
        engine.sort(d2, options).forEach(r -> System.out.println(r.option.getId()+" "+r.option.getName()+" → "+r.why));

        System.out.println("\n=== Dehradun ===");
        engine.sort(d3, options).forEach(r -> System.out.println(r.option.getId()+" "+r.option.getName()+" → "+r.why));

        System.out.println("\n=== North-East ===");
        engine.sort(d4, options).forEach(r -> System.out.println(r.option.getId()+" "+r.option.getName()+" → "+r.why));

        System.out.println("\n=== Rajasthan ===");
        engine.sort(d5, options).forEach(r -> System.out.println(r.option.getId()+" "+r.option.getName()+" → "+r.why));
    }

    private static CourierOption make(String id, String name, String eta, double cost) {
        CourierOption c = new CourierOption();
        c.setId(id); c.setName(name); c.setEta(eta); c.setCost(cost);
        return c;
    }

}
