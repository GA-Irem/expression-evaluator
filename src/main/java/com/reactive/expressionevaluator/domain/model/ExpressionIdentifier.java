package com.reactive.expressionevaluator.domain.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document
public class ExpressionIdentifier {

    @Id
    private String identifier_id;
    private String identifier_name;
    private String identifier_value;

}
