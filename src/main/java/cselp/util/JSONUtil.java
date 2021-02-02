package cselp.util;


import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONString;

import java.util.Iterator;

/**
 * Utilities class, contains JSON manipulation methods
 */
public class JSONUtil {

    /**
     * Converts input JSONObject to valid JSON string, that length no more than maxSize.
     * Superfluous JSON fields ignored.
     * @param json JSONObject
     * @param maxSize maximum result string size
     * @return valid json string
     */
    public static String jsonToString(JSONObject json, long maxSize) {
        try {
            maxSize--; // '}' char
            Iterator keys = json.keys();
            StringBuilder sb = new StringBuilder("{");
            maxSize--; //'{' char
            while (keys.hasNext()) {
                String entry = "";
                if (sb.length() > 1) {
                    entry = entry + ',';
                }
                Object o = keys.next();
                entry = entry + JSONObject.quote(o.toString(), true) + ':' +
                        valueToString(json.opt(o.toString()), maxSize, true);
                if (entry.length() < maxSize) {
                    sb.append(entry);
                    maxSize -= entry.length();
                }
            }
            sb.append('}');
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Converts input JSONArray to valid JSON string, that length no more than maxSize.
     * Superfluous JSON fields ignored.
     * @param json JSONArray
     * @param maxSize maximum result string size
     * @return valid json string
     * @throws JSONException if any
     */
    public static String jsonToString(JSONArray json, long maxSize) throws JSONException {
        try {
            return '[' + join(json, maxSize, ",") + ']';
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Join JSONArray entries up to specified maxSize
     * @param json JSONArray
     * @param maxSize maximum result string size
     * @param separator A string that will be inserted between the elements.
     * @return valid json string
     * @throws JSONException if any
     */
    public static String join(JSONArray json, long maxSize, String separator) throws JSONException {
        int len = json.length();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < len; i += 1) {
            if (i > 0) {
                sb.append(separator);
                maxSize -= separator.length();
            }
            String value = valueToString(json.get(i), maxSize, true);
            if (value.length() < maxSize) {
                sb.append(value);
                maxSize -= value.length();
            }
        }
        return sb.toString();
    }

    /**
     * Converts input Object value to string.
     * Takes into account a class of object - f.e. JSONObject, JSONArray, JSONString
     * @param value input
     * @param maxSize maximum result string size
     * @param escapeForwardSlash should forward flash be escaped always
     * @return result string
     * @throws JSONException
     */
    static String valueToString(Object value, long maxSize, boolean escapeForwardSlash) throws JSONException {

        if (value == null || value.equals(JSONObject.NULL)) {
            return "null";
        }
        if (value instanceof JSONString) {
            try {
                return  ((JSONString)value).toJSONString();
            } catch (Exception e) {
                throw new JSONException(e);
            }
        }
        if (value instanceof Number) {
            return JSONObject.numberToString((Number) value);
        }
        if (value instanceof Boolean) {
            return value.toString();
        }
        else if (value instanceof JSONObject) {
            return jsonToString((JSONObject)value, maxSize);
        }
        else if (value instanceof JSONArray) {
            return jsonToString((JSONArray)value, maxSize);
        }
        return JSONObject.quote(value.toString(), escapeForwardSlash);
    }
}
