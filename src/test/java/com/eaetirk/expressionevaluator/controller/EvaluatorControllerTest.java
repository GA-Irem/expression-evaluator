package com.eaetirk.expressionevaluator.controller;

import com.eaetirk.expressionevaluator.constant.ExpressionConstant;
import com.eaetirk.expressionevaluator.service.EvaluateService;
import com.eaetirk.expressionevaluator.util.SystemTokenStore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EvaluatorControllerTest {

    @InjectMocks
    private EvaluateController evaluateController;

    @Mock
    private EvaluateService evaluateService;

    @Mock
    private SystemTokenStore systemTokenStore;

    @Test
    public void testExpressionEvaluation() throws IOException {
        // Arrange data
        String customerString = "{ \"firstName\":\"JOHN\", \"lastName\":\"DOE\", \"address\":{\"city\":\"Chicago\",\"zipCode\":1234,\"street\":\"56th\",\"houseNumber\":2345}, \"salary\":99, \"type\":\"BUSINESS\" }";

        Map<String,Object> request = new HashMap<>();
        request.put("name", "Complex logical expression");
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, Object> customerMap = objectMapper.readValue(customerString, HashMap.class);

        Map<String,Map<String, Object>> serviceMap = new HashMap<>();
        request.put("customer", customerMap );
        serviceMap.put("customer", customerMap );
        Map<String,String> headers = new HashMap<>();
        headers.put(ExpressionConstant.IAM_PASSWORD, "wise");
        headers.put(ExpressionConstant.IAM_USERNAME, "wise");
        headers.put(ExpressionConstant.GRANT_TYPE, "password");
        headers.put(ExpressionConstant.CLIENT_SECRET, "OOXE#$GGFG");
        headers.put(ExpressionConstant.CLIENT_ID, "eaetirk");

        when(systemTokenStore.getAccessToken("wise", "wise", "password", "eaetirk", "OOXE#$GGFG")).thenReturn("token");
        // Act
        ResponseEntity<Object> response = evaluateController.evaluateExpression(request,headers);
        verify(evaluateService, times(1)).evaluateExpression(anyString(), any(HashMap.class), anyString());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    public void testUnauthorizedExpressionEvaluation() throws JsonProcessingException {
        // Arrange data

        String customerString = "{ \"firstName\":\"JOHN\", \"lastName\":\"DOE\", \"address\":{\"city\":\"Chicago\",\"zipCode\":1234,\"street\":\"56th\",\"houseNumber\":2345}, \"salary\":99, \"type\":\"BUSINESS\" }";


        Map<String,Object> request = new HashMap<>();
        request.put("name", "Complex logical expression");
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, Object> customerMap = objectMapper.readValue(customerString, HashMap.class);

        request.put("customer", customerMap );

        Map<String,String> headers = new HashMap<>();
        headers.put(ExpressionConstant.IAM_PASSWORD, "wise");
        headers.put(ExpressionConstant.IAM_USERNAME, "wise");
        headers.put(ExpressionConstant.GRANT_TYPE, "password");
        headers.put(ExpressionConstant.CLIENT_SECRET, "OOXE#$GGFG");
        headers.put(ExpressionConstant.CLIENT_ID, "eaetirk");

        when(systemTokenStore.getAccessToken("wise", "wise", "password", "eaetirk", "OOXE#$GGFG")).thenReturn(null);
        // Act
        ResponseEntity<Object> response = evaluateController.evaluateExpression(request,headers);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

    }
}
