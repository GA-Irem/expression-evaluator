package com.eaetirk.expressionevaluator.controller;

import com.eaetirk.expressionevaluator.constant.ExpressionConstant;
import com.eaetirk.expressionevaluator.exception.ErrorResponse;
import com.eaetirk.expressionevaluator.exception.ExpressionException;
import com.eaetirk.expressionevaluator.service.EvaluateService;
import com.eaetirk.expressionevaluator.util.SystemTokenStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class EvaluateController {
    @Autowired
    EvaluateService evaluateService;

    @Autowired
    @Qualifier("systemTokenStore")
    private SystemTokenStore systemTokenStore;

    private static final Logger Evaluate_Logger = LoggerFactory.getLogger(EvaluateService.class);

    @PostMapping("/evaluate")
    public ResponseEntity evaluateExpression(@RequestBody Map<String,Object> evaluatorRequest, @RequestHeader Map<String,String> headers) {
        try{
            String username = headers.get(ExpressionConstant.IAM_PASSWORD);
            String password = headers.get(ExpressionConstant.IAM_USERNAME);
            String grant_type = headers.get(ExpressionConstant.GRANT_TYPE);
            String client_id = headers.get(ExpressionConstant.CLIENT_ID);
            String client_secret = headers.get(ExpressionConstant.CLIENT_SECRET);

            String accessToken = systemTokenStore.getAccessToken(username, password, grant_type, client_id, client_secret);
            Evaluate_Logger.info("Evaluating expression operation started");
            boolean evaluationResult = false;

            String expressionName = null;
            Map<String, Map<String, Object>> value = null;
            String evaluatedObjectName = null;
            if(evaluatorRequest.size() == 2 ){
                for (String key : evaluatorRequest.keySet()) {
                    if(key.equals(ExpressionConstant.NAME)){
                        expressionName = (String)evaluatorRequest.get(key);
                    }else {
                        evaluatedObjectName = key;
                        value = (Map<String, Map<String, Object>>)evaluatorRequest.get(key);
                    }
                }
            }else{
                ErrorResponse errorResponse = new ErrorResponse();
                errorResponse.setStatus(HttpStatus.BAD_REQUEST);
                errorResponse.setCause(ExpressionConstant.JSON_IS_NOT_VALID_MESSAGE);
                errorResponse.setCause(ExpressionConstant.JSON_IS_NOT_VALID_MESSAGE);
                errorResponse.setErrorCode(HttpStatus.BAD_REQUEST.toString());
                return ResponseHandler.generateExceptionResponse(errorResponse);
            }

            if(accessToken != null){
                evaluationResult = evaluateService.evaluateExpression(expressionName, value, evaluatedObjectName);
            }else {
                ErrorResponse errorResponse = new ErrorResponse();
                errorResponse.setStatus(HttpStatus.UNAUTHORIZED);
                errorResponse.setCause(ExpressionConstant.UNAUTHORIZED_USER);
                errorResponse.setCause(ExpressionConstant.UNAUTHORIZED_USER_MESSAGE);
                errorResponse.setErrorCode(HttpStatus.UNAUTHORIZED.toString());
                return ResponseHandler.generateExceptionResponse(errorResponse);
            }
            return ResponseHandler.generateExpressionEvaluatorResponse(evaluationResult, HttpStatus.OK, value);

        } catch(ExpressionException expressionException){

            Evaluate_Logger.info("ExpressionException thrown while evaluating expression : " + expressionException.getMessage());
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrorCode(expressionException.getCode());
            errorResponse.setMessage(expressionException.getMessage());
            errorResponse.setCause(expressionException.getReason());
            errorResponse.setStatus(expressionException.getHttpStatus());
            return ResponseHandler.generateExceptionResponse(errorResponse);

        }catch (Exception exception) {

            Evaluate_Logger.info("Unknown Error thrown while evaluating expression : " + exception.getMessage());
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            errorResponse.setMessage(ExpressionConstant.ERROR_EVALUATING_EXPRESSION);
            errorResponse.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            errorResponse.setCause(exception.getMessage());
            return ResponseHandler.generateExceptionResponse(errorResponse);
        }
    }

}
