package com.listennotes.podcast_api;

import com.listennotes.podcast_api.exception.*;

import java.util.Map;
import java.util.HashMap;
import java.net.HttpURLConnection;
import java.net.URL;

import junit.framework.TestCase;

import org.json.JSONObject;
import org.json.JSONArray;

public final class ClientTest extends TestCase {
    public void testSetApiKey() throws Exception {
        Client client = new Client();
        HttpURLConnection con = client.getConnection(client.getUrl("test"));
        assertEquals(null, con.getRequestProperty("X-ListenAPI-Key"));

        String strApiKey = "helloWorld";
        client = new Client(strApiKey);
        con = client.getConnection(client.getUrl("test"));
        assertEquals(con.getRequestProperty("X-ListenAPI-Key"), strApiKey);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("q", "test");

        try {
            client.search(parameters);
        } catch (AuthenticationException e) {
            assertEquals(401, client.con.getResponseCode());
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testSearch() throws Exception {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("q", "test");
        parameters.put("sort_by_date", "1");
        ApiResponse response = tester.search(parameters);
        assertEquals(200, tester.con.getResponseCode());
        assertEquals(Client.USER_AGENT, tester.con.getRequestProperty("User-Agent"));

        JSONObject oj = response.toJSON();
        assertTrue(oj.optJSONArray("results") instanceof JSONArray);
        assertTrue(oj.optJSONArray("results").length() > 0);

        assertEquals(tester.con.getRequestMethod(), "GET");

        URL u = tester.con.getURL();
        assertEquals(u.getPath(), "/api/v2/search");

        Map<String, String> params = Client.splitQuery(u);
        assertEquals(params.get("sort_by_date"), parameters.get("sort_by_date"));
        assertEquals(params.get("q"), parameters.get("q"));
    }

    public void testTypeahead() throws Exception {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("q", "test");
        parameters.put("show_podcasts", "1");
        ApiResponse response = tester.typeahead(parameters);
        JSONObject oj = response.toJSON();
        assertTrue(oj.optJSONArray("terms").length() > 0);
        assertEquals(tester.con.getRequestMethod(), "GET");
        URL u = tester.con.getURL();
        assertEquals(u.getPath(), "/api/v2/typeahead");
        Map<String, String> params = Client.splitQuery(u);
        assertEquals(params.get("show_podcasts"), parameters.get("show_podcasts"));
        assertEquals(params.get("q"), parameters.get("q"));
    }

    public void testFetchBestPodcasts() throws Exception {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("genre_id", "23");
        ApiResponse response = tester.fetchBestPodcasts(parameters);
        JSONObject oj = response.toJSON();
        assertTrue(oj.optInt("total", 0) > 0);
        assertEquals(tester.con.getRequestMethod(), "GET");
        URL u = tester.con.getURL();
        assertEquals(u.getPath(), "/api/v2/best_podcasts");
        Map<String, String> params = Client.splitQuery(u);
        assertEquals(params.get("genre_id"), parameters.get("genre_id"));
    }

    public void testPodcastById() throws Exception {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        String id = "23";
        parameters.put("id", id);
        ApiResponse response = tester.fetchPodcastById(parameters);
        JSONObject oj = response.toJSON();
        assertTrue(oj.optJSONArray("episodes").length() > 0);
        assertEquals(tester.con.getRequestMethod(), "GET");
        URL u = tester.con.getURL();
        assertEquals(u.getPath(), "/api/v2/podcasts/" + id);
    }

    public void testEpisodeById() throws Exception {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        String id = "23";
        parameters.put("id", id);
        ApiResponse response = tester.fetchEpisodeById(parameters);
        JSONObject oj = response.toJSON();
        assertTrue(oj.optJSONObject("podcast").optString("rss").length() > 0);
        assertEquals(tester.con.getRequestMethod(), "GET");
        URL u = tester.con.getURL();
        assertEquals(u.getPath(), "/api/v2/episodes/" + id);
    }

    public void testFetchCuratedPodcastsListById() throws Exception {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        String id = "23";
        parameters.put("id", id);
        ApiResponse response = tester.fetchCuratedPodcastsListById(parameters);
        JSONObject oj = response.toJSON();
        assertTrue(oj.optJSONArray("podcasts").length() > 0);
        assertEquals(tester.con.getRequestMethod(), "GET");
        URL u = tester.con.getURL();
        assertEquals(u.getPath(), "/api/v2/curated_podcasts/" + id);
    }

    public void testFetchCuratedPodcastsLists() throws Exception {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("page", "2");
        ApiResponse response = tester.fetchCuratedPodcastsLists(parameters);
        JSONObject oj = response.toJSON();
        assertTrue(oj.optInt("total", 0) > 0);
        assertEquals(tester.con.getRequestMethod(), "GET");
        URL u = tester.con.getURL();
        assertEquals(u.getPath(), "/api/v2/curated_podcasts");
        Map<String, String> params = Client.splitQuery(u);
        assertEquals(params.get("page"), parameters.get("page"));
    }

    public void testFetchPodcastGenres() throws Exception {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("top_level_only", "1");
        ApiResponse response = tester.fetchPodcastGenres(parameters);
        JSONObject oj = response.toJSON();
        assertTrue(oj.optJSONArray("genres").length() > 0);
        assertEquals(tester.con.getRequestMethod(), "GET");
        URL u = tester.con.getURL();
        assertEquals(u.getPath(), "/api/v2/genres");
        Map<String, String> params = Client.splitQuery(u);
        assertEquals(params.get("top_level_only"), parameters.get("top_level_only"));
    }

    public void testFetchPodcastRegions() throws Exception {
        Client tester = new Client();
        ApiResponse response = tester.fetchPodcastRegions();
        JSONObject oj = response.toJSON();
        assertTrue(oj.optJSONObject("regions").length() > 0);
        assertEquals(tester.con.getRequestMethod(), "GET");
        URL u = tester.con.getURL();
        assertEquals(u.getPath(), "/api/v2/regions");
    }

    public void testFetchPodcastLanguages() throws Exception {
        Client tester = new Client();
        ApiResponse response = tester.fetchPodcastLanguages();
        JSONObject oj = response.toJSON();
        assertTrue(oj.optJSONArray("languages").length() > 0);
        assertEquals(tester.con.getRequestMethod(), "GET");
        URL u = tester.con.getURL();
        assertEquals(u.getPath(), "/api/v2/languages");
    }

    public void testJustListen() throws Exception {
        Client tester = new Client();

        ApiResponse response = tester.justListen();
        JSONObject oj = response.toJSON();
        /* System.out.println( "DBG: " + oj.toString() ); */
        assertTrue(oj.optInt("audio_length_sec", 0) > 0);
        assertEquals(tester.con.getRequestMethod(), "GET");
        URL u = tester.con.getURL();
        assertEquals(u.getPath(), "/api/v2/just_listen");
    }

    public void testFetchRecommendationsForPodcast() throws Exception {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        String id = "23";
        parameters.put("id", id);
        ApiResponse response = tester.fetchRecommendationsForPodcast(parameters);
        JSONObject oj = response.toJSON();
        assertTrue(oj.optJSONArray("recommendations").length() > 0);
        assertEquals(tester.con.getRequestMethod(), "GET");
        URL u = tester.con.getURL();
        assertEquals(u.getPath(), "/api/v2/podcasts/" + id + "/recommendations");
    }

    public void testFetchRecommendationsForEpisode() throws Exception {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        String id = "23";
        parameters.put("id", id);
        ApiResponse response = tester.fetchRecommendationsForEpisode(parameters);
        JSONObject oj = response.toJSON();
        assertTrue(oj.optJSONArray("recommendations").length() > 0);
        assertEquals(tester.con.getRequestMethod(), "GET");
        URL u = tester.con.getURL();
        assertEquals(u.getPath(), "/api/v2/episodes/" + id + "/recommendations");
    }

    public void testFetchPlaylistById() throws Exception {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        String id = "23";
        parameters.put("id", id);
        ApiResponse response = tester.fetchPlaylistById(parameters);
        JSONObject oj = response.toJSON();
        assertTrue(oj.optJSONArray("items").length() > 0);
        assertEquals(tester.con.getRequestMethod(), "GET");
        URL u = tester.con.getURL();
        assertEquals(u.getPath(), "/api/v2/playlists/" + id);
    }

    public void testFetchMyPlaylists() throws Exception {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("page", "2");
        ApiResponse response = tester.fetchMyPlaylists(parameters);
        JSONObject oj = response.toJSON();
        assertTrue(oj.optJSONArray("playlists").length() > 0);
        assertEquals(tester.con.getRequestMethod(), "GET");
        URL u = tester.con.getURL();
        assertEquals(u.getPath(), "/api/v2/playlists");
        Map<String, String> params = Client.splitQuery(u);
        assertEquals(params.get("page"), parameters.get("page"));
    }

    public void testBatchFetchPodcasts() throws Exception {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("ids", "2,222,333,4444");
        ApiResponse response = tester.batchFetchPodcasts(parameters);
        JSONObject oj = response.toJSON();
        assertTrue(oj.optJSONArray("podcasts").length() > 0);
        assertEquals(tester.con.getRequestMethod(), "POST");
        assertEquals(parameters.get("ids"), tester.requestParams.get("ids"));
        URL u = tester.con.getURL();
        assertEquals(u.getPath(), "/api/v2/podcasts");
    }

    public void testBatchFetchEpisodes() throws Exception {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("ids", "2,222,333,4444");
        ApiResponse response = tester.batchFetchEpisodes(parameters);
        JSONObject oj = response.toJSON();
        assertTrue(oj.optJSONArray("episodes").length() > 0);
        assertEquals(tester.con.getRequestMethod(), "POST");
        assertEquals(parameters.get("ids"), tester.requestParams.get("ids"));
        URL u = tester.con.getURL();
        assertEquals(u.getPath(), "/api/v2/episodes");
    }

    public void testDeletePodcast() throws Exception {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        String id = "23";
        parameters.put("id", id);
        parameters.put("reason", "User wants to delete podcast");
        ApiResponse response = tester.deletePodcast(parameters);
        JSONObject oj = response.toJSON();
        assertTrue(oj.optString("status").length() > 0);
        assertEquals(tester.con.getRequestMethod(), "DELETE");

        URL u = tester.con.getURL();
        assertEquals(u.getPath(), "/api/v2/podcasts/" + id);

        Map<String, String> params = Client.splitQuery(u);
        assertEquals(params.get("reason"), parameters.get("reason"));
    }

    public void testSubmitPodcast() throws Exception {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("rss", "http://myrss.com/rss");
        ApiResponse response = tester.submitPodcast(parameters);
        JSONObject oj = response.toJSON();
        assertTrue(oj.optString("status").length() > 0);
        assertEquals(tester.con.getRequestMethod(), "POST");
        assertEquals(parameters.get("rss"), tester.requestParams.get("rss"));
        URL u = tester.con.getURL();
        assertEquals(u.getPath(), "/api/v2/podcasts/submit");
    }
}
