package br.com.fernando.browsewords;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.google.common.collect.ArrayListMultimap;
import com.jayway.jsonpath.JsonPath;

public class BrowseWordsHtmlUnit {

    public static void main(String[] args) throws Exception {

	final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_60);
	webClient.getOptions().setJavaScriptEnabled(false);
	webClient.getOptions().setCssEnabled(false);

	final ArrayListMultimap<String, String> globalMap = ArrayListMultimap.create();

	try (webClient) {

	    final WebRequest webRequest = new WebRequest(new URL(BrowseWordsUtils.URL), HttpMethod.GET);
	    webRequest.setAdditionalHeader("Accept", "*/*");
	    webRequest.setAdditionalHeader("Content-Type", "application/json");

	    final String jsonString = webClient.<UnexpectedPage>getPage(webRequest) //
	        .getWebResponse() //
	        .getContentAsString();

	    /**
	     * <pre>
	     *    {"responses":[
	     *                      {
	     *                        "models": {
	     *                                    "classSet":[], 
	     *                                    "session":[], 
	     *                                    "set":[ 
	     *                                             {    
	     *                                               "id":306948128, 
	     *                                               "timestamp":1533897810, 
	     *                                               "_webUrl":"https://quizlet.com/306948128/english-expressions-0001-flash-cards/", 
	     *                                                "_thumbnailUrl":null, 
	     *                                                "price":null 
	     *                                             },
	     *                                             {   
	     *                                               "id": 279096228,   
	     *                                               "timestamp": 1521453734,   
	     *                                               "_webUrl": "https://quizlet.com/279096228/english-multi-word-verbs-0001-flash-cards/",   
	     *                                               "_thumbnailUrl": null,   
	     *                                               "price": null   
	     *                                             }   
	     *                                          ]    
	     *                                  } 
	     *                      }
	     *                 ] 
	     *    }
	     * </pre>
	     */
	    final List<String> urlStudySets = JsonPath.read(jsonString, "$..set[*]._webUrl");

	    for (final String urlStudySet : urlStudySets) {

		final List<HtmlSpan> spans = webClient.<HtmlPage>getPage(urlStudySet) //
		    .getByXPath("//span[contains(@class,'lang-en')]");

		spans.stream() //
		    .map(HtmlSpan::getTextContent) //
		    .collect(Collectors.toList())//
		    .forEach(w -> globalMap.put(w, urlStudySet));

		// spans.stream() //
		// .map(HtmlSpan::getTextContent) //
		// .collect(Collectors //
		// .toMap(k -> k, v -> urlStudySet));
	    }
	}

	BrowseWordsUtils.printWordsNotInSite(globalMap.asMap());
	
	BrowseWordsUtils.printRepeatedWordsInSite(globalMap.asMap());
    }
}
