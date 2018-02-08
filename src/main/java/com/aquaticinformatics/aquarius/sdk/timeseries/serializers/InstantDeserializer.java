package com.aquaticinformatics.aquarius.sdk.timeseries.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstantDeserializer implements JsonDeserializer<Instant> {

    public static final String ZoneFieldPattern = "ZZZZZ";
    public static final String DateTimePattern = "yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSSSSSS" + ZoneFieldPattern;

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern(DateTimePattern)
            .withLocale(Locale.US);

    public static final String JsonMaxValue = "MaxInstant";
    public static final String JsonMinValue = "MinInstant";
    public static final Instant MaxValue = Instant.MAX;
    public static final Instant MinValue = Instant.MIN;

    public static final String JsonMaxConcreteValue = "9999-12-31T23:59:59.9999999Z";
    public static final String JsonMinConcreteValue = "0001-01-01T00:00:00.0000000Z";
    public static final Instant MaxConcreteValue = FORMATTER.parse(JsonMaxConcreteValue, Instant::from);
    public static final Instant MinConcreteValue = FORMATTER.parse(JsonMinConcreteValue, Instant::from);

    @Override
    public Instant deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        return parse(jsonElement.getAsString());
    }

    private static String padFractionalSeconds(String text) {
        Pattern microPattern = Pattern.compile("\\.([0-9]*)([a-zA-Z+-].*)");
        Matcher matcher = microPattern.matcher(text);
        
        if(matcher.find()) {
            return text.substring(0, text.lastIndexOf('.') + 1) + (matcher.group(1) + "0000000").substring(0, 7) + matcher.group(2);
        }

        return text;
    } 

    public static Instant parse(String text) {
        text = padFractionalSeconds(text);

        if (text.equalsIgnoreCase(JsonMaxValue))
            return MaxValue;

        if (text.equalsIgnoreCase(JsonMinValue))
            return MinValue;

        return FORMATTER.parse(text, Instant::from);
    }
}
