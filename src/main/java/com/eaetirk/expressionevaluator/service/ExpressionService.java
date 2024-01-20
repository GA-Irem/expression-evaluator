package com.eaetirk.expressionevaluator.service;

import com.eaetirk.expressionevaluator.model.ExpressionIdentifier;

import java.util.List;

public interface ExpressionService {
    List<ExpressionIdentifier> getAllExpressions();

    ExpressionIdentifier getExpressionById(String expression_identifier_id);

    ExpressionIdentifier saveExpressionIdentifier(ExpressionIdentifier expressionIdentifier);

    void deleteExpressionIdentifier(String expression_identifier_id);
}
