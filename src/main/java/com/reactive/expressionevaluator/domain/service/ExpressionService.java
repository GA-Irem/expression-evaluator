package com.reactive.expressionevaluator.domain.service;

import com.reactive.expressionevaluator.domain.exception.ExpressionException;
import com.reactive.expressionevaluator.domain.model.ExpressionIdentifier;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ExpressionService {
    public Mono<ExpressionIdentifier> patchEmployee(String expressionId, ExpressionIdentifier expressionIdentifier) throws ExpressionException;

    public Mono<ExpressionIdentifier> createEmployee(ExpressionIdentifier expressionIdentifier) throws ExpressionException;

    public Mono<ExpressionIdentifier> getEmployee(String expressionIdentifierID) throws ExpressionException;

    public Flux<ExpressionIdentifier> getAllEmployees(ExpressionIdentifier expressionIdentifier);

    public void deleteEmployee(String id) throws ExpressionException;
}
