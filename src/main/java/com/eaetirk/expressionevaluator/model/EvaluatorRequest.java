package com.eaetirk.expressionevaluator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluatorRequest {

    //expression name
    private String name;
    private Object customer;
}
