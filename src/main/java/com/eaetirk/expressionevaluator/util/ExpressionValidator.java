package com.eaetirk.expressionevaluator.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.eaetirk.expressionevaluator.constant.ExpressionConstant;
import com.eaetirk.expressionevaluator.exception.ExpressionException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class ExpressionValidator {

    private static final Logger Expression_Logger = LoggerFactory.getLogger(ExpressionValidator.class);

    public static <T>  boolean validateLogicalExpression(String expression, T context) throws ExpressionException{
        Expression_Logger.info("Validating Expression ");
        List<Boolean> booleanResultORList = new ArrayList<>();
        List<Boolean> booleanResultANDList = new ArrayList<>();
        boolean firstResult = false;
        List<String> clauses_btw_o_c_parentheses = findExpressionsBtwOpenCloseParentheses(expression);
        List<String> clauses_btw_c_o_parentheses = findExpressionsBtwCloseOpenParentheses(expression);

        if(expression != null && clauses_btw_c_o_parentheses.isEmpty()){
           if(expression.contains(ExpressionConstant.LOGICAL_OR) || expression.contains(ExpressionConstant.LOGICAL_AND)) {
               throw new ExpressionException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExpressionConstant.ERROR_EVALUATING_EXPRESSION, ExpressionConstant.INVALID_EXPRESSION, HttpStatus.INTERNAL_SERVER_ERROR);
           }
            Expression_Logger.info("There is no OR and AND Parentheses in the expression");
            String filteredCondition = expression.replaceAll("[()]", "");
            Function<T, Boolean> evaluator = buildEvaluator(filteredCondition);
            firstResult = evaluator.apply(context);
            return firstResult;
        }

        if(clauses_btw_o_c_parentheses.isEmpty()){
            if (!clauses_btw_c_o_parentheses.isEmpty()){
                throw new ExpressionException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExpressionConstant.ERROR_EVALUATING_EXPRESSION, ExpressionConstant.INVALID_EXPRESSION_PARENTHESES, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }else{
            String clause = null;
            for(int i=0; i<clauses_btw_c_o_parentheses.size(); i++){
                clause = clauses_btw_c_o_parentheses.get(i);
                if(clauses_btw_o_c_parentheses.size()>1){
                    StringBuilder sb_expression = new StringBuilder();
                    if (i==0 && !clauses_btw_o_c_parentheses.get(i).isEmpty()){
                        Function<T, Boolean> evaluator = buildEvaluator(clauses_btw_o_c_parentheses.get(0));
                        if(clause.equals(ExpressionConstant.LOGICAL_OR)){
                            booleanResultORList.add(evaluator.apply(context));
                        }else if(clause.equals(ExpressionConstant.LOGICAL_AND)){
                            booleanResultANDList.add(evaluator.apply(context));
                        }
                    }
                    switch (clause) {
                        case ExpressionConstant.LOGICAL_OR:
                            if(null != clauses_btw_o_c_parentheses.get(i+1) && !clauses_btw_o_c_parentheses.get(i+1).isEmpty()){
                                sb_expression.append(clauses_btw_o_c_parentheses.get(i+1));
                                Function<T, Boolean> evaluator = buildEvaluator(sb_expression.toString());
                                booleanResultORList.add(evaluator.apply(context));
                                sb_expression.setLength(0);
                            }else{
                                throw new ExpressionException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExpressionConstant.ERROR_EVALUATING_EXPRESSION, ExpressionConstant.INVALID_EXPRESSION, HttpStatus.INTERNAL_SERVER_ERROR);
                            }
                        case ExpressionConstant.LOGICAL_AND:
                            if(null != clauses_btw_o_c_parentheses.get(i+1) && !clauses_btw_o_c_parentheses.get(i+1).isEmpty()){
                                sb_expression.append(clauses_btw_o_c_parentheses.get(i+1));
                                Function<T, Boolean> evaluator = buildEvaluator(sb_expression.toString());
                                booleanResultANDList.add(evaluator.apply(context));
                                sb_expression.setLength(0);
                            }else{
                                throw new ExpressionException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), ExpressionConstant.ERROR_EVALUATING_EXPRESSION, ExpressionConstant.INVALID_EXPRESSION, HttpStatus.INTERNAL_SERVER_ERROR);
                            }
                    }
                }
            }
        }

        if(!booleanResultANDList.isEmpty()){
            for (boolean value : booleanResultANDList) {
                if (!value) {
                    return false; // If any value is false, return false
                }
            }
            return true;
        }
        if(!booleanResultORList.isEmpty()){
            for (boolean value : booleanResultORList) {
                if (value) {
                    return true; // If any value is true, return true
                }
            }
            return false;
        }

        return false;
    }

    private static List<String> findExpressionsBtwCloseOpenParentheses(String expression) {

        List<String> clauses_btw_c_o_parentheses = new ArrayList<String>();
        Pattern pattern1 = Pattern.compile("\\)(.*?)\\(");
        Matcher matcher1 = pattern1.matcher(expression);

        while (matcher1.find()) {
            // Group 1 contains the value between parentheses
            String match1 = matcher1.group(1);
            clauses_btw_c_o_parentheses.add(match1);
        }
        return clauses_btw_c_o_parentheses;
    }

    private static List<String> findExpressionsBtwOpenCloseParentheses(String expression) {

        List<String> clauses_btw_o_c_parentheses = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(expression);
        while (matcher.find()) {
            String match = matcher.group(1);// Group 1 contains the value between parentheses
            clauses_btw_o_c_parentheses.add(match);
        }
        return clauses_btw_o_c_parentheses;
    }
    private static <T> Function<T, Boolean> buildEvaluator(String expression) {

        if (expression != null && !expression.isEmpty() && Character.isWhitespace(expression.charAt(0)))  {
            expression = expression.substring(1); // Omit the first character
        }

        String finalExpression = expression;

        Predicate<T> predicate = t -> {
            String[] conditionsOR = finalExpression.split("\\|\\|");
            String[] conditionsAND = finalExpression.split("&&");
            boolean clauseResult = false;
            if(conditionsOR.length > 1){
                clauseResult = false;
                for (String condition : conditionsOR) {
                    String filteredCondition = condition.replaceAll("[()]", "");
                    if (evaluateCondition(filteredCondition, t)) {
                        clauseResult = true;
                        break;
                    }
                }
                if (!clauseResult) {
                    return false;
                }
            }else if(conditionsAND.length > 1){
                clauseResult = true;
                for (String condition : conditionsAND) {
                    String filteredCondition = condition.replaceAll("[()]", "");
                    if (!evaluateCondition(filteredCondition, t)) {
                        clauseResult = false;
                        break;
                    }
                }
                if (clauseResult) {
                    return true;
                }
            }else{
                return evaluateCondition(finalExpression, t);
            }

            return clauseResult;
        };

        return predicate::test;
    }

    private static <T> boolean evaluateCondition(String condition, T context) throws ExpressionException {
        if (condition != null && !condition.isEmpty() && Character.isWhitespace(condition.charAt(0)))  {
            condition = condition.substring(1); // Omit the first character
        }
        String[] parts = condition.split("\\s+");
        if (parts.length == 3) {
            String fieldName = parts[0];
            String operator = parts[1];
            String value = parts[2].replaceAll("\"", ""); // Remove quotes around the value

            Map<String, Object> fieldValues = Arrays.stream(context.getClass().getDeclaredFields())
                    .peek(field -> field.setAccessible(true))
                    .collect(Collectors.toMap(Field::getName, field -> {
                        try {
                            if (field.get(context) == null){
                                field.set(context, "");
                            }
                            return field.get(context);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }));
            String[] attributes = fieldName.split("\\.");
            Object fieldValue= null;
            if(attributes.length>1){
                fieldValue = fieldValues.get(attributes[0]);
                Field field = null;
                try {
                    field = fieldValue.getClass().getDeclaredField(attributes[1]);
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
                field.setAccessible(true);
                try {
                    fieldValue = field.get(fieldValue);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

            }else{
                fieldValue = fieldValues.get(fieldName);
            }
            switch (operator) {
                case "==":
                    if(fieldValue==null){
                        return StringUtils.isEmpty(value);
                    }
                    return fieldValue.equals(value);
                case "<" :
                    return (Integer)fieldValue < Integer.parseInt(value);
                case ">" :
                    return (int)fieldValue > Integer.parseInt(value);
                case "!=":
                    if(fieldValue==null){
                        return StringUtils.isNotEmpty(value);
                    }
                    return !fieldValue.equals(value);
                case ">=":
                    return (int)fieldValue >= Integer.parseInt(value);
                case "<=":
                    return (int)fieldValue <= Integer.parseInt(value);
                // Add more operators as needed
            }

        }
        return false;
    }

}
