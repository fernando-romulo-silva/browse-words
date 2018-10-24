package br.com.fernando.browsewords;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
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
	final Connection connect = Jsoup.connect(BrowseWordsConsts.URL);

	connect.ignoreContentType(true);

	final ArrayListMultimap<String, String> map = ArrayListMultimap.create();

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
	        .forEach(w -> map.put(w, urlStudySet));

	}

	System.out.println("-------------------------------------------------------------------------------------------");
	System.out.println("Looking for repeated words");

	map.asMap().entrySet().stream() //
	    .filter(e -> e.getValue().size() > 1) //
	    .forEach(e -> System.out.println(e.getKey() + " - " + e.getValue()));
	// .forEach(System.out::println);

	System.out.println("-------------------------------------------------------------------------------------------");
	System.out.println("Check if words on 'words.txt' are on the site");

	final Path words = Paths.get(BrowseWordsJSoups.class.getClassLoader().getResource("words.txt").toURI());

	final Set<String> wordsOnSite = map.keySet();

	Files.lines(words) // reading file
	    .filter(w -> !wordsOnSite.contains(w.toLowerCase())) // only words that not in site
	    .sorted() //
	    .collect(Collectors.toSet()) // remove duplicates, but disorganize
	    .forEach(System.out::println);
    }

}
