package br.com.fernando.browsewords;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.common.collect.ArrayListMultimap;
import com.jayway.jsonpath.JsonPath;

public class BrowseWordsHttpComponents {

    public static void main(String[] args) throws Exception {
        
	final ArrayListMultimap<String, String> globalMap = ArrayListMultimap.create();
        
        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            final HttpGet httpget = new HttpGet(BrowseWordsUtils.URL);

            // Create a custom response handler
            final ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();

                if (status >= 200 && status < 300) {
                    final HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };

            final String jsonString = httpclient.execute(httpget, responseHandler);

            final List<String> urlStudySets = JsonPath.read(jsonString, "$..set[*]._webUrl");
            
            for (final String urlStudySet : urlStudySets) {
                
                
                
            }
            
            
        }
    }
}
