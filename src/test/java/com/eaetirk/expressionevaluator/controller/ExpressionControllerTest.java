package com.eaetirk.expressionevaluator.controller;

import com.eaetirk.expressionevaluator.constant.ExpressionConstant;
import com.eaetirk.expressionevaluator.model.ExpressionIdentifier;
import com.eaetirk.expressionevaluator.service.ExpressionService;
import com.eaetirk.expressionevaluator.util.SystemTokenStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExpressionControllerTest {

    @InjectMocks
    private ExpressionController expressionController;

    @Mock
    private ExpressionService expressionService;

    @Mock
    private SystemTokenStore systemTokenStore;

    @Test
    public void testExpressionCreation(){
        // Arrange
        ExpressionIdentifier expressionIdentifier = new ExpressionIdentifier();
        String expression = "(firstName == \"JOHN\" && salary < 100) AND (address != null || address.city != \"Washington\")";

        expressionIdentifier.setName("Complex logical expression");
        expressionIdentifier.setValue(expression);

        Map<String,String> headers = new HashMap<>();
        headers.put(ExpressionConstant.IAM_PASSWORD, "wise");
        headers.put(ExpressionConstant.IAM_USERNAME, "wise");
        headers.put(ExpressionConstant.GRANT_TYPE, "password");
        headers.put(ExpressionConstant.CLIENT_SECRET, "OOXE#$GGFG");
        headers.put(ExpressionConstant.CLIENT_ID, "eaetirk");


        when(expressionService.saveExpressionIdentifier(expressionIdentifier)).thenReturn(expressionIdentifier);
        when(systemTokenStore.getAccessToken("wise", "wise", "password", "eaetirk", "OOXE#$GGFG")).thenReturn("token");
        // Act
        ResponseEntity<Object> response = expressionController.createExpression(expressionIdentifier, headers);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

    }

    @Test
    public void testUnauthorized(){
        // Arrange
        ExpressionIdentifier expressionIdentifier = new ExpressionIdentifier();
        String expression = "(firstName == \"JOHN\" && salary < 100) AND (address != null || address.city != \"Washington\")";

        expressionIdentifier.setName("Complex logical expression");
        expressionIdentifier.setName(expression);

        Map<String,String> headers = new HashMap<>();
        headers.put(ExpressionConstant.IAM_PASSWORD, "wise");
        headers.put(ExpressionConstant.IAM_USERNAME, "wise");
        headers.put(ExpressionConstant.GRANT_TYPE, "password");
        headers.put(ExpressionConstant.CLIENT_SECRET, "OOXE#$GGFG");
        headers.put(ExpressionConstant.CLIENT_ID, "eaetirk");

        when(systemTokenStore.getAccessToken("wise", "wise", "password", "eaetirk", "OOXE#$GGFG")).thenReturn(null);
        // Act
        ResponseEntity<Object> response = expressionController.createExpression(expressionIdentifier, headers);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

    }
}
