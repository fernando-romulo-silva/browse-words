package br.com.fernando.browsewords.browsewords;

import static br.com.fernando.browsewords.util.BrowseWordsUtils.printRepeatedWordsInSite;
import static br.com.fernando.browsewords.util.BrowseWordsUtils.printWordsNotInSite;

import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.StopWatch;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.google.common.collect.ArrayListMultimap;

import br.com.fernando.browsewords.util.BrowseWordsUtils;

public class BrowseWordsJSoups {

    public static void main(String[] args) throws Exception {
	
	final ArrayListMultimap<String, String> globalMap = ArrayListMultimap.create();
	
        final var connect = Jsoup.connect(BrowseWordsUtils.URL).ignoreContentType(true);

        final var watch = new StopWatch();

        watch.start();
        
        final String jsonString = connect //
                .execute() //
                .body(); //
        
        watch.stop();
        System.out.println("URL principal, Time Elapsed: " + watch.getTime(TimeUnit.MILLISECONDS)+ " ms"); 
        watch.reset();        

        watch.start();
        
        final var urlStudySets = BrowseWordsUtils.getUrlFromJson02(jsonString);
        
        watch.stop();
        System.out.println("JsonPath Time Elapsed: " + watch.getTime(TimeUnit.MILLISECONDS) + " ms");
        watch.reset();

        watch.start();
        
        for (final var urlStudySet : urlStudySets) {

            final var document = Jsoup.parse(new URL(urlStudySet), 10000);

            final var spans = document.select("span[class$=lang-en]");

            spans.stream() //
                    .map(Element::text) //
                    .filter(x -> !"...".equals(x.trim())) //
                    // .collect(Collectors.mapping(mapper, downstream))
                    .collect(Collectors.toList())//
                    .forEach(w -> globalMap.put(w.trim(), urlStudySet));

        }
        
        watch.stop();
        System.out.println("URL with XPath: "+ urlStudySets.size() +" requests, Time Elapsed: " + watch.getTime(TimeUnit.SECONDS)+ " s");
        watch.reset();        

        printWordsNotInSite(globalMap.asMap());

        printRepeatedWordsInSite(globalMap.asMap());
    }
}
