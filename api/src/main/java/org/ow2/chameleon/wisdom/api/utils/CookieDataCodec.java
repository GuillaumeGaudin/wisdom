package org.ow2.chameleon.wisdom.api.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

/**
 * CookieDataCodec and CookieDataCodecTest are imported from Play Framework.
 * 
 * Enables us to use the same sessions as Play Framework if
 * the secret is the same.
 * 
 * Also really important because we want to make sure that our client
 * side session mechanism is widely used and stable.
 * We don't want to reinvent 
 * the wheel of securely encoding / decoding and signing cookie data.
 * 
 * All praise goes to Play Framework and their awesome work.
 * //TODO this should be a service.
 */
public class CookieDataCodec {
    /**
     * @param map  the map to decode data into.
     * @param data the data to decode.
     * @throws java.io.UnsupportedEncodingException
     */
    public static void decode(Map<String, String> map, String data) throws UnsupportedEncodingException {
        String[] keyValues = data.split("&");
        for (String keyValue : keyValues) {
            String[] splitted = keyValue.split("=", 2);
            if (splitted.length == 2) {
                map.put(URLDecoder.decode(splitted[0], "utf-8"), URLDecoder.decode(splitted[1], "utf-8"));
            }
        }
    }

    /**
     * @param map the data to encode.
     * @return the encoded data.
     * @throws java.io.UnsupportedEncodingException
     */
    public static String encode(Map<String, String> map) throws UnsupportedEncodingException {
        StringBuilder data = new StringBuilder();
        String separator = "";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue() != null) {
                data.append(separator)
                        .append(URLEncoder.encode(entry.getKey(), "utf-8"))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), "utf-8"));
                separator = "&";
            }
        }
        return data.toString();
    }

    /**
     * Constant time for same length String comparison, to prevent timing attacks
     */
    public static boolean safeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        } else {
            char equal = 0;
            for (int i = 0; i < a.length(); i++) {
                equal |= a.charAt(i) ^ b.charAt(i);
            }
            return equal == 0;
        }
    }
}
