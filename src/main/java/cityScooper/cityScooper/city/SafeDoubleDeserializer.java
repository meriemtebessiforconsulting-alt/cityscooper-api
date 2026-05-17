package cityScooper.cityScooper.city;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class SafeDoubleDeserializer extends JsonDeserializer<Double> {
    @Override
    public Double deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
    	String value = p.getText();

        if (value == null) return null;

        value = value.trim();

        if (value.equalsIgnoreCase("NC")
                || value.equalsIgnoreCase("")
                || value.equalsIgnoreCase("null")) {
            return null;
        }

        try {
            return Double.parseDouble(value);
        } catch (Exception e) {return null;}
    }
}