package com.eaetirk.expressionevaluator.util;

import com.eaetirk.expressionevaluator.constant.ExpressionConstant;
import com.eaetirk.expressionevaluator.exception.ExpressionException;
import com.eaetirk.expressionevaluator.stub.TokenResponse;
import com.google.gson.Gson;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * This is a utility class which is used to fetch system Token form IAM service.
 * Following properties are to be added in application.properties file of each service that uses this utility. <br>
 * iam.url = API URL to fetch the token <br>
 * iam.client.id = client-id required to fetch the token <br>
 * iam.grant.type = grant type required to fetch the token <br>
 * iam.client.secret.path = path where the client secret mounted
 */
@Component("systemTokenStore")
public class SystemTokenStore {


    @Value("${iam.client.secret.path}")
    private String iamClientSecretPath;


    @Value("${auth.enabled}")
    private boolean isAuthEnabled;


    @Value("${stub.keycloak.path}")
    private String stubKeycloakResponsePath;

    @Setter
    private String accessToken = null;
    private String refreshToken = null;
    
    private final AtomicBoolean isTokenValid = new AtomicBoolean(true);

    private static final Logger Expression_Logger = LoggerFactory.getLogger(SystemTokenStore.class);

    @Retryable(value = {HttpClientErrorException.class}, maxAttempts = 3, backoff = @Backoff(200))
    public String getAccessToken(String username, String password, String grant_type, String client_id, String client_secret){
        Expression_Logger.debug("Inside getAccessToken method ");
        if(!isAuthEnabled){
            Expression_Logger.debug("Authentication and Authorization is disabled, returning");
            return null;
        }
        waitForTokenRefresh();
        if(null == accessToken){
            Expression_Logger.debug("Access token is null");
            getAccessTokenFromIam( username, password, grant_type, client_id, client_secret);
        }
        Expression_Logger.debug("Exiting getAccessToken method ");
        return accessToken;
    }

    private void getAccessTokenFromIam(String username, String password, String grant_type, String client_id, String client_secret) {
        try{
        accessToken = null;
        getRequestBody(username, password,grant_type,client_id, client_secret);

        Expression_Logger.info("Sending POST Request to fetch system access token");

        TokenResponse tokenResponse = new TokenResponse();
        Gson gson = new Gson();
        try{
            Expression_Logger.debug("Reading Security Role Map file ");
            InputStream resource = new ClassPathResource(stubKeycloakResponsePath).getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
            tokenResponse = gson.fromJson(reader, TokenResponse.class);


            Expression_Logger.debug("Reading Security Role Map successful");
        } catch (IOException e) {
            Expression_Logger.error("Failed to Read Security Role Map from file : " + e.getMessage());
        }
        Expression_Logger.debug("Converting access token response to HashMap");
        String jsonTokenResp = gson.toJson(tokenResponse);
        ResponseEntity<String> response = new ResponseEntity<>(jsonTokenResp,HttpStatus.OK);
        TokenResponse tokenResp = gson.fromJson(response.getBody(), TokenResponse.class);

        if(HttpStatus.OK == response.getStatusCode()){
            Expression_Logger.info("System access token fetched successfully");
            accessToken = tokenResp.getAccess_token();
            refreshToken = tokenResp.getRefresh_token();
        }
        
    }catch (Exception e){
        if(e instanceof HttpClientErrorException){
            throw new ExpressionException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), e.toString(),  HttpStatus.INTERNAL_SERVER_ERROR);
        }else if(e instanceof ResourceAccessException){
            throw new ExpressionException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }else{
            throw new ExpressionException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }
        Expression_Logger.debug("Exiting getAccessTokenFromIam method");
    }

    private MultiValueMap<String, String> getRequestBody( String username, String password, String grant_type, String client_id, String client_secret) {
        MultiValueMap<String, String > requestBody = new LinkedMultiValueMap<>();
        requestBody.add(ExpressionConstant.CLIENT_ID, client_id);
        requestBody.add(ExpressionConstant.IAM_USERNAME,username);
        requestBody.add(ExpressionConstant.IAM_PASSWORD, password);
        if(null == client_secret){
            Expression_Logger.info("IAM_CLIENT_SECRET environment variable is null, getting clientSecret value from IAM file.");
            client_secret = getClientSecret(iamClientSecretPath);
        }
        requestBody.add(ExpressionConstant.CLIENT_SECRET, client_secret);
        Expression_Logger.debug("Request to fetch new access token ");
        requestBody.add(ExpressionConstant.GRANT_TYPE, grant_type);
        Expression_Logger.debug("Exiting getRequestBody method");
        return requestBody;
    }

    private String getClientSecret(String iamClientSecretPath) {
        Expression_Logger.debug("Inside getClientSecret method. iamClient Secret Path : " + iamClientSecretPath);
        String clientSecret = null;
        try{
            Expression_Logger.debug("Reading client secret ");
            InputStream resource = new ClassPathResource(iamClientSecretPath).getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
            clientSecret = reader.lines().collect(Collectors.joining());
            Expression_Logger.debug("Reading client secret read successful");
        } catch (IOException e) {
            Expression_Logger.error("Failed to read client secret from file : " + e.getMessage());
        }
        Expression_Logger.debug("Exiting getClientSecret method. ");
        return clientSecret;
    }

    private void waitForTokenRefresh() {
        if (!isTokenValid.get()){
            synchronized (this){
                while(!isTokenValid.get()){
                    try{
                        wait();
                        Expression_Logger.debug("Waiting for token to be refreshed");
                    }catch (InterruptedException e){
                        Expression_Logger.error(e.getMessage());
                    }
                }
                notifyAll();
            }
        }
    }
    
}
