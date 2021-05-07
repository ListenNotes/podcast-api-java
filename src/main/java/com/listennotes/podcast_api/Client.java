package com.listennotes.podcast_api;

import com.listennotes.podcast_api.exception.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.DataOutputStream;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.LinkedList;

import java.net.URL;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;

public final class Client
{
    public static final String BASE_URL_TEST = "https://listen-api-test.listennotes.com/api/v2";
    public static final String USER_AGENT = "podcast-api-java";
    public static final String BASE_URL_PROD = "https://listen-api.listennotes.com/api/v2";
    public String API_KEY = "";
    public static HttpURLConnection con;
    public Map<String, String> requestParams;

    public String submitPodcast( Map<String, String> mapParams ) throws Exception,ListenApiException
    {
        String strResponse = this.post( "podcasts/submit", mapParams );
        return strResponse;
    }

    public String deletePodcast( Map<String, String> mapParams ) throws Exception,ListenApiException
    {
        String strId = mapParams.get( "id" );
        mapParams.remove( "id" );
        String strResponse = this.delete( "podcasts/" + strId, mapParams );
        return strResponse;
    }

    public String batchFetchEpisodes( Map<String, String> mapParams ) throws Exception,ListenApiException
    {
        String strResponse = this.post( "episodes", mapParams );
        return strResponse;
    }

    public String batchFetchPodcasts( Map<String, String> mapParams ) throws Exception,ListenApiException
    {
        String strResponse = this.post( "podcasts", mapParams );
        return strResponse;
    }

    public String fetchMyPlaylists( Map<String, String> mapParams ) throws Exception,ListenApiException
    {
        String strResponse = this.get( "playlists", mapParams );
        return strResponse;
    }

    public String fetchPlaylistById( Map<String, String> mapParams ) throws Exception,ListenApiException
    {
        String strId = mapParams.get( "id" );
        mapParams.remove( "id" );
        String strResponse = this.get( "playlists/" + strId, mapParams );
        return strResponse;
    }

    public String fetchRecommendationsForEpisode( Map<String, String> mapParams ) throws Exception,ListenApiException
    {
        String strId = mapParams.get( "id" );
        mapParams.remove( "id" );
        String strResponse = this.get( "episodes/" + strId + "/recommendations", mapParams );
        return strResponse;
    }

    public String fetchRecommendationsForPodcast( Map<String, String> mapParams ) throws Exception,ListenApiException
    {
        String strId = mapParams.get( "id" );
        mapParams.remove( "id" );
        String strResponse = this.get( "podcasts/" + strId + "/recommendations", mapParams );
        return strResponse;
    }

    public String justListen() throws Exception,ListenApiException
    {
        Map<String, String> parameters = new HashMap<>();
        String strResponse = this.get( "just_listen", parameters );
        return strResponse;
    }

    public String fetchPodcastLanguages() throws Exception,ListenApiException
    {
        Map<String, String> parameters = new HashMap<>();
        String strResponse = this.get( "languages", parameters );
        return strResponse;
    }

    public String fetchPodcastRegions() throws Exception,ListenApiException
    {
        Map<String, String> parameters = new HashMap<>();
        String strResponse = this.get( "regions", parameters );
        return strResponse;
    }

    public String fetchPodcastGenres( Map<String, String> mapParams ) throws Exception,ListenApiException
    {
        String strResponse = this.get( "genres", mapParams );
        return strResponse;
    }

    public String fetchCuratedPodcastsListById( Map<String, String> mapParams ) throws Exception,ListenApiException
    {
        String strId = mapParams.get( "id" );
        mapParams.remove( "id" );
        String strResponse = this.get( "curated_podcasts/" + strId, mapParams );
        return strResponse;
    }

    public String fetchCuratedPodcastsLists( Map<String, String> mapParams ) throws Exception,ListenApiException
    {
        String strResponse = this.get( "curated_podcasts", mapParams );
        return strResponse;
    }

    public String fetchEpisodeById( Map<String, String> mapParams ) throws Exception,ListenApiException
    {
        String strId = mapParams.get( "id" );
        mapParams.remove( "id" );
        String strResponse = this.get( "episodes/" + strId, mapParams );
        return strResponse;
    }

    public String fetchPodcastById( Map<String, String> mapParams ) throws Exception,ListenApiException
    {
        String strId = mapParams.get( "id" );
        mapParams.remove( "id" );
        String strResponse = this.get( "podcasts/" + strId, mapParams );
        return strResponse;
    }

