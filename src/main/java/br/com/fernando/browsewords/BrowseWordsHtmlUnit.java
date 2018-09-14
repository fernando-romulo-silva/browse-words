package br.com.fernando.browsewords;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static void main(String[] args) throws Exception {

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

        for (final Map.Entry<String, Collection<String>> entry : map.asMap().entrySet()) {
            if (entry.getValue().size() > 1) {
                System.out.println(entry.getKey() + " - " + entry.getValue());
            }
        }

        // no longer - [https://quizlet.com/306948128/english-expressions-0001-flash-cards/, https://quizlet.com/305720741/english-multi-word-verbs-0002-flash-cards/]
        // stretch - [https://quizlet.com/305720293/english-words-0025-flash-cards/, https://quizlet.com/306947342/english-words-0026-flash-cards/]
        // decline - [https://quizlet.com/273850041/english-words-0013-flash-cards/, https://quizlet.com/304762009/english-words-0024-flash-cards/]
        // figure out - [https://quizlet.com/279096228/english-multi-word-verbs-0001-flash-cards/, https://quizlet.com/305720741/english-multi-word-verbs-0002-flash-cards/]
        // fuss - [https://quizlet.com/276796282/english-words-0014-flash-cards/, https://quizlet.com/304236634/english-words-0020-flash-cards/]
        // distrust - [https://quizlet.com/304236634/english-words-0020-flash-cards/, https://quizlet.com/304762009/english-words-0024-flash-cards/]
    }
}
