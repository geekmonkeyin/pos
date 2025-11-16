package com.gkmonk.pos.model.rule;

import java.util.List;
import java.util.Set;

public interface Rule {

    boolean appliesTo(Destination d);
    Set<Carrier> hardBans();                // remove these carriers
    List<Carrier> preferredOrder();         // earlier = stronger preference
    boolean injectSelfDelivery();           // add "Self Delivery" option
    String note();

}
