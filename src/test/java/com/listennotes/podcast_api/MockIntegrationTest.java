package com.listennotes.podcast_api;

import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
class MockIntegrationTest {
    private final Client client = new Client(); // Fixed public mock; never read credentials or a base URL from the environment.

    @TestFactory Stream<DynamicTest> allMethodsAgainstPublicMock() throws Exception {
        return TestSupport.operations().stream().map(operation -> DynamicTest.dynamicTest(operation.getString("func"), () -> {
            ApiResponse response = TestSupport.call(client, operation, TestSupport.examples(operation));
            String name = operation.getString("operationId");
            int status = switch (name) {
                case "createPlaylist", "addPlaylistItem" -> 201;
                case "transcribeEpisode" -> 202;
                default -> 200;
            };
            assertEquals(status, response.getStatusCode());
            assertFalse(response.toJSON().isEmpty());
            if (name.equals("transcribeEpisode")) {
                assertEquals("queued", response.toJSON().getString("status"));
            } else if (name.equals("getEpisodeTranscript")) {
                assertEquals("completed", response.toJSON().getString("status"));
                assertFalse(response.toJSON().getJSONObject("download_urls").isEmpty());
            } else if (name.equals("deletePlaylist")) {
                assertEquals(200, response.getStatusCode());
                assertTrue(response.toJSON().getBoolean("deleted"));
                assertEquals(operation.getJSONObject("example_params").getString("id"), response.toJSON().getString("id"));
            }
        }));
    }
    @Test void unicodeQuery() throws Exception {
        assertTrue(client.search(Map.of("q", "café + design & art")).toJSON().has("results"));
    }
    @Test void clearNotes() throws Exception {
        assertFalse(client.updatePlaylistItemNotes(Map.of("id", "m1pe7z60bsw", "item_id", "23", "notes", "")).toJSON().isEmpty());
    }
    @Test void clearDescription() throws Exception {
        assertFalse(client.updatePlaylist(Map.of("id", "m1pe7z60bsw", "description", "")).toJSON().isEmpty());
    }
    @Test void addPodcast() throws Exception {
        assertFalse(client.addPlaylistItem(Map.of("id", "m1pe7z60bsw", "podcast_id", "4d3fe717742d4963a85562e9f84d8c79"))
                .toJSON().isEmpty());
    }
}
