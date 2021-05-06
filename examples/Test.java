
import com.listennotes.podcastapi.Client;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.net.*;

class Test
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
            System.out.println( "Malformed URL: " + mue.getMessage() );
        } catch ( UnsupportedEncodingException uee ) {
            System.out.println( "UnsupportedEncoding: " + uee.getMessage() );
        } catch ( ProtocolException pe ) {
            System.out.println( "Protocol: " + pe.getMessage() );
        } catch ( IOException ioe ) {
            System.out.println( "IOException: " + ioe.getMessage() );
        }
    }
}
