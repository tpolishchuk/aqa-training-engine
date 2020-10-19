package org.lrp.aqa_training_engine.utils;

import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static java.util.stream.Collectors.joining;

@Component
public class UrlEncoderUtil {

    public String convertToEncodedUrl(Map<String, String> params) {
        return params.keySet().stream()
                     .map(key -> key + "=" + encodeValue(params.get(key)))
                     .collect(joining("&", "", ""));
    }

    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unable to encode value " + value, e);
        }
    }
}
