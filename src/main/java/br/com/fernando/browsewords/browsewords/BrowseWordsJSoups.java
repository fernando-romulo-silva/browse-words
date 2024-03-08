package br.com.fernando.browsewords.browsewords;

import static br.com.fernando.browsewords.util.BrowseWordsUtilsHtmlUnit.printRepeatedWordsInSite;
import static br.com.fernando.browsewords.util.BrowseWordsUtilsHtmlUnit.printWordsNotInSite;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;

import br.com.fernando.browsewords.util.BrowseWordsUtilsHtmlUnit;

public class BrowseWordsJSoups {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BrowseWordsJSoups.class);

    public static void main(String[] args) throws Exception {
	
	final ArrayListMultimap<String, String> globalMap = ArrayListMultimap.create();
	
        final var connect = Jsoup.connect(BrowseWordsUtilsHtmlUnit.URL)
        		.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
        		.ignoreContentType(true);

        final var watch = new StopWatch();

        watch.start();
        
        final String jsonString = connect //
                .execute() //
                .body(); //
        
        watch.stop();
        
        LOGGER.info("URL principal, Time Elapsed: {} ms", watch.getTime(TimeUnit.MILLISECONDS)); 
       
        watch.reset();        

        watch.start();
        
        final var urlStudySets = BrowseWordsUtilsHtmlUnit.getUrlFromJson02(jsonString);
        
        watch.stop();
        LOGGER.info("JsonPath Time Elapsed: {} ms", watch.getTime(TimeUnit.MILLISECONDS));
        watch.reset();

        watch.start();
        
        for (final var urlStudySet : urlStudySets) {

            final var document = Jsoup.parse(new URL(urlStudySet), 10000);

            final var spans = document.select("span[class$=lang-en]");

            spans.stream() //
                    .map(Element::text) //
                    .filter(x -> !"...".equals(x.trim())) //
                    // .collect(Collectors.mapping(mapper, downstream))
                    .toList() //
                    .forEach(w -> globalMap.put(w.trim(), urlStudySet));

        }
        
        watch.stop();
        LOGGER.info("URL with XPath: {} requests, Time Elapsed: {} s", urlStudySets.size(), watch.getTime(TimeUnit.SECONDS));
        watch.reset();        

        printWordsNotInSite(globalMap.asMap());

        printRepeatedWordsInSite(globalMap.asMap());
    }
}
