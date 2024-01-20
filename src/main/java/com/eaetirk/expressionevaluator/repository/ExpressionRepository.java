package com.eaetirk.expressionevaluator.repository;
import com.eaetirk.expressionevaluator.model.ExpressionIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpressionRepository extends JpaRepository<ExpressionIdentifier, String>{
}
