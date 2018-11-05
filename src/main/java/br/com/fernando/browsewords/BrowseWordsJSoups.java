package br.com.fernando.browsewords;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.ArrayListMultimap;
import com.jayway.jsonpath.JsonPath;

public class BrowseWordsJSoups {

    public static void main(String[] args) throws Exception {
	final Connection connect = Jsoup.connect(BrowseWordsUtils.URL);

	connect.ignoreContentType(true);

	final ArrayListMultimap<String, String> globalMap = ArrayListMultimap.create();

	final String jsonString = connect //
	    .execute() //
	    .body(); //

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

	    final Document document = Jsoup.parse(new URL(urlStudySet), 10000);

	    final Elements spans = document.select("span[class$=lang-en]");

	    spans.stream() //
	        .map(Element::text) //
	        .collect(Collectors.toList())//
	        .forEach(w -> globalMap.put(w, urlStudySet));

	}

	BrowseWordsUtils.printWordsNotInSite(globalMap.asMap());

	BrowseWordsUtils.printRepeatedWordsInSite(globalMap.asMap());
    }

}
