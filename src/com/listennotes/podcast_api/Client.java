package com.listennotes.podcast_api;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.net.*;

public final class Client
{
    public static final String BASE_URL = "https://listen-api-test.listennotes.com/api/v2";
    /* public static final String strBaseUrl = "http://tester.com/api/v2"; */
    public static final String USER_AGENT = "podcast-api-java";
    public static final String BASE_URL_PROD = "https://listen-api.listennotes.com/api/v2";
    public String API_KEY = "";
    public static HttpURLConnection con;

    public String search( Map<String, String> mapParams ) throws UnsupportedEncodingException,MalformedURLException,ProtocolException,IOException
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

    public HttpURLConnection getConnection( String strUrl ) throws MalformedURLException,IOException
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

    public String get( String strPath, Map<String, String> mapParams ) throws UnsupportedEncodingException,MalformedURLException,ProtocolException,IOException
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

    public static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException
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
}
