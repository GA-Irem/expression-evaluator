package com.eaetirk.expressionevaluator.service;

import com.eaetirk.expressionevaluator.constant.ExpressionConstant;
import com.eaetirk.expressionevaluator.exception.ExpressionException;
import com.eaetirk.expressionevaluator.model.ExpressionIdentifier;
import com.eaetirk.expressionevaluator.repository.ExpressionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExpressionServiceImpl implements ExpressionService {

    @Autowired
    ExpressionRepository expressionRepository;
    @Override
    @Cacheable(value = "yourListCache")
    public List<ExpressionIdentifier> getAllExpressions() {
        return new ArrayList<ExpressionIdentifier>(expressionRepository.findAll());
    }

    @Override
    public ExpressionIdentifier getExpressionById(String expression_identifier_id) {
        return expressionRepository.findById(expression_identifier_id).orElse(null);
    }

    @Override
    public ExpressionIdentifier saveExpressionIdentifier(ExpressionIdentifier expressionIdentifier) {

        if(expressionIdentifier.getName() == null || expressionIdentifier.getName().isBlank()){
            throw new ExpressionException(HttpStatus.BAD_REQUEST.toString(), ExpressionConstant.MISSING_NAME, ExpressionConstant.MISSING_NAME_CAUSE, HttpStatus.BAD_REQUEST);
        }
        ExpressionIdentifier expressionIdentifierGet = getAllExpressions().stream().filter(expression->expressionIdentifier.getName().equals(expression.getName())).findAny().orElse(null);
        if(expressionIdentifierGet != null){
            throw new ExpressionException(HttpStatus.BAD_REQUEST.toString(), ExpressionConstant.EXPRESSION_NAME_ALREADY_EXISTS, ExpressionConstant.EXPRESSION_NAME_ALREADY_EXISTS_CAUSE, HttpStatus.BAD_REQUEST);
        }
        return expressionRepository.save(expressionIdentifier);
    }

    @Override
    public void deleteExpressionIdentifier(String expression_identifier_id) {
        expressionRepository.deleteById(expression_identifier_id);
    }
}
