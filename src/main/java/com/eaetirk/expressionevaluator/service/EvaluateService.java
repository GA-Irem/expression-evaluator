package com.eaetirk.expressionevaluator.service;

import java.util.Map;

public interface EvaluateService {

    boolean evaluateExpression(String expression, Map<String, Map<String, Object>> value, String evaluatedObjectName);
}
