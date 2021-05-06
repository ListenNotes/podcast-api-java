
import com.listennotes.podcastapi.Client;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.net.*;

public final class Sample
{
    public static void main(String args[])
    {
        try {
            Client objClient = new Client();

            Map<String, String> parameters = new HashMap<>();
            parameters.put("q", "val");

            String strResponse = objClient.search( parameters );

            System.out.println( "RESPONSE LENGTH: " + strResponse.length() );

        } catch ( MalformedURLException mue ) {
            System.out.println( "Malformed URL Exception: " + mue.getMessage() );
        } catch ( UnsupportedEncodingException uee ) {
            System.out.println( "UnsupportedEncodingException: " + uee.getMessage() );
        } catch ( ProtocolException pe ) {
            System.out.println( "ProtocolException: " + pe.getMessage() );
        } catch ( IOException ioe ) {
            System.out.println( "IOException: " + ioe.getMessage() );
        }
    }
}