    public String fetchBestPodcasts( Map<String, String> mapParams ) throws Exception,ListenApiException
    {
        String strResponse = this.get( "best_podcasts", mapParams );
        return strResponse;
    }

    public String typeahead( Map<String, String> mapParams ) throws Exception,ListenApiException
    {
        String strResponse = this.get( "typeahead", mapParams );
        return strResponse;
    }

    public String search( Map<String, String> mapParams ) throws Exception,ListenApiException
    {
        String strResponse = this.get( "search", mapParams );
        return strResponse;
    }

    public String getUrl( String strPath )
    {
        String strUrl = BASE_URL_TEST;
        if ( this.API_KEY.length() > 0 ) {
            strUrl = BASE_URL_PROD;
        }

        strUrl = strUrl + "/" + strPath;
        return strUrl;
    }

    public void setApiKey( String strKey )
    {
        this.API_KEY = strKey;
    }

    public HttpURLConnection getConnection( String strUrl ) throws Exception
    {
        URL url = new URL( strUrl );
        con = (HttpURLConnection) url.openConnection();

        con.setDoOutput( true );
        con.setRequestProperty( "User-Agent", USER_AGENT );
        if ( this.API_KEY.length() > 0 ) {
            con.setRequestProperty( "X-ListenAPI-Key", this.API_KEY );
        }
        con.setConnectTimeout( 5000 );
        con.setReadTimeout( 5000 );
        con.setInstanceFollowRedirects( false );
        return con;
    }

    private String post( String strPath, Map<String, String> mapParams ) throws Exception
    {
        String strUrl = getUrl( strPath );

        this.requestParams = mapParams;
        String strParameters = getParamsString( mapParams );
        strUrl = strUrl;

        HttpURLConnection con = getConnection( strUrl );
        con.setRequestMethod( "POST" );
        con.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );

        DataOutputStream out = new DataOutputStream( con.getOutputStream() );
        out.writeBytes( strParameters );
        out.flush();
        out.close();

        StringBuffer content = getResponse();

        return content.toString();
    }

    private String get( String strPath, Map<String, String> mapParams ) throws Exception
    {
        String strUrl = getUrl( strPath );
        String strParameters = getParamsString( mapParams );
        strUrl = strUrl + "?" + strParameters;

        HttpURLConnection con = getConnection( strUrl );
        con.setRequestMethod( "GET" );
        StringBuffer content = getResponse();

        return content.toString();
    }

    private String delete( String strPath, Map<String, String> mapParams ) throws Exception
    {
        String strUrl = getUrl( strPath );

        String strParameters = getParamsString( mapParams );
        strUrl = strUrl + "?" + strParameters;

        HttpURLConnection con = getConnection( strUrl );
        con.setRequestMethod( "DELETE" );

        StringBuffer content = getResponse();

        return content.toString();
    }

    private StringBuffer getResponse() throws Exception
    {
        int status = con.getResponseCode();
        processStatus( status );

        Reader streamReader = null;

        if (status > 299) {
            streamReader = new InputStreamReader( con.getErrorStream() );
        } else {
            streamReader = new InputStreamReader( con.getInputStream() );
        }

        BufferedReader in = new BufferedReader( streamReader );
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
        }
        in.close();
        con.disconnect();
        return content;
    }

    private void processStatus( int intStatus ) throws Exception
    {
        if ( intStatus == 401 ) {
            throw new AuthenticationException( "Wrong api key or your account is suspended" );
        } else if ( intStatus == 429 ) {
            throw new RateLimitException( "You use FREE plan and you exceed the quota limit" );
        } else if ( intStatus == 404 ) {
            throw new NotFoundException( "Endpoint not exist, or podcast / episode not exist" );
        } else if ( intStatus == 400 ) {
            throw new InvalidRequestException( "Something wrong on your end (client side errors),"
                                            + " e.g., missing required parameters");
        } else if ( intStatus >= 500 ) {
            throw new ListenApiException( "Error on our end (unexpected server errors)" );
        } else {
        }
    }

    public static String getParamsString(Map<String, String> params) throws Exception
    {
            StringBuilder result = new StringBuilder();

            for (Map.Entry<String, String> entry : params.entrySet()) {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                result.append("&");
            }

            String resultString = result.toString();
            return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }

    public static Map<String, String> splitQuery(URL url) throws Exception
    {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }
}
