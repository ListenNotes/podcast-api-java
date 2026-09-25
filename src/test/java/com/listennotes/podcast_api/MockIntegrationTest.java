package com.listennotes.podcast_api;

import java.util.List;
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
            assertTrue(List.of(200, 201).contains(response.getStatusCode()));
            assertFalse(response.toJSON().isEmpty());
            if (operation.getString("operationId").equals("deletePlaylist")) {
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
