package com.gkmonk.pos.model.rule;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class SimpleRule implements Rule{

    private final java.util.function.Predicate<Destination> when;
    private final Set<Carrier> bans;
    private final List<Carrier> prefs;
    private final boolean injectSelf;
    private final String note;
    public SimpleRule(java.util.function.Predicate<Destination> when,
                      Set<Carrier> bans, List<Carrier> prefs,
                      boolean injectSelf, String note) {
        this.when = when; this.bans = bans; this.prefs = prefs; this.injectSelf = injectSelf; this.note = note;
    }
    public boolean appliesTo(Destination d) { return when.test(d); }
    public Set<Carrier> hardBans() { return bans; }
    public List<Carrier> preferredOrder() { return prefs; }
    public boolean injectSelfDelivery() { return injectSelf; }
    public String note() { return note; }


}
