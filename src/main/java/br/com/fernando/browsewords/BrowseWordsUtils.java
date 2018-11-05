package br.com.fernando.browsewords;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class BrowseWordsUtils {

    public static final String URL = "https://quizlet.com/webapi/3.2/feed/65138028/created-sets?perPage=100&query=&sort=alphabetical&seenCreatedSetIds=&filters%5Bsets%5D%5BisPublished%5D=true&include%5Bset%5D%5B%5D=creator";

    public static void printRepeatedWordsInSite(final Map<String, Collection<String>> map) {
	System.out.println("-------------------------------------------------------------------------------------------");
	System.out.println("Looking for repeated words");

	map.entrySet().stream() //
	    .filter(e -> e.getValue().size() > 1) //
	    .forEach(e -> System.out.println(e.getKey() + " - " + e.getValue()));
    }

    public static void printWordsNotInSite(final Map<String, Collection<String>> map) throws URISyntaxException, IOException {

	System.out.println("-------------------------------------------------------------------------------------------");
	System.out.println("Print words that not in site:");

	final Path words = Paths.get(BrowseWordsUtils.class.getClassLoader().getResource("words.txt").toURI());

	final Set<String> wordsOnSite = map.keySet();

	Files.lines(words) // reading file
	    .filter(w -> !wordsOnSite.contains(w.toLowerCase())) // only words that not in site
	    .sorted() //
	    .collect(Collectors.toSet()) // remove duplicates, but disorganize
	    .forEach(System.out::println);
    }
}
