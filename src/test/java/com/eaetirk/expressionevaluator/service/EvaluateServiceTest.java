package com.eaetirk.expressionevaluator.service;

import com.eaetirk.expressionevaluator.evaluatorObject.impl.CustomerEvaluator;
import com.eaetirk.expressionevaluator.exception.ExpressionException;
import com.eaetirk.expressionevaluator.model.ExpressionIdentifier;
import com.eaetirk.expressionevaluator.util.JsonValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class EvaluateServiceTest {

        @InjectMocks
        private EvaluateServiceImpl evaluateService;

         @Mock
         private CustomerEvaluator evaluator;

        @Mock
        private ExpressionServiceImpl expressionService;

        @Mock
        private JsonValidator jsonValidator;



        public void testEvaluateExpression() throws IOException {
            // Arrange
            ExpressionIdentifier expressionIdentifier = new ExpressionIdentifier();
            String expression = "(firstName == \"JOHN\" && salary < 100) AND (address != null || address.city != \"Washington\")";

            expressionIdentifier.setName("Complex logical expression");
            expressionIdentifier.setValue(expression);

            String customerString = "{ \"firstName\":\"JOHN\", \"lastName\":\"DOE\", \"address\":{\"city\":\"Chicago\",\"zipCode\":1234,\"street\":\"56th\",\"houseNumber\":2345}, \"salary\":99, \"type\":\"BUSINESS\" }";
            ObjectMapper objectMapper = new ObjectMapper();
            HashMap<String, Object> customerMap = objectMapper.readValue(customerString, HashMap.class);

            Map<String,Map<String, Object>> serviceMap = new HashMap<>();
            serviceMap.put("customer", customerMap );
            String expressionName = "Complex logical expression";

            when(expressionService.getAllExpressions()).thenReturn(List.of(expressionIdentifier));
            when(jsonValidator.isValid(serviceMap.toString())).thenReturn(true);
           // Act
           //evaluateService.evaluateExpression(expressionName, serviceMap, "customer");
           //verify(evaluator, times(1)).evaluateExpression(anyString(), anyString());

        }

    @Test
    public void testEvaluateExpression_NullExpressionValue_throwException() throws IOException {
        // Arrange
        String customer = "{firstName=JOHN, lastName=DOE, address={city=Chicago, zipCode=1234, street=56th, houseNumber=2345}, salary=99, type=BUSINESS}";
        String expressionName = "Complex logical expression";

        String customerString = "{ \"firstName\":\"JOHN\", \"lastName\":\"DOE\", \"address\":{\"city\":\"Chicago\",\"zipCode\":1234,\"street\":\"56th\",\"houseNumber\":2345}, \"salary\":99, \"type\":\"BUSINESS\" }";
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, Object> customerMap = objectMapper.readValue(customerString, HashMap.class);

        Map<String,Map<String, Object>> serviceMap = new HashMap<>();
        serviceMap.put("customer", customerMap );

        when(expressionService.getAllExpressions()).thenReturn(new ArrayList<>());

        assertThrows(ExpressionException.class, () -> {
            evaluateService.evaluateExpression(expressionName, serviceMap, "customer");
        });
    }

    @Test
    public void testEvaluateExpression_InvalidJSONFormat_throwException() throws IOException {
        // Arrange
        String customer = "{firstName=JOHN, lastName=DOE, address={city=Chicago, zipCode=1234, street=56th, houseNumber=2345}, salary=99, type=BUSINESS}";
        String expressionName = "Complex logical expression";

        ExpressionIdentifier expressionIdentifier = new ExpressionIdentifier();
        String expression = "(firstName == \"JOHN\" && salary < 100) AND (address != null || address.city != \"Washington\")";

        String customerString = "{ \"firstName\":\"JOHN\", \"lastName\":\"DOE\", \"address\":{\"city\":\"Chicago\",\"zipCode\":1234,\"street\":\"56th\",\"houseNumber\":2345}, \"salary\":99, \"type\":\"BUSINESS\" }";
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, Object> customerMap = objectMapper.readValue(customerString, HashMap.class);

        Map<String,Map<String, Object>> serviceMap = new HashMap<>();
        serviceMap.put("customer", customerMap );

        expressionIdentifier.setName("Complex logical expression");
        expressionIdentifier.setValue(expression);

        when(expressionService.getAllExpressions()).thenReturn(List.of(expressionIdentifier));
        when(jsonValidator.isValid(serviceMap.toString())).thenReturn(false);

       assertThrows(ExpressionException.class, () -> {
            evaluateService.evaluateExpression(expressionName, serviceMap, "customer");
        });
    }

}

