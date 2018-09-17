package br.com.fernando.browsewords;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.jayway.jsonpath.JsonPath;

public class BrowseWordsHtmlUnit {
    
    private static void lookingForRepeatedWords() throws MalformedURLException, IOException {
	final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_60);
	webClient.getOptions().setJavaScriptEnabled(false);
	webClient.getOptions().setCssEnabled(false);

	// final Multimap<String, String> map = ArrayListMultimap.create();
	final ListMultimap<String, String> map = ArrayListMultimap.create();

	try (webClient) {

	    final WebRequest webRequest = new WebRequest(new URL("https://quizlet.com/webapi/3.2/feed/65138028/created-sets?perPage=100&query=&sort=alphabetical&seenCreatedSetIds=&filters%5Bsets%5D%5BisPublished%5D=true&include%5Bset%5D%5B%5D=creator"), HttpMethod.GET);
	    webRequest.setAdditionalHeader("Accept", "*/*");
	    webRequest.setAdditionalHeader("Content-Type", "application/json");

	    final String jsonString = webClient.<UnexpectedPage>getPage(webRequest) //
	        .getWebResponse() //
	        .getContentAsString();

	    /**
	     * <pre>
	     *    {"responses":[
	     *                 	    {
	     *                        "models": {
	     *                                    "classSet":[], 
	     *                 	                  "session":[], 
	     *                 	    	          "set":[ 
	     *                                             {	
	     *                                               "id":306948128, 
	     *                 	    	    	             "timestamp":1533897810, 
	     *                 	    	    	             "_webUrl":"https://quizlet.com/306948128/english-expressions-0001-flash-cards/", 
	     *                 	    	    	              "_thumbnailUrl":null, 
	     *                 	    	    	              "price":null 
	     *                                             },
	     *                                             {   
	     *                                               "id": 279096228,   
	     *                                               "timestamp": 1521453734,   
	     *                                               "_webUrl": "https://quizlet.com/279096228/english-multi-word-verbs-0001-flash-cards/",   
	     *                                               "_thumbnailUrl": null,   
	     *                                               "price": null   
	     *                                             }   
	     *                                           ]    
	     *                 	    	        } 
	     *                      }
	     *                 ] 
	     *    }
	     * </pre>
	     */
	    final List<String> urlStudySets = JsonPath.read(jsonString, "$..set[*]._webUrl");

	    for (final String urlStudySet : urlStudySets) {

		final List<HtmlSpan> spans = webClient.<HtmlPage>getPage(urlStudySet) //
		    .getByXPath("//span[contains(@class,'lang-en')]");

		final List<String> wordsPage = spans.stream() //
		    .map(elt -> elt.getTextContent()) //
		    .collect(Collectors.toList());

		wordsPage.forEach(w -> map.put(w, urlStudySet));

		// map.putAll(spans.stream() //
		// .collect(Collectors //
		// .toMap(HtmlSpan::getTextContent, (x) -> urlStudySet)));
	    }
	}

	System.out.println("Looking for repeated words ");

	for (final Map.Entry<String, Collection<String>> entry : map.asMap().entrySet()) {
	    if (entry.getValue().size() > 1) {
		System.out.println(entry.getKey() + " - " + entry.getValue());
	    }
	}
    }
    

    public static void main(String[] args) throws Exception {

	// lookingForRepeatedWords();

	System.out.println("Looking for repeated words ");

	Path path = Paths.get(BrowseWordsHtmlUnit.class.getClassLoader().getResource("words.txt").toURI());

	StringBuilder data = new StringBuilder();
	Stream<String> lines = Files.lines(path);
	
	try (lines){
	    lines.forEach(line -> data.append(line).append("\n"));
	    
	}

    }
}
