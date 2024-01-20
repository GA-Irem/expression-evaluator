package com.reactive.expressionevaluator.domain.utility;

import com.google.gson.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JsonValidator {
    final TypeAdapter<JsonElement> strictAdapter = new Gson().getAdapter(JsonElement.class);

    public boolean isValid(String json) {
        try {
            JsonParser.parseString(json);
        } catch (JsonSyntaxException e) {
            return false;
        }
        return true;
    }

    public boolean isValidString(String json) {
        try {
            strictAdapter.fromJson(json);
        } catch (JsonSyntaxException | IOException e) {
            return false;
        }
        return true;
    }
}