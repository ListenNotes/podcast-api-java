package com.listennotes.podcast_api;

import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.json.JSONObject;

/** Response body, status, and a snapshot of case-insensitive response headers. */
public final class ApiResponse {
    private final String jsonString;
    private final int statusCode;
    private final Map<String, List<String>> headers;
    private JSONObject jsonObj;

    /** Compatibility constructor for callers using HttpURLConnection. */
    public ApiResponse(String jsonString, HttpURLConnection connection) {
        this(jsonString, connectionStatus(connection), connection.getHeaderFields());
    }

    public ApiResponse(String jsonString, int statusCode, Map<String, List<String>> headers) {
        this.jsonString = jsonString;
        this.statusCode = statusCode;
        Map<String, List<String>> snapshot = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        headers.forEach((name, values) -> { if (name != null) snapshot.put(name, List.copyOf(values)); });
        this.headers = Collections.unmodifiableMap(snapshot);
    }

    private static int connectionStatus(HttpURLConnection connection) {
        try { return connection.getResponseCode(); }
        catch (java.io.IOException e) { return -1; }
    }

    public synchronized JSONObject toJSON() {
        if (jsonObj == null) jsonObj = new JSONObject(jsonString);
        return jsonObj;
    }

    @Override public String toString() { return jsonString; }
    public int getStatusCode() { return statusCode; }
    public Map<String, List<String>> getHeaders() { return headers; }
    public String getHeader(String name) {
        List<String> values = headers.get(name);
        return values == null || values.isEmpty() ? null : values.get(0);
    }
    public Integer getFreeQuota() { return integerHeader("X-ListenAPI-FreeQuota"); }
    public Integer getUsage() { return integerHeader("X-ListenAPI-Usage"); }
    public String getNextBillingDate() { return getHeader("X-ListenAPI-NextBillingDate"); }
    public Double getLatencySeconds() {
        String value = getHeader("X-ListenAPI-Latency-Seconds");
        try { return value == null ? null : Double.valueOf(value); }
        catch (NumberFormatException e) { return null; }
    }
    private Integer integerHeader(String name) {
        String value = getHeader(name);
        try { return value == null ? null : Integer.valueOf(value); }
        catch (NumberFormatException e) { return null; }
    }
}
