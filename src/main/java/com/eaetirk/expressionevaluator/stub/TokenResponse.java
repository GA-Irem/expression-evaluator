package com.eaetirk.expressionevaluator.stub;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {
    String access_token;
    String refresh_token;
    String token_type;
    String session_state;
    Integer expires_in;
    Integer refresh_expires_in;
    Integer not_before_policy;
}
