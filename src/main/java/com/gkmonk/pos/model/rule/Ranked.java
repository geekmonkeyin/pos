package com.gkmonk.pos.model.rule;

import com.gkmonk.pos.model.order.CourierOption;
import lombok.Data;

import java.util.List;

@Data
public class Ranked {

    public final CourierOption option;
    public final Carrier carrier;
    public final double score;
    public final List<String> why;
    public Ranked(CourierOption option, Carrier carrier, double score, List<String> why) {
        this.option = option; this.carrier = carrier; this.score = score; this.why = why;
    }

}
