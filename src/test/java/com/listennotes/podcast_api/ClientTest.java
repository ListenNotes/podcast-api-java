package com.listennotes.podcast_api;

import com.listennotes.podcast_api.exception.*;

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.net.HttpURLConnection;
import java.net.URL;;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.json.JSONObject;
import org.json.JSONArray;

public final class ClientTest extends TestCase
{
    public ClientTest( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( ClientTest.class );
    }

    public void testClient()
    {
        assertTrue( true );
    }

    public void testSetApiKey() throws Exception
    {
        Client tester = new Client();
        HttpURLConnection con = tester.getConnection( tester.getUrl( "test" ) );
        assertEquals( null, con.getRequestProperty( "X-ListenAPI-Key" ) );

        String strApiKey = "helloWorld";
        tester.setApiKey( strApiKey );
        con = tester.getConnection( tester.getUrl( "test" ) );
        assertEquals( con.getRequestProperty( "X-ListenAPI-Key" ), strApiKey );

        Map<String, String> parameters = new HashMap<>();
        parameters.put("q", "test");

        try {
            String strResult = tester.search( parameters );
        } catch ( AuthenticationException e ) {
            assertEquals( 401, tester.con.getResponseCode() );
        } catch ( Exception e ) {
            assertTrue( false );
        }
    }

    public void testSearch() throws Exception
    {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        parameters.put( "q", "test" );
        parameters.put( "sort_by_date", "1" );
        String strResponse = tester.search( parameters );
        assertEquals( 200, tester.con.getResponseCode() );
        assertEquals( tester.USER_AGENT, tester.con.getRequestProperty( "User-Agent" ) );

        JSONObject oj = new JSONObject( strResponse );
        assertTrue( oj.optJSONArray( "results" ) instanceof JSONArray );
        assertTrue( oj.optJSONArray( "results" ).length() > 0 );

        assertEquals( tester.con.getRequestMethod(), "GET" );

        URL u = tester.con.getURL();
        assertEquals( u.getPath(), "/api/v2/search" );

        Map<String, String> params = tester.splitQuery( u );
        assertEquals( params.get( "sort_by_date" ), parameters.get( "sort_by_date" ) );
        assertEquals( params.get( "q" ), parameters.get( "q" ) );
        /* System.out.println( "URL: " + tester.con.getURL( ).toString() ); */
    }

    public void testTypeahead() throws Exception
    {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("q", "test");
        parameters.put("show_podcasts", "1");
        String strResponse = tester.typeahead( parameters );
        JSONObject oj = new JSONObject( strResponse );
        assertTrue( oj.optJSONArray( "terms" ).length() > 0 );
        assertEquals( tester.con.getRequestMethod(), "GET" );
        URL u = tester.con.getURL();
        assertEquals( u.getPath(), "/api/v2/typeahead" );
        Map<String, String> params = tester.splitQuery( u );
        assertEquals( params.get( "show_podcasts" ), parameters.get( "show_podcasts" ) );
        assertEquals( params.get( "q" ), parameters.get( "q" ) );
    }

    public void testFetchBestPodcasts() throws Exception
    {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("genre_id", "23");
        String strResponse = tester.fetchBestPodcasts( parameters );
        JSONObject oj = new JSONObject( strResponse );
        assertTrue( oj.optInt( "total", 0 ) > 0 );
        assertEquals( tester.con.getRequestMethod(), "GET" );
        URL u = tester.con.getURL();
        assertEquals( u.getPath(), "/api/v2/best_podcasts" );
        Map<String, String> params = tester.splitQuery( u );
        assertEquals( params.get( "genre_id" ), parameters.get( "genre_id" ) );
    }

    public void testPodcastById() throws Exception
    {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        String id = "23";
        parameters.put("id", id );
        String strResponse = tester.fetchPodcastById( parameters );
        JSONObject oj = new JSONObject( strResponse );
        assertTrue( oj.optJSONArray( "episodes" ).length() > 0 );
        assertEquals( tester.con.getRequestMethod(), "GET" );
        URL u = tester.con.getURL();
        assertEquals( u.getPath(), "/api/v2/podcasts/" + id );
    }

    public void testEpisodeById() throws Exception
    {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        String id = "23";
        parameters.put("id", id );
        String strResponse = tester.fetchEpisodeById( parameters );
        JSONObject oj = new JSONObject( strResponse );
        /* JSONObject podcast new JSONObject( oj */
        assertTrue( oj.optJSONObject( "podcast" ).optString("rss").length() > 0 );
        assertEquals( tester.con.getRequestMethod(), "GET" );
        URL u = tester.con.getURL();
        assertEquals( u.getPath(), "/api/v2/episodes/" + id );
    }

    public void testFetchCuratedPodcastsLists() throws Exception
    {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("page", "2");
        String strResponse = tester.fetchCuratedPodcastsLists( parameters );
        JSONObject oj = new JSONObject( strResponse );
        assertTrue( oj.optInt( "total", 0 ) > 0 );
        assertEquals( tester.con.getRequestMethod(), "GET" );
        URL u = tester.con.getURL();
        assertEquals( u.getPath(), "/api/v2/curated_podcasts" );
        Map<String, String> params = tester.splitQuery( u );
        assertEquals( params.get( "page" ), parameters.get( "page" ) );
    }

    public void testFetchPodcastGenres() throws Exception
    {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("top_level_only", "1");
        String strResponse = tester.fetchPodcastGenres( parameters );
        JSONObject oj = new JSONObject( strResponse );
        assertTrue( oj.optJSONArray( "genres" ).length() > 0 );
        assertEquals( tester.con.getRequestMethod(), "GET" );
        URL u = tester.con.getURL();
        assertEquals( u.getPath(), "/api/v2/genres" );
        Map<String, String> params = tester.splitQuery( u );
        assertEquals( params.get( "top_level_only" ), parameters.get( "top_level_only" ) );
    }

    public void testFetchPodcastRegions() throws Exception
    {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        String strResponse = tester.fetchPodcastRegions( parameters );
        JSONObject oj = new JSONObject( strResponse );
        assertTrue( oj.optJSONObject( "regions" ).length() > 0 );
        assertEquals( tester.con.getRequestMethod(), "GET" );
        URL u = tester.con.getURL();
        assertEquals( u.getPath(), "/api/v2/regions" );
    }

    public void testFetchPodcastLanguages() throws Exception
    {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("top_level_only", "1");
        String strResponse = tester.fetchPodcastLanguages( parameters );
        JSONObject oj = new JSONObject( strResponse );
        assertTrue( oj.optJSONArray( "languages" ).length() > 0 );
        assertEquals( tester.con.getRequestMethod(), "GET" );
        URL u = tester.con.getURL();
        assertEquals( u.getPath(), "/api/v2/languages" );
    }
}
