package com.listennotes.podcast_api;

import com.sun.net.httpserver.HttpServer;
import com.listennotes.podcast_api.exception.ListenApiException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.json.JSONArray;
import org.json.JSONObject;

final class TestSupport implements AutoCloseable {
    record Request(String method, URI uri, Map<String, List<String>> headers, String body) { }
    record Reply(int status, String body, Map<String, String> headers) {
        Reply(int status, String body) { this(status, body, Map.of("Content-Type", "application/json")); }
    }
    final BlockingQueue<Request> requests = new LinkedBlockingQueue<>();
    volatile Function<Request, Reply> handler = request -> new Reply(200, "{\"ok\":true}");
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final HttpServer server;

    TestSupport() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.setExecutor(executor);
        server.createContext("/", exchange -> {
            try (exchange) {
                Request request = new Request(exchange.getRequestMethod(), exchange.getRequestURI(),
                        exchange.getRequestHeaders(), new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
                requests.add(request);
                Reply reply = handler.apply(request);
                reply.headers().forEach((name, value) -> exchange.getResponseHeaders().set(name, value));
                byte[] bytes = reply.body().getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(reply.status(), reply.status() == 204 ? -1 : bytes.length);
                if (reply.status() != 204) exchange.getResponseBody().write(bytes);
            }
        });
        server.start();
    }
    String baseUrl() { return "http://127.0.0.1:" + server.getAddress().getPort() + "/api/v2"; }
    Client client() { return new Client(null, baseUrl()); }
    Request take() throws InterruptedException {
        Request request = requests.poll(5, TimeUnit.SECONDS);
        if (request == null) throw new AssertionError("Expected an HTTP request");
        return request;
    }
    @Override public void close() { server.stop(0); executor.shutdownNow(); }

    static List<JSONObject> operations() throws IOException {
        try (var stream = TestSupport.class.getResourceAsStream("/api-contract.json")) {
            if (stream == null) throw new IOException("Missing API contract");
            JSONArray array = new JSONObject(new String(stream.readAllBytes(), StandardCharsets.UTF_8)).getJSONArray("operations");
            List<JSONObject> result = new ArrayList<>();
            for (Object entry : array) result.add((JSONObject) entry);
            return result;
        }
    }
    static Map<String, String> examples(JSONObject operation) {
        Map<String, String> parameters = new LinkedHashMap<>();
        operation.getJSONObject("example_params").toMap().forEach((name, value) -> {
            if (value != null) parameters.put(name, value.toString());
        });
        return parameters;
    }
    static ApiResponse call(Client client, JSONObject operation, Map<String, String> parameters) throws Exception {
        try { return (ApiResponse) Client.class.getMethod(operation.getString("func"), Map.class).invoke(client, parameters); }
        catch (InvocationTargetException error) {
            if (error.getCause() instanceof ListenApiException cause) throw cause;
            throw error;
        }
    }
    static Map<String, String> decode(String query) {
        Map<String, String> result = new LinkedHashMap<>();
        if (query == null || query.isEmpty()) return result;
        for (String pair : query.split("&")) {
            String[] parts = pair.split("=", 2);
            result.put(URLDecoder.decode(parts[0], StandardCharsets.UTF_8),
                    parts.length == 2 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "");
        }
        return result;
    }
}
