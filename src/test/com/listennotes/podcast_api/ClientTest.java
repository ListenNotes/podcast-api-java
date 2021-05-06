package com.listennotes.podcast_api;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.net.HttpURLConnection;

public final class ClientTest
{
    @Test
    public void testSetApiKey() throws IOException 
    {
        Client tester = new Client();
        HttpURLConnection con = tester.getConnection( tester.getUrl( "test" ) );
        assertEquals( null, con.getRequestProperty( "X-ListenAPI-Key" ) );

        String strApiKey = "helloWorld";
        tester.setApiKey( strApiKey );
        con = tester.getConnection( tester.getUrl( "test" ) );
        assertEquals( con.getRequestProperty( "X-ListenAPI-Key" ), strApiKey );

    }

    @Test
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
