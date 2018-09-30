package br.com.fernando.browsewords;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
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

	final ArrayListMultimap<String, String> map = ArrayListMultimap.create();

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
		    .forEach(w -> map.put(w, urlStudySet));

		// spans.stream() //
		// .map(HtmlSpan::getTextContent) //
		// .collect(Collectors //
		// .toMap(k -> k, v -> urlStudySet));

	    }
	}

	System.out.println("-------------------------------------------------------------------------------------------");
	System.out.println("Looking for repeated words");

	map.asMap().entrySet().stream() //
	    .filter(e -> e.getValue().size() > 1) //
	    .forEach(e -> System.out.println(e.getKey() + " - " + e.getValue()));

	System.out.println("-------------------------------------------------------------------------------------------");
	System.out.println("Check if words on 'words.txt' are on the site");

	final Path words = Paths.get(BrowseWordsHtmlUnit.class.getClassLoader().getResource("words.txt").toURI());

	final Set<String> wordsOnSite = map.keySet();

	Files.lines(words) // reading file
	    .filter(w -> !wordsOnSite.contains(w.toLowerCase())) // only words that not in site
	    .sorted() //
	    .collect(Collectors.toSet()) // remove duplicates, but disorganize
	    .forEach(System.out::println);
    }
}
