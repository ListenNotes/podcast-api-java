package com.listennotes.podcast_api;

import com.listennotes.podcast_api.exception.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.tools.ToolProvider;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class ClientTest {
    @TestFactory Stream<DynamicTest> everyGeneratedMethodMatchesContract() throws Exception {
        var operations = TestSupport.operations();
        assertEquals(31, operations.size());
        return operations.stream().map(operation -> DynamicTest.dynamicTest(operation.getString("func"), () -> {
            try (var server = new TestSupport()) {
                Map<String, String> parameters = new HashMap<>();
                Map<String, String> expectedQuery = new HashMap<>();
                Map<String, String> expectedBody = new HashMap<>();
                String expectedPath = "/api/v2" + operation.getString("path");
                for (Object raw : operation.getJSONArray("parameters")) {
                    JSONObject parameter = (JSONObject) raw;
                    String name = parameter.getString("name");
                    String value = "value-" + name;
                    parameters.put(name, value);
                    switch (parameter.getString("in")) {
                        case "path" -> expectedPath = expectedPath.replace("{" + name + "}", value);
                        case "query" -> expectedQuery.put(name, value);
                        case "body" -> expectedBody.put(name, value);
                        default -> fail("Unknown parameter location");
                    }
                }
                Map<String, String> immutable = Map.copyOf(parameters);
                assertTrue(TestSupport.call(server.client(), operation, immutable).toJSON().getBoolean("ok"));
                TestSupport.Request request = server.take();
                assertEquals(operation.getString("method"), request.method());
                assertEquals(expectedPath, request.uri().getRawPath());
                assertEquals(expectedQuery, TestSupport.decode(request.uri().getRawQuery()));
                assertEquals(expectedBody, TestSupport.decode(request.body()));
                assertEquals(parameters, immutable);
                assertEquals(List.of(Client.USER_AGENT), request.headers().get("User-Agent"));
                assertNull(request.headers().get("X-ListenAPI-Key"));
            }
        }));
    }

    @Test void encodingAndEmptyFields() throws Exception {
        try (var server = new TestSupport()) {
            Client client = server.client();
            client.updatePlaylistItemNotes(Map.of("id", "list/+ ?#é", "item_id", "23/&", "notes", ""));
            var request = server.take();
            assertEquals("/api/v2/playlists/list%2F%2B%20%3F%23%C3%A9/items/23%2F%26", request.uri().getRawPath());
            assertEquals("notes=", request.body());
            assertNull(request.uri().getRawQuery());
            assertEquals("PUT", request.method());
            assertTrue(request.headers().get("Content-Type").get(0).startsWith("application/x-www-form-urlencoded"));
            client.updatePlaylist(Map.of("id", "..", "description", ""));
            assertEquals("/api/v2/playlists/%2E%2E", server.take().uri().getRawPath());
            Map<String, String> params = new HashMap<>(Map.of("q", "café + &/?", "language", ""));
            params.put("offset", null);
            client.search(params);
            request = server.take();
            assertEquals("language=&q=caf%C3%A9+%2B+%26%2F%3F", request.uri().getRawQuery());
            assertEquals("", request.body());
            client.createPlaylist(Map.of("name", "café + &", "description", ""));
            assertEquals("description=&name=caf%C3%A9+%2B+%26", server.take().body());
            client.deletePlaylistItem(Map.of("id", "list", "item_id", "23"));
            request = server.take();
            assertEquals("DELETE", request.method());
            assertEquals("", request.body());
            assertNull(request.uri().getRawQuery());
        }
    }

    @Test void deletePlaylistEncodesIdAndPreservesResponsesWithoutRetries() throws Exception {
        try (var server = new TestSupport()) {
            Client client = new Client("test-key", server.baseUrl());
            Map<String, String> parameters = Map.of("id", "list/+ ?#é");
            String body = new JSONObject().put("id", parameters.get("id")).put("deleted", true).toString();
            server.handler = request -> new TestSupport.Reply(200, body, Map.of("X-ListenAPI-Usage", "12"));
            ApiResponse response = client.deletePlaylist(parameters);
            assertEquals(200, response.getStatusCode());
            assertEquals(body, response.toString());
            assertEquals(parameters.get("id"), response.toJSON().getString("id"));
            assertTrue(response.toJSON().getBoolean("deleted"));
            assertEquals(12, response.getUsage());
            var request = server.take();
            assertEquals("DELETE", request.method());
            assertEquals("/api/v2/playlists/list%2F%2B%20%3F%23%C3%A9", request.uri().getRawPath());
            assertNull(request.uri().getRawQuery());
            assertEquals("", request.body());
            assertNull(request.headers().get("Content-Type"));
            assertEquals(List.of("test-key"), request.headers().get("X-ListenAPI-Key"));
            assertEquals(Map.of("id", "list/+ ?#é"), parameters);

            Map<Integer, Class<? extends ListenApiException>> errors = Map.of(
                    401, AuthenticationException.class, 403, PermissionDeniedException.class,
                    404, NotFoundException.class, 429, RateLimitException.class, 500, ListenApiException.class);
            for (var entry : errors.entrySet()) {
                server.handler = ignored -> new TestSupport.Reply(entry.getKey(), "{\"error\":\"Cannot delete playlist\"}",
                        Map.of("X-ListenAPI-Usage", "13"));
                ListenApiException error = assertThrows(entry.getValue(), () -> client.deletePlaylist(parameters));
                assertEquals(entry.getKey(), error.getStatusCode());
                assertEquals("Cannot delete playlist", error.getResponse().toJSON().getString("error"));
                assertEquals(13, error.getResponse().getUsage());
                assertEquals(1, server.requests.size());
                server.take();
            }
        }
    }

    @Test void instanceSettingsAndConcurrentCallsAreIndependent() throws Exception {
        try (var server = new TestSupport()) {
            Client first = new Client("first-key", server.baseUrl());
            Client second = new Client("second-key", server.baseUrl());
            first.setUserAgent("first/1.0");
            var executor = Executors.newFixedThreadPool(4);
            try {
                var calls = new ArrayList<java.util.concurrent.Future<ApiResponse>>();
                for (int i = 0; i < 8; i++) {
                    Client client = i % 2 == 0 ? first : second;
                    calls.add(executor.submit(() -> { return client.justListen(); }));
                }
                for (var call : calls) assertEquals(200, call.get(10, TimeUnit.SECONDS).getStatusCode());
                for (int i = 0; i < 8; i++) {
                    var request = server.take();
                    String key = request.headers().get("X-ListenAPI-Key").get(0);
                    assertEquals(key.equals("first-key") ? "first/1.0" : Client.USER_AGENT,
                            request.headers().get("User-Agent").get(0));
                }
            } finally { executor.shutdownNow(); }
        }
    }

    @Test void bodyMethodsKeepDeclaredQueryParametersOutOfTheForm() throws Exception {
        try (var server = new TestSupport()) {
            server.client().requestApi("POST", "/future/{id}", new String[] {"id"}, new String[] {"cursor"},
                    Map.of("id", "a/b", "cursor", "next + é", "notes", ""));
            var request = server.take();
            assertEquals("/api/v2/future/a%2Fb", request.uri().getRawPath());
            assertEquals("cursor=next+%2B+%C3%A9", request.uri().getRawQuery());
            assertEquals("notes=", request.body());
        }
    }

    @Test void invalidParametersFailBeforeConnecting() throws Exception {
        try (var server = new TestSupport()) {
            Client client = server.client();
            for (String missing : List.of("id", "item_id")) {
                Map<String, String> params = new HashMap<>(Map.of("id", "list", "item_id", "23"));
                params.remove(missing);
                assertTrue(assertThrows(InvalidRequestException.class, () -> client.deletePlaylistItem(params))
                        .getMessage().contains(missing));
            }
            assertThrows(InvalidRequestException.class, () -> client.fetchPodcastById(Map.of("id", " ")));
            for (Map<String, String> parameters : List.of(Map.<String, String>of(), Map.of("id", ""), Map.of("id", " "))) {
                assertTrue(assertThrows(InvalidRequestException.class, () -> client.deletePlaylist(parameters))
                        .getMessage().contains("id"));
            }
            assertThrows(InvalidRequestException.class, () -> client.deletePlaylist(null));
            Map<String, String> nullId = new HashMap<>();
            nullId.put("id", null);
            assertThrows(InvalidRequestException.class, () -> client.deletePlaylist(nullId));
            assertThrows(IllegalArgumentException.class, () -> client.setResponseTimeoutMs(0));
            assertThrows(IllegalArgumentException.class, () -> client.setResponseTimeoutMs(null));
            assertThrows(IllegalArgumentException.class, () -> client.setUserAgent("bad\r\nvalue"));
            assertThrows(IllegalArgumentException.class, () -> new Client("bad\nkey"));
            assertThrows(IllegalArgumentException.class, () -> new Client(null, "file:///tmp/a"));
            assertTrue(server.requests.isEmpty());
            assertEquals(Client.BASE_URL_TEST + "/search", new Client("  ").getUrl("search"));
            assertEquals(Client.BASE_URL_PROD + "/search", new Client("key").getUrl("search"));
        }
    }

    @Test void responseStatusBodyHeadersAndLegacyOverloads() throws Exception {
        try (var server = new TestSupport()) {
            server.handler = request -> new TestSupport.Reply(201, "{\n\"name\":\"café\"\n}", Map.of(
                    "X-ListenAPI-Usage", "19231", "X-ListenAPI-FreeQuota", "25000",
                    "X-ListenAPI-Latency-Seconds", "0.056", "X-ListenAPI-NextBillingDate", "2026-09-26"));
            ApiResponse result = server.client().createPlaylist(Map.of("name", "café"));
            assertEquals(201, result.getStatusCode());
            assertEquals("{\n\"name\":\"café\"\n}", result.toString());
            assertEquals("café", result.toJSON().getString("name"));
            assertEquals(19231, result.getUsage());
            assertEquals(25000, result.getFreeQuota());
            assertEquals(0.056, result.getLatencySeconds());
            assertEquals("2026-09-26", result.getNextBillingDate());
            assertEquals("19231", result.getHeader("x-LISTENapi-USAGE"));
            assertThrows(UnsupportedOperationException.class, () -> result.getHeaders().clear());
            server.handler = request -> new TestSupport.Reply(204, "");
            ApiResponse empty = server.client().fetchPodcastLanguages();
            assertEquals("", empty.toString());
            assertNull(empty.getFreeQuota());
            assertNull(empty.getUsage());
            assertNull(empty.getNextBillingDate());
            assertNull(empty.getLatencySeconds());
            assertEquals(204, server.client().fetchPodcastRegions().getStatusCode());
        }
    }

    @TestFactory Stream<DynamicTest> httpFailuresKeepTheirResponseAndNeverFollowRedirects() {
        Map<Integer, Class<? extends ListenApiException>> errors = Map.of(
                400, InvalidRequestException.class, 401, AuthenticationException.class, 403, PermissionDeniedException.class,
                404, NotFoundException.class, 429, RateLimitException.class, 500, ListenApiException.class,
                302, ListenApiException.class, 418, ListenApiException.class);
        return errors.entrySet().stream().map(entry -> DynamicTest.dynamicTest("HTTP " + entry.getKey(), () -> {
            try (var server = new TestSupport()) {
                server.handler = request -> new TestSupport.Reply(entry.getKey(), "{\"error\":\"exact café reason\"}",
                        Map.of("Location", server.baseUrl() + "/redirect", "X-ListenAPI-Usage", "12"));
                ListenApiException error = assertThrows(entry.getValue(), () -> server.client().justListen());
                assertEquals(entry.getKey(), error.getStatusCode());
                assertEquals("exact café reason", error.getResponse().toJSON().getString("error"));
                assertEquals(12, error.getResponse().getUsage());
                assertEquals(1, server.requests.size());
            }
        }));
    }

    @Test void timeoutsAndInterruptionRetainCause() throws Exception {
        try (var server = new TestSupport()) {
            CountDownLatch release = new CountDownLatch(1);
            server.handler = request -> {
                try { release.await(5, TimeUnit.SECONDS); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                return new TestSupport.Reply(200, "{}");
            };
            try {
                Client client = server.client();
                client.setResponseTimeoutMs(500);
                var error = assertThrows(ApiConnectionException.class, client::justListen);
                assertInstanceOf(java.net.http.HttpTimeoutException.class, error.getCause());
                assertNull(error.getResponse());
                server.take();
                AtomicBoolean interrupted = new AtomicBoolean();
                Thread thread = new Thread(() -> {
                    try { server.client().justListen(); }
                    catch (ApiConnectionException e) {
                        interrupted.set(Thread.currentThread().isInterrupted() && e.getCause() instanceof InterruptedException);
                    } catch (ListenApiException e) { throw new AssertionError(e); }
                });
                thread.start();
                server.take();
                thread.interrupt();
                thread.join(5000);
                assertFalse(thread.isAlive());
                assertTrue(interrupted.get());
            } finally { release.countDown(); }
        }
    }

    @Test @SuppressWarnings("deprecation")
    void legacyConnectionAndResponseConstructorsRemainUsable() throws Exception {
        try (var server = new TestSupport()) {
            Client client = new Client("legacy-key", server.baseUrl());
            var connection = client.getConnection(server.baseUrl() + "/search");
            try {
                assertTrue(connection.getDoOutput());
                assertFalse(connection.getInstanceFollowRedirects());
                try (var input = connection.getInputStream()) {
                    ApiResponse response = new ApiResponse(new String(input.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8), connection);
                    assertEquals(200, response.getStatusCode());
                    assertTrue(response.toJSON().getBoolean("ok"));
                }
                assertEquals(List.of("legacy-key"), server.take().headers().get("X-ListenAPI-Key"));
            } finally { connection.disconnect(); }
        }
    }

    @Test void readmeExamplesCompileAgainstTheBuiltJar(@TempDir Path temp) throws Exception {
        String readme = Files.readString(Path.of(System.getProperty("sdk.root"), "README.md"));
        var matcher = Pattern.compile("```java\\n(.*?)\\n```", Pattern.DOTALL).matcher(readme);
        List<String> arguments = new ArrayList<>(List.of("--release", "17", "-Xlint:all", "-Werror", "-classpath",
                System.getProperty("sdk.compile.classpath"), "-d", temp.toString()));
        int count = 0;
        while (matcher.find()) {
            String name = "Example" + count++;
            Path file = temp.resolve(name + ".java");
            Files.writeString(file, matcher.group(1).replace("public class Example", "public class " + name));
            arguments.add(file.toString());
        }
        assertEquals(32, count);
        assertEquals(0, ToolProvider.getSystemJavaCompiler().run(null, null, null, arguments.toArray(String[]::new)));
    }
}
