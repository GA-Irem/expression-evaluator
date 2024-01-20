package com.reactive.expressionevaluator.domain.router;

import com.reactive.expressionevaluator.domain.handler.ExpressionHandler;
import com.reactive.expressionevaluator.domain.model.ExpressionIdentifier;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

public class ExpressionRouter {

        @Bean
        @RouterOperations(
                {
                        @RouterOperation(
                                method = RequestMethod.GET,
                                path = "/expression/{identifier_name}",
                                operation =
                                @Operation(
                                        description = "Get expression by identifier_name common router",
                                        operationId = "getExpression",
                                        tags = "expression",
                                        responses = {
                                                @ApiResponse(
                                                        responseCode = "200",
                                                        description = "Get expression by identifier_name response",
                                                        content = {
                                                                @Content(
                                                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                                        schema = @Schema(implementation = ExpressionIdentifier.class))
                                                        }),
                                                @ApiResponse(responseCode = "404", description = "Expression not found")
                                        },
                                        parameters = {
                                                @Parameter(in = ParameterIn.PATH, name = "identifier_name")}
                                )),
                        @RouterOperation(
                                method = RequestMethod.POST,
                                path = "/expression",
                                operation =
                                @Operation(
                                        description = "POST expression common router",
                                        operationId = "saveExpression",
                                        tags = "expression",
                                        responses = {
                                                @ApiResponse(
                                                        responseCode = "201",
                                                        description = "CREATE expression",
                                                        content = {
                                                                @Content(
                                                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                                        schema = @Schema(implementation = ExpressionIdentifier.class))
                                                        }),
                                                @ApiResponse(responseCode = "404", description = "Expression not found")
                                        })),
                        @RouterOperation(
                                method = RequestMethod.PATCH,
                                path = "/expression/{identifier_name}",
                                operation =
                                @Operation(
                                        description = "PATCH expression common router",
                                        operationId = "patchExpression",
                                        tags = "expression",
                                        responses = {
                                                @ApiResponse(
                                                        responseCode = "200",
                                                        description = "PATCH expression by identifier_name response",
                                                        content = {
                                                                @Content(
                                                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                                        schema = @Schema(implementation = ExpressionIdentifier.class))
                                                        }),
                                                @ApiResponse(responseCode = "404", description = "Expression not found")
                                        },
                                        parameters = {
                                                @Parameter(in = ParameterIn.PATH, name = "identifier_name")}
                                )),
                        @RouterOperation(
                                method = RequestMethod.DELETE,
                                path = "/expression/{identifier_name}",
                                operation =
                                @Operation(
                                        description = "PATCH expression common router",
                                        operationId = "deleteExpression",
                                        tags = "expression",
                                        responses = {
                                                @ApiResponse(
                                                        responseCode = "204",
                                                        description = "DELETE expression by identifier_name response"
                                                ),
                                                @ApiResponse(responseCode = "404", description = "Expression not found")
                                        },
                                        parameters = {
                                                @Parameter(in = ParameterIn.PATH, name = "identifier_name")}
                                ))
                })
        RouterFunction<ServerResponse> routeExpression(ExpressionHandler handler) {
        return route()
                .path("/expression",
                        builder -> builder
                                .nest(accept(APPLICATION_JSON).or(contentType(APPLICATION_JSON)).or(accept(TEXT_EVENT_STREAM)),
                                        nestedBuilder -> nestedBuilder
                                                .GET("/{identifier_name}", handler::getExpression)
                                                .GET(handler::getAllExpressions)
                                                .POST(handler::saveExpression)
                                )
                                .DELETE("/{identifier_name}", handler::deleteExpression)
                                .DELETE(handler::deleteAllExpressions)
                ).build();
    }

}
