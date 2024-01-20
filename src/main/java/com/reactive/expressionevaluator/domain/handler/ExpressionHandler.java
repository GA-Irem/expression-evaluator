package com.reactive.expressionevaluator.domain.handler;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface ExpressionHandler {

    Mono<ServerResponse> getAllExpressions(ServerRequest request);
    Mono<ServerResponse> getExpression(ServerRequest request);
    Mono<ServerResponse> saveExpression(ServerRequest request);
    Mono<ServerResponse> deleteExpression(ServerRequest request);

    Mono<ServerResponse> deleteAllExpressions(ServerRequest request);
}
