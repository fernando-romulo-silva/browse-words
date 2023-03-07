package br.com.fernando.browsewords.util;

import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.RegExUtils.removePattern;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.trim;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import br.com.fernando.browsewords.checkbase.CheckBaseWordHtmlUnit;

public class BrowseWordsUtilsHtmlUnit {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrowseWordsUtilsHtmlUnit.class);

    public static final String URL = "https://quizlet.com/webapi/3.2/feed/65138028/created-sets?perPage=200&query=&sort=alphabetical&seenCreatedSetIds=&filters%5Bsets%5D%5BisPublished%5D=true&include%5Bset%5D%5B%5D=creator";

    public static void printRepeatedWordsInSite(final Map<String, Collection<String>> map) {
	LOGGER.info("-------------------------------------------------------------------------------------------");
	LOGGER.info("Looking for repeated words");

	map.entrySet() //
			.stream() //
			.filter(e -> e.getValue().size() > 1) //
			.forEach(e -> System.out.println(e.getKey() + " - " + e.getValue()));
    }

    public static void printWordsNotInSite(final Map<String, Collection<String>> map) throws URISyntaxException, IOException {

	LOGGER.info("-------------------------------------------------------------------------------------------");
	LOGGER.info("Print words that not in site:");

	final Path words = Paths.get(BrowseWordsUtilsHtmlUnit.class.getClassLoader().getResource("txt-files/multi-words.txt").toURI());

	final Set<String> wordsOnSite = map.keySet() //
			.stream() //
			// .map(s -> StringUtils.removeStart(s, "(")) //
			.collect(toSet());

	try (final var linesStream = Files.lines(words)) {
	    
	    final var newWords = linesStream // reading file
			    .filter(s -> isNotBlank(s)) //
			    .map(s -> trim(removePattern(s, "\\(.*"))) //
			    .filter(w -> !wordsOnSite.contains(trim(split(w, ':')[0].toLowerCase()))) // only words that not in site
			    .distinct() //
			    .collect(partitioningBy(s -> StringUtils.contains(s, SPACE)));
	    
	    final var newWordsSimple = newWords.get(false);

	    for (final var string : newWordsSimple) {
		LOGGER.info(CheckBaseWordHtmlUnit.conjugation(string));
	    }

	    newWords.get(true).forEach(LOGGER::info);

	}
    }

    public static final List<String> getUrlFromJson01(final String jsonString) {
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
	return JsonPath.read(jsonString, "$..set[*]._webUrl");
    }

    public static final List<String> getUrlFromJson02(final String jsonString) {

	final var om = new ObjectMapper();

	om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

	final var c = Configuration.builder() //
			.mappingProvider(new JacksonMappingProvider()) //
			.jsonProvider(new JacksonJsonNodeJsonProvider(om)) //
			.build();

	final var context = JsonPath.using(c).parse(jsonString);

	final var arrayNote = (ArrayNode) context.read("$..set[*]._webUrl");

	final var result = new ArrayList<String>();

	for (final var jsonNode : arrayNote) {
	    result.add(jsonNode.textValue());
	}

	return result;
    }
}
