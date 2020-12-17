package br.com.fernando.browsewords;

import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.NodeList;

import com.google.common.collect.ArrayListMultimap;

import br.com.fernando.browsewords.util.BrowseWordsUtils;

public class BrowseWordsHttpComponents {

    public static void main(String[] args) throws Exception {

        final ArrayListMultimap<String, String> globalMap = ArrayListMultimap.create();
        final CloseableHttpClient httpclient = HttpClients.createDefault();
        
        try (httpclient) {
            
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

            final var documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            final var xPath = XPathFactory.newInstance().newXPath();

            final List<String> urlStudySets = BrowseWordsUtils.getUrlFromJson01(jsonString);

            for (final String urlStudySet : urlStudySets) {
                // html parse don't work
                final var doc = documentBuilder.parse(httpclient.execute(new HttpGet(urlStudySet), responseHandler));

                final var nodes = (NodeList) xPath.evaluate("//span[contains(@class,'lang-en')]", doc, XPathConstants.NODESET);

                System.out.println(nodes);
            }
        }

        BrowseWordsUtils.printWordsNotInSite(globalMap.asMap());

        BrowseWordsUtils.printRepeatedWordsInSite(globalMap.asMap());
    }
}
