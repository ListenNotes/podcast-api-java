package com.listennotes.podcast_api;

import com.listennotes.podcast_api.exception.*;

import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

import java.net.URL;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;


public final class Client {
    public static final String BASE_URL_TEST = "https://listen-api-test.listennotes.com/api/v2";
    public static final String BASE_URL_PROD = "https://listen-api.listennotes.com/api/v2";

    public static final String USER_AGENT = "podcast-api-java";

    protected String apiKey = "";
    protected Integer timeoutMs = 30000; // 30 seconds
    protected String userAgent = USER_AGENT;
    protected HttpURLConnection con;
    protected Map<String, String> requestParams;

    public Client() {
        // No api key. Use api mock server.
    }

    public Client(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setResponseTimeoutMs(Integer timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public ApiResponse submitPodcast(Map<String, String> mapParams) throws ListenApiException {
        return this.post("podcasts/submit", mapParams);
    }

    public ApiResponse deletePodcast(Map<String, String> mapParams) throws ListenApiException {
        String strId = mapParams.get("id");
        mapParams.remove("id");
        return this.delete("podcasts/" + strId, mapParams);
    }

    public ApiResponse batchFetchEpisodes(Map<String, String> mapParams) throws ListenApiException {
        return this.post("episodes", mapParams);
    }

    public ApiResponse batchFetchPodcasts(Map<String, String> mapParams) throws ListenApiException {
        return this.post("podcasts", mapParams);
    }

    public ApiResponse fetchMyPlaylists(Map<String, String> mapParams) throws ListenApiException {
        return this.get("playlists", mapParams);
    }

    public ApiResponse fetchPlaylistById(Map<String, String> mapParams) throws ListenApiException {
        String strId = mapParams.get("id");
        mapParams.remove("id");
        return this.get("playlists/" + strId, mapParams);
    }

    public ApiResponse fetchRecommendationsForEpisode(Map<String, String> mapParams) throws ListenApiException {
        String strId = mapParams.get("id");
        mapParams.remove("id");
        return this.get("episodes/" + strId + "/recommendations", mapParams);
    }

    public ApiResponse fetchRecommendationsForPodcast(Map<String, String> mapParams) throws ListenApiException {
        String strId = mapParams.get("id");
        mapParams.remove("id");
        return this.get("podcasts/" + strId + "/recommendations", mapParams);
    }

    public ApiResponse justListen() throws ListenApiException {
        Map<String, String> parameters = new HashMap<>();
        return this.get("just_listen", parameters);
    }

    public ApiResponse fetchPodcastLanguages() throws ListenApiException {
        Map<String, String> parameters = new HashMap<>();
        return this.get("languages", parameters);
    }

    public ApiResponse fetchPodcastRegions() throws ListenApiException {
        Map<String, String> parameters = new HashMap<>();
        return this.get("regions", parameters);
    }

    public ApiResponse fetchPodcastGenres(Map<String, String> mapParams) throws ListenApiException {
        return this.get("genres", mapParams);
    }

    public ApiResponse fetchCuratedPodcastsListById(Map<String, String> mapParams) throws ListenApiException {
        String strId = mapParams.get("id");
        mapParams.remove("id");
        return this.get("curated_podcasts/" + strId, mapParams);
    }

    public ApiResponse fetchCuratedPodcastsLists(Map<String, String> mapParams) throws ListenApiException {
        return this.get("curated_podcasts", mapParams);
    }

    public ApiResponse fetchEpisodeById(Map<String, String> mapParams) throws ListenApiException {
        String strId = mapParams.get("id");
        mapParams.remove("id");
        return this.get("episodes/" + strId, mapParams);
    }

    public ApiResponse fetchPodcastById(Map<String, String> mapParams) throws ListenApiException {
        String strId = mapParams.get("id");
        mapParams.remove("id");
        return this.get("podcasts/" + strId, mapParams);
    }

    public ApiResponse fetchBestPodcasts(Map<String, String> mapParams) throws ListenApiException {
        return this.get("best_podcasts", mapParams);
    }

    public ApiResponse typeahead(Map<String, String> mapParams) throws ListenApiException {
        return this.get("typeahead", mapParams);
    }

    public ApiResponse search(Map<String, String> mapParams) throws ListenApiException {
        return this.get("search", mapParams);
    }

    protected String getUrl(String strPath) {
        String strUrl = BASE_URL_TEST;
        if (this.apiKey != null &&this.apiKey.length() > 0) {
            strUrl = BASE_URL_PROD;
        }

        strUrl = strUrl + "/" + strPath;
        return strUrl;
    }

    public HttpURLConnection getConnection(String strUrl) throws ListenApiException {
        URL url;
        try {
            url = new URL(strUrl);
        } catch (MalformedURLException e) {
            throw new InvalidRequestException("Malformed Url");
        }
        try {
            con = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new ApiConnectionException("Failed to connect to Listen API.");
        }

        con.setDoOutput(true);
        con.setRequestProperty("User-Agent", this.userAgent);
        if (this.apiKey != null && this.apiKey.length() > 0) {
            con.setRequestProperty("X-ListenAPI-Key", this.apiKey);
        }
        con.setConnectTimeout(5000);
        con.setReadTimeout(this.timeoutMs);
        con.setInstanceFollowRedirects(false);
        return con;
    }

    private ApiResponse post(String strPath, Map<String, String> mapParams) throws ListenApiException {
        String strUrl = getUrl(strPath);

        this.requestParams = mapParams;
        String strParameters = getParamsString(mapParams);

        con = getConnection(strUrl);
        try {
            con.setRequestMethod("POST");
        } catch (ProtocolException e) {
            throw new InvalidRequestException("Unexpected protocol.");
        }
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        DataOutputStream out;
        try {
            out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(strParameters);
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new InvalidRequestException("Invalid parameters.");
        }
        return getResponse();
    }

    private ApiResponse get(String strPath, Map<String, String> mapParams) throws ListenApiException {
        String strUrl = getUrl(strPath);
        String strParameters = getParamsString(mapParams);
        strUrl = strUrl + "?" + strParameters;

        con = getConnection(strUrl);
        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            throw new InvalidRequestException("Unexpected protocol");
        }
        return getResponse();
    }

    private ApiResponse delete(String strPath, Map<String, String> mapParams) throws ListenApiException {
        String strUrl = getUrl(strPath);

        String strParameters = getParamsString(mapParams);
        strUrl = strUrl + "?" + strParameters;

        con = getConnection(strUrl);
        try {
            con.setRequestMethod("DELETE");
        } catch (ProtocolException e) {
            throw new InvalidRequestException("Unexpected protocol");
        }

        return getResponse();
    }

    private ApiResponse getResponse() throws ListenApiException {
        int status;
        try {
            status = con.getResponseCode();
        } catch (IOException e) {
            throw new ApiConnectionException("Failed to connect to Listen API servers.");
        }
        processStatus(status);

        Reader streamReader = null;

        if (status > 299) {
            streamReader = new InputStreamReader(con.getErrorStream());
        } else {
            try {
                streamReader = new InputStreamReader(con.getInputStream());
            } catch (IOException e) {
                throw new ApiConnectionException("Failed to connect to Listen API servers.");
            }
        }

        BufferedReader in = new BufferedReader(streamReader);
        String inputLine;
        StringBuilder content = new StringBuilder();
        try {
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            throw new ListenApiException("Error on our end (unexpected server errors)");
        }
        con.disconnect();

        return new ApiResponse(content.toString(), con);
    }

    private void processStatus(int intStatus) throws ListenApiException {
        if (intStatus == 401) {
            throw new AuthenticationException("Wrong api key or your account is suspended");
        } else if (intStatus == 429) {
            throw new RateLimitException("You use FREE plan and you exceed the quota limit");
        } else if (intStatus == 404) {
            throw new NotFoundException("Endpoint not exist, or podcast / episode not exist");
        } else if (intStatus == 400) {
            throw new InvalidRequestException(
                    "Something wrong on your end (client side errors)," + " e.g., missing required parameters");
        } else if (intStatus >= 500) {
            throw new ListenApiException("Error on our end (unexpected server errors)");
        }
    }

    protected static String getParamsString(Map<String, String> params) {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            try {
                result.append(encodeUrlString(entry.getKey()));
                result.append("=");
                result.append(encodeUrlString(entry.getValue()));
                result.append("&");
            } catch (UnsupportedEncodingException e) {
                // e.printStackTrace();
            }
        }

        String resultString = result.toString();
        return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
    }

    protected static Map<String, String> splitQuery(URL url) {
        Map<String, String> queryPairs = new LinkedHashMap<>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            try {
                queryPairs.put(decodeUrlString(pair.substring(0, idx)), decodeUrlString(pair.substring(idx + 1)));
            } catch (UnsupportedEncodingException e) {
                // e.printStackTrace();
            }
        }
        return queryPairs;
    }

    private static String encodeUrlString(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, "UTF-8");
    }

    private static String decodeUrlString(String value) throws UnsupportedEncodingException {
        return URLDecoder.decode(value, "UTF-8");
    }
}
