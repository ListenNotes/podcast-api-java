package com.listennotes.podcast_api;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.net.HttpURLConnection;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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

    public void testSetApiKey() throws IOException 
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

        String strResult = tester.search( parameters );
        assertEquals( 401, tester.con.getResponseCode() );

    }

    public void testSearch() throws IOException 
    {
        Client tester = new Client();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("q", "test");
        String strResult = tester.search( parameters );
        assertEquals( 200, tester.con.getResponseCode() );
        assertEquals( tester.USER_AGENT, tester.con.getRequestProperty( "User-Agent" ) );
    }
}
