package com.reactive.expressionevaluator.domain.utility;

import com.leapwise.expressionevaluator.exception.ExpressionException;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LogicalExpressionValidator1 {

    public static <T> boolean validateLogicalExpression(String expression, T context) {
        Function<T, Boolean> evaluator = buildEvaluator(expression);
        return evaluator.apply(context);
    }

    private static <T> Function<T, Boolean> buildEvaluator(String expression) {

        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(expression);

        while (matcher.find()) {
            String match = matcher.group(1);// Group 1 contains the value between parentheses
            System.out.println(match);
        }

        String inputString = "This is a ) sample ( string with ) values ( between parentheses.";

        Pattern pattern1 = Pattern.compile("\\)(.*?)\\(");
        Matcher matcher1 = pattern1.matcher(inputString);

        while (matcher1.find()) {
            String match1 = matcher1.group(1); // Group 1 contains the value between parentheses
            System.out.println(match1);
        }

        String[] clauses = expression.split("\\s+OR\\s+");

        Predicate<T> predicate = t -> {
            for (String clause : clauses) {
                String[] conditions = clause.split("\\s+AND\\s+");
                boolean clauseResult = true;
                for (String condition : conditions) {
                    String filteredCondition = condition.replaceAll("[()]", "");
                    try {
                        if (!evaluateCondition(filteredCondition, t)) {
                            clauseResult = false;
                            break;
                        }
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (clauseResult) {
                    return true;
                }
            }
            return false;
        };

        return predicate::test;
    }

    private static <T> boolean evaluateCondition(String condition, T context) throws ExpressionException, NoSuchFieldException, IllegalAccessException {
        // Implement your custom condition evaluation logic here
        // For simplicity, assuming the condition is of the form "field == value"
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
                Field field = fieldValue.getClass().getDeclaredField(attributes[1]);
                field.setAccessible(true);
                fieldValue = field.get(fieldValue);

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

    public static void main(String[] args) {
        // Example usage
      //  Customer customer = new Customer();
     //   customer.setFirstName("JOHN");
      //  customer.setSalary(90);

    //    Address address = new Address();
    //    address.setCity("Washington");
        // String expression = "(firstName == \"JOHN\" AND salary > 100) OR (address != null AND address.city == \"Washington\")";

        String expression = "(firstName == \"JOHN\" AND salary < 100) OR (address != null AND address.city == \"Washington\")";
        //String expression = "(address.city == \"Washington\")";

      //  customer.setAddress(address);

    //    boolean result = validateLogicalExpression(expression, customer);
     //   System.out.println("Expression is " + (result ? "true" : "false"));
    }
}

