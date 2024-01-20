package com.eaetirk.expressionevaluator.evaluatorObject.impl;

import com.eaetirk.expressionevaluator.util.ExpressionValidator;
import com.google.gson.Gson;
import com.eaetirk.expressionevaluator.evaluatorObject.Evaluator;
import com.eaetirk.expressionevaluator.model.Customer;
import org.springframework.stereotype.Service;

@Service
public class CustomerEvaluator implements Evaluator {

    @Override
    public boolean evaluateExpression(String expression, String value) {

        Gson gson = new Gson();
        Object jsonObject = gson.fromJson(value, Customer.class);
        return ExpressionValidator.validateLogicalExpression(expression, jsonObject);
    }
}
