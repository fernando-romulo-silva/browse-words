package br.com.fernando.browsewords;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.jayway.jsonpath.JsonPath;

public class BrowseWordsHtmlUnit {

    public static void main(String[] args) throws Exception {

        final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_60);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);

        final Map<String, String> map = new HashMap<>();

        try (webClient) {

            final WebRequest webRequest = new WebRequest(new URL("https://quizlet.com/webapi/3.2/feed/65138028/created-sets?perPage=100&query=&sort=alphabetical&seenCreatedSetIds=&filters%5Bsets%5D%5BisPublished%5D=true&include%5Bset%5D%5B%5D=creator"), HttpMethod.GET);
            webRequest.setAdditionalHeader("Accept", "*/*");
            webRequest.setAdditionalHeader("Content-Type", "application/json");

            final WebResponse response = webClient.<UnexpectedPage>getPage(webRequest).getWebResponse();
            final String jsonString = response.getContentAsString();

            /**
             * <pre>
             *    {"responses":[
             *    	{"models": 
             *    		{"classSet":[], 
             *    		 "session":[], 
             *    		 "set":[{	
             *                    	   "id":306948128, 
             *    			    "timestamp":1533897810, 
             *    			    "_webUrl":"https://quizlet.com/306948128/english-expressions-0001-flash-cards/", 
             *    			    "_thumbnailUrl":null, 
             *    			    "price":null 
             *    			]} 
             *    		} 
             *        }] 
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
            }
        }

        System.out.println(map);
    }
}
