package com.eaetirk.expressionevaluator.service;

import com.eaetirk.expressionevaluator.constant.ExpressionConstant;
import com.eaetirk.expressionevaluator.evaluatorObject.Evaluator;
import com.eaetirk.expressionevaluator.evaluatorObject.impl.CustomerEvaluator;
import com.eaetirk.expressionevaluator.exception.ExpressionException;
import com.eaetirk.expressionevaluator.model.ExpressionIdentifier;
import com.eaetirk.expressionevaluator.util.JsonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class EvaluateServiceImpl implements EvaluateService {


    @Autowired
    ExpressionService expressionService;

    @Autowired
    private JsonValidator jsonValidator;

    @Autowired
    private Evaluator evaluator;



    @Override
    public boolean evaluateExpression(String expression_name, Map<String, Map<String, Object>> value, String evaluatedObjectName) {

        //Make necessary Validations

        ExpressionIdentifier expressionIdentifierGet = expressionService.getAllExpressions().stream().filter(expression -> expression_name.equals(expression.getName())).findAny().orElse(null);

        if (expressionIdentifierGet == null) {
            throw new ExpressionException(HttpStatus.BAD_REQUEST.toString(), ExpressionConstant.EXPRESSION_NOT_FOUND, ExpressionConstant.EXPRESSION_NOT_FOUND_REASON, HttpStatus.BAD_REQUEST);
        }

        boolean isValidJson = jsonValidator.isValid(value.toString());
        if (!isValidJson) {
            throw new ExpressionException(HttpStatus.BAD_REQUEST.toString(), ExpressionConstant.JSON_IS_NOT_VALID_MESSAGE, ExpressionConstant.JSON_IS_NOT_VALID, HttpStatus.BAD_REQUEST);
        }

         return evaluateExpressionForObject(expressionIdentifierGet.getValue(), value, evaluatedObjectName);
    }

    private boolean evaluateExpressionForObject(String expression, Map<String, Map<String, Object>> value, String evaluatedObjectName) {

        switch (evaluatedObjectName){
            case ExpressionConstant.CUSTOMER :
                evaluator = new CustomerEvaluator();
                return evaluator.evaluateExpression(expression, value.toString());
            default:
                throw new ExpressionException(HttpStatus.BAD_REQUEST.toString(), ExpressionConstant.JSON_IS_NOT_VALID_MESSAGE,ExpressionConstant.JSON_IS_NOT_VALID, HttpStatus.BAD_REQUEST);
        }

    }
}
