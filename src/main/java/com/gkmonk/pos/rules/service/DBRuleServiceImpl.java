package com.gkmonk.pos.rules.service;

import com.gkmonk.pos.repo.rules.DBRuleRepo;
import com.gkmonk.pos.rules.model.CourierRules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DBRuleServiceImpl {

    @Autowired
    private DBRuleRepo dbRuleRepo;

    public Optional<CourierRules> findAllRules(String ruleId) {
        return dbRuleRepo.findRuleById(ruleId);
    }
}
