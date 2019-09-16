package br.com.fernando.browsewords;

import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.StopWatch;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.ArrayListMultimap;

import br.com.fernando.browsewords.util.BrowseWordsUtils;

public class BrowseWordsJSoups {

    public static void main(String[] args) throws Exception {
	
	final ArrayListMultimap<String, String> globalMap = ArrayListMultimap.create();
	
        final Connection connect = Jsoup.connect(BrowseWordsUtils.URL).ignoreContentType(true);

        final var watch = new StopWatch();

        watch.start();
        
        final String jsonString = connect //
                .execute() //
                .body(); //
        
        watch.stop();
        System.out.println("URL principal, Time Elapsed: " + watch.getTime(TimeUnit.MILLISECONDS)+ " ms"); 
        watch.reset();        

        watch.start();
        
        final List<String> urlStudySets = BrowseWordsUtils.getUrlFromJson02(jsonString);
        
        watch.stop();
        System.out.println("JsonPath Time Elapsed: " + watch.getTime(TimeUnit.MILLISECONDS) + " ms");
        watch.reset();

        watch.start();
        
        for (final String urlStudySet : urlStudySets) {

            final Document document = Jsoup.parse(new URL(urlStudySet), 10000);

            final Elements spans = document.select("span[class$=lang-en]");

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

        BrowseWordsUtils.printWordsNotInSite(globalMap.asMap());

        BrowseWordsUtils.printRepeatedWordsInSite(globalMap.asMap());
    }
}
