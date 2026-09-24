package com.listennotes.podcast_api;

import com.listennotes.podcast_api.exception.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** Synchronous Listen API client. Requests and credentials are isolated per client. */
public final class Client extends ApiMethods {
    public static final String BASE_URL_TEST = "https://listen-api-test.listennotes.com/api/v2";
    public static final String BASE_URL_PROD = "https://listen-api.listennotes.com/api/v2";
    public static final String USER_AGENT = "podcast-api-java";

    private final String apiKey;
    private final String baseUrl;
    private final HttpClient httpClient;
    private volatile int timeoutMs = 30000;
    private volatile String userAgent = USER_AGENT;

    /** Creates a client for the public mock server, without credentials. */
    public Client() { this(null); }

    /** A null or blank API key selects the public mock server. */
    public Client(String apiKey) {
        this(apiKey, apiKey == null || apiKey.isBlank() ? BASE_URL_TEST : BASE_URL_PROD);
    }

    /** Creates a client with an explicit API base URL, useful for local testing. */
    public Client(String apiKey, String baseUrl) {
        this.apiKey = apiKey == null || apiKey.isBlank() ? null : headerValue(apiKey);
        URI uri = URI.create(baseUrl);
        if (!("https".equals(uri.getScheme()) || "http".equals(uri.getScheme())) || uri.getHost() == null
                || uri.getRawQuery() != null || uri.getRawFragment() != null || uri.getRawUserInfo() != null) {
            throw new IllegalArgumentException("Base URL must be an HTTP(S) URL without credentials, query, or fragment");
        }
        this.baseUrl = baseUrl.replaceAll("/+$", "");
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NEVER).build();
    }

    /** Sets a positive request timeout in milliseconds; defaults to 30 seconds. */
    public void setResponseTimeoutMs(Integer timeoutMs) {
        if (timeoutMs == null || timeoutMs <= 0) throw new IllegalArgumentException("Timeout must be positive");
        this.timeoutMs = timeoutMs;
    }

    /** Overrides the User-Agent for subsequent requests. */
    public void setUserAgent(String userAgent) { this.userAgent = headerValue(userAgent); }

    private static String headerValue(String value) {
        if (value == null || value.isBlank() || value.chars().anyMatch(c -> c < 32 || c > 126)) {
            throw new IllegalArgumentException("Header value must contain printable ASCII characters");
        }
        return value;
    }

    protected String getUrl(String path) { return baseUrl + "/" + path.replaceFirst("^/", ""); }

    /**
     * Creates a configured legacy connection. API methods use java.net.http instead.
     * @deprecated Prefer the endpoint methods; callers must close this connection themselves.
     */
    @Deprecated(since = "3.0.0")
    public HttpURLConnection getConnection(String url) throws ListenApiException {
        try {
            HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("User-Agent", userAgent);
            if (apiKey != null) connection.setRequestProperty("X-ListenAPI-Key", apiKey);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(timeoutMs);
            connection.setInstanceFollowRedirects(false);
            return connection;
        } catch (IOException e) {
            throw new ApiConnectionException("Failed to create an API connection", e);
        } catch (IllegalArgumentException | ClassCastException e) {
            throw new InvalidRequestException("Invalid HTTP URL", e);
        }
    }

    @Override
    protected ApiResponse requestApi(String method, String path, String[] pathNames,
            String[] queryNames, Map<String, String> parameters) throws ListenApiException {
        Map<String, String> remaining = new TreeMap<>();
        if (parameters != null) {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                if (entry.getKey() == null) throw new InvalidRequestException("Parameter names cannot be null");
                if (entry.getValue() != null) remaining.put(entry.getKey(), entry.getValue());
            }
        }
        for (String name : pathNames) {
            String value = remaining.remove(name);
            if (value == null || value.isBlank()) throw new InvalidRequestException("Missing path parameter: " + name);
            String encoded = encode(value).replace("+", "%20").replace("*", "%2A");
            if (value.equals(".") || value.equals("..")) encoded = encoded.replace(".", "%2E");
            path = path.replace("{" + name + "}", encoded);
        }
        boolean hasBody = method.equals("POST") || method.equals("PUT");
        Set<String> querySet = Set.copyOf(Arrays.asList(queryNames));
        Map<String, String> query = new LinkedHashMap<>();
        Map<String, String> body = new LinkedHashMap<>();
        remaining.forEach((name, value) -> (hasBody && !querySet.contains(name) ? body : query).put(name, value));
        String encodedQuery = getParamsString(query);
        String url = getUrl(path) + (encodedQuery.isEmpty() ? "" : "?" + encodedQuery);
        try {
            HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofMillis(timeoutMs)).header("User-Agent", userAgent)
                    .header("Accept", "application/json");
            if (apiKey != null) request.header("X-ListenAPI-Key", apiKey);
            if (hasBody) {
                request.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                        .method(method, HttpRequest.BodyPublishers.ofString(getParamsString(body), StandardCharsets.UTF_8));
            } else {
                request.method(method, HttpRequest.BodyPublishers.noBody());
            }
            HttpResponse<String> response = httpClient.send(request.build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            ApiResponse result = new ApiResponse(response.body(), response.statusCode(), response.headers().map());
            checkStatus(result);
            return result;
        } catch (IOException e) {
            throw new ApiConnectionException("Failed to connect to Listen API", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiConnectionException("API request interrupted", e);
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Invalid request parameters", e);
        }
    }

    private static void checkStatus(ApiResponse response) throws ListenApiException {
        int status = response.getStatusCode();
        if (status >= 200 && status < 300) return;
        String message = "Listen API returned HTTP " + status;
        switch (status) {
            case 400 -> throw new InvalidRequestException(message, response);
            case 401 -> throw new AuthenticationException(message, response);
            case 403 -> throw new PermissionDeniedException(message, response);
            case 404 -> throw new NotFoundException(message, response);
            case 429 -> throw new RateLimitException(message, response);
            default -> throw new ListenApiException(message, response);
        }
    }

    private static String encode(String value) { return URLEncoder.encode(value, StandardCharsets.UTF_8); }

    protected static String getParamsString(Map<String, String> parameters) {
        return parameters.entrySet().stream().filter(entry -> entry.getValue() != null)
                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue())).collect(Collectors.joining("&"));
    }

    protected static Map<String, String> splitQuery(URL url) {
        Map<String, String> result = new LinkedHashMap<>();
        if (url.getQuery() == null || url.getQuery().isEmpty()) return result;
        for (String pair : url.getQuery().split("&")) {
            String[] parts = pair.split("=", 2);
            result.put(URLDecoder.decode(parts[0], StandardCharsets.UTF_8),
                    parts.length == 2 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "");
        }
        return result;
    }
}
