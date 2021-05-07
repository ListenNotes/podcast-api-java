package com.listennotes.podcast_api;

import com.listennotes.podcast_api.exception.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.BufferedReader;

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
    public static final String BASE_URL = "https://listen-api-test.listennotes.com/api/v2";
    public static final String USER_AGENT = "podcast-api-java";
    public static final String BASE_URL_PROD = "https://listen-api.listennotes.com/api/v2";
    public String API_KEY = "";
    public static HttpURLConnection con;

    public String fetchEpisodeById( Map<String, String> mapParams ) throws Exception
    {
        String strId = mapParams.get( "id" );
        mapParams.remove( "id" );
        String strResponse = this.get( "episodes/" + strId, mapParams );
        return strResponse;
    }

    public String fetchPodcastById( Map<String, String> mapParams ) throws Exception
    {
        String strId = mapParams.get( "id" );
        mapParams.remove( "id" );
        String strResponse = this.get( "podcasts/" + strId, mapParams );
        return strResponse;
    }

    public String fetchBestPodcasts( Map<String, String> mapParams ) throws Exception
    {
        String strResponse = this.get( "best_podcasts", mapParams );
        return strResponse;
    }

    public String typeahead( Map<String, String> mapParams ) throws Exception
    {
        String strResponse = this.get( "typeahead", mapParams );
        return strResponse;
    }

    public String search( Map<String, String> mapParams ) throws Exception
    {
        String strResponse = this.get( "search", mapParams );
        return strResponse;
    }

    public String getUrl( String strPath )
    {
        String strUrl = BASE_URL;
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
        /* con.setRequestProperty( "Content-Type", "application/json" ); */
        /* String contentType = con.getHeaderField( "Content-Type"); */
        con.setConnectTimeout( 5000 );
        con.setReadTimeout( 5000 );
        con.setInstanceFollowRedirects( false );
        return con;
    }

    public String get( String strPath, Map<String, String> mapParams ) throws Exception
    {
        String strUrl = getUrl( strPath );

        /* Map<String, String> parameters = new HashMap<>(); */
        /* parameters.put("q", "val"); */
        String strParameters = getParamsString( mapParams );

        strUrl = strUrl + "?" + strParameters;
        /* System.out.println( "URL: " + strUrl ); */

        HttpURLConnection con = getConnection( strUrl );
        con.setRequestMethod( "GET" );

        /* DataOutputStream out = new DataOutputStream( con.getOutputStream() ); */
        /* System.out.println( "Params: " + strParameters ); */
        /* out.writeBytes( strParameters ); */
        /* out.flush(); */
        /* out.close(); */

        int status = con.getResponseCode();
        processStatus( status );

        /* System.out.println( "Status: " + status ); */

        Reader streamReader = null;

        if (status > 299) {
            streamReader = new InputStreamReader( con.getErrorStream() );
        } else {
            streamReader = new InputStreamReader( con.getInputStream() );
        }
        /* System.exit( 0 ); */

        BufferedReader in = new BufferedReader( streamReader );
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
        }
        in.close();

        con.disconnect();

        return content.toString();
    }

    public void processStatus( int intStatus ) throws Exception
    {
        if ( intStatus == 401 ) {
            throw new AuthenticationException( "Wrong api key or your account is suspended" );
        } else if ( intStatus == 400 ) {
            throw new InvalidRequestException( "Something wrong on your end (client side errors),"
                                            + " e.g., missing required parameters");
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
