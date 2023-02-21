package br.com.fernando.browsewords.browsewords;

import static br.com.fernando.browsewords.util.BrowseWordsUtilsHtmlUnit.printRepeatedWordsInSite;
import static br.com.fernando.browsewords.util.BrowseWordsUtilsHtmlUnit.printWordsNotInSite;
import static javax.xml.xpath.XPathConstants.NODESET;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.NodeList;

import com.google.common.collect.ArrayListMultimap;

import br.com.fernando.browsewords.util.BrowseWordsUtilsHtmlUnit;

public class BrowseWordsHttpComponents {

    public static void main(String[] args) throws Exception {

        final var globalMap = ArrayListMultimap.<String, String>create();
        final var httpclient = HttpClients.createDefault();
        
        try (httpclient) {
            
            final var httpget = new HttpGet(BrowseWordsUtilsHtmlUnit.URL);

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

            final var jsonString = httpclient.execute(httpget, responseHandler);

            final var documentBuilder = createDocumentBuilder();
            final var xPath = XPathFactory.newInstance().newXPath();

            final var urlStudySets = BrowseWordsUtilsHtmlUnit.getUrlFromJson01(jsonString);

            for (final String urlStudySet : urlStudySets) {
                // html parse don't work
                final var doc = documentBuilder.parse(httpclient.execute(new HttpGet(urlStudySet), responseHandler));

                final var nodes = (NodeList) xPath.evaluate("//span[contains(@class,'lang-en')]", doc, NODESET);

                System.out.println(nodes);
            }
        }

        printWordsNotInSite(globalMap.asMap());

        printRepeatedWordsInSite(globalMap.asMap());
    }

    private static DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
	
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	// to be compliant, completely disable DOCTYPE declaration:
	factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
	// or completely disable external entities declarations:
	factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
	factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
	// or prohibit the use of all protocols by external entities:
	factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
	factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
	// or disable entity expansion but keep in mind that this doesn't prevent fetching external entities
	// and this solution is not correct for OpenJDK < 13 due to a bug: https://bugs.openjdk.java.net/browse/JDK-8206132
	factory.setExpandEntityReferences(false);
	
	return factory.newDocumentBuilder();
    }
}
