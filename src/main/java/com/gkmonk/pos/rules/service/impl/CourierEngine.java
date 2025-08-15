package com.gkmonk.pos.rules.service.impl;

import com.gkmonk.pos.rules.model.CourierRules;
import com.gkmonk.pos.rules.model.Rule;
import com.gkmonk.pos.rules.service.IRulesEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Service
public class CourierEngine implements IRulesEngine {

    private static final Logger log = LoggerFactory.getLogger(CourierEngine.class);
    private ExpressionParser spelExpressionParser;
    private StandardEvaluationContext standardEvaluationContext;
    private List<CourierRules> rawRules;

    @Override
    @PostConstruct
    public void init() {
        spelExpressionParser = new SpelExpressionParser();
        standardEvaluationContext = new StandardEvaluationContext();
    }

    @Override
    public Rule validateRules(CourierRules courierRules , Map<String,Object> contextVariables) {
        prepareContext(contextVariables);
        if (courierRules == null) {
            log.error("No rules loaded for CourierEngine");
            return null;
        }
        for (Rule ruleSelected : courierRules.getRules()) {
            Expression exp = spelExpressionParser.parseExpression(ruleSelected.getCondition());
            Boolean result = exp.getValue(standardEvaluationContext, Boolean.class);
            if (result) {
                return ruleSelected;
            }
        }
        return null;
    }

    private void prepareContext(Map<String, Object> contextVariables) {
        if(contextVariables !=null && !contextVariables.isEmpty()){
            for (Map.Entry<String, Object> entry : contextVariables.entrySet()) {
                standardEvaluationContext.setVariable(entry.getKey(), entry.getValue());
            }
        } else {
            log.warn("No context variables provided for CourierEngine");
        }
    }

}
