package com.gkmonk.pos.rules.service;

import com.gkmonk.pos.rules.model.CourierRules;
import com.gkmonk.pos.rules.model.Rule;

import java.util.Map;

public interface IRulesEngine {

    public void init();
    Rule validateRules(CourierRules rules, Map<String, Object> request);
}
