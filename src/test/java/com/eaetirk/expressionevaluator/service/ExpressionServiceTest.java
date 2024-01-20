package com.eaetirk.expressionevaluator.service;

import com.eaetirk.expressionevaluator.exception.ExpressionException;
import com.eaetirk.expressionevaluator.model.ExpressionIdentifier;
import com.eaetirk.expressionevaluator.repository.ExpressionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ExpressionServiceTest {

        @InjectMocks
        private ExpressionServiceImpl expressionService;

        @Mock
        ExpressionRepository expressionRepository;

        @Test
        public void testSavingExpression(){
            // Arrange
            ExpressionIdentifier expressionIdentifier = new ExpressionIdentifier();
            String expression = "(firstName == \"JOHN\" && salary < 100) AND (address != null || address.city != \"Washington\")";

            expressionIdentifier.setName("Complex logical expression");
            expressionIdentifier.setValue(expression);

            when(expressionRepository.save(expressionIdentifier)).thenReturn(expressionIdentifier);
           // Act
            ExpressionIdentifier response = expressionService.saveExpressionIdentifier(expressionIdentifier);

            assertEquals(expressionIdentifier.getName(), response.getName());
            assertEquals(expressionIdentifier.getValue(), response.getValue());

        }

    @Test
    public void testSavingExpression_withoutName_throwException(){
        // Arrange
        ExpressionIdentifier expressionIdentifier = new ExpressionIdentifier();
        String expression = "(firstName == \"JOHN\" && salary < 100) AND (address != null || address.city != \"Washington\")";

        expressionIdentifier.setValue(expression);

        assertThrows(ExpressionException.class, () -> {
            expressionService.saveExpressionIdentifier(expressionIdentifier);
        });
    }

    @Test
    public void testSavingExpression_withAlreadyExistingExpressionName_throwException(){
        // Arrange
        ExpressionIdentifier expressionIdentifier = new ExpressionIdentifier();
        String expression = "(firstName == \"JOHN\" && salary < 100) AND (address != null || address.city != \"Washington\")";

        expressionIdentifier.setName("Complex logical expression");
        expressionIdentifier.setValue(expression);

        when(expressionRepository.findAll()).thenReturn(List.of(expressionIdentifier));

        assertThrows(ExpressionException.class, () -> {
            expressionService.saveExpressionIdentifier(expressionIdentifier);
        });

    }

}

