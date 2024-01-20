package com.reactive.expressionevaluator.domain.repository;

import com.reactive.expressionevaluator.domain.model.ExpressionIdentifier;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;


//
public interface ExpressionIdentifierRepository extends ReactiveMongoRepository<ExpressionIdentifier, String>  {
}

