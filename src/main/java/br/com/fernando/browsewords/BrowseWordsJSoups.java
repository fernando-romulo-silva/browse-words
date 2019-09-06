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

import br.com.fernando.browsewords.util.BrowseWordsUtils;

public class BrowseWordsJSoups {

    public static void main(String[] args) throws Exception {
        final Connection connect = Jsoup.connect(BrowseWordsUtils.URL);

        connect.ignoreContentType(true);

        final ArrayListMultimap<String, String> globalMap = ArrayListMultimap.create();

        final String jsonString = connect //
                .execute() //
                .body(); //

        final List<String> urlStudySets = BrowseWordsUtils.getUrlFromJson02(jsonString);

        for (final String urlStudySet : urlStudySets) {

            final Document document = Jsoup.parse(new URL(urlStudySet), 10000);

            final Elements spans = document.select("span[class$=lang-en]");

            spans.stream() //
                    .map(Element::text) //
                    .filter(x -> !"...".equals(x)) //
                    // .collect(Collectors.mapping(mapper, downstream))
                    .collect(Collectors.toList())//
                    .forEach(w -> globalMap.put(w, urlStudySet));

        }

        BrowseWordsUtils.printWordsNotInSite(globalMap.asMap());

        BrowseWordsUtils.printRepeatedWordsInSite(globalMap.asMap());
    }
}
