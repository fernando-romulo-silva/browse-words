package br.com.fernando.browsewords.pmdsite;

import static com.gargoylesoftware.htmlunit.BrowserVersion.FIREFOX_78;
import static com.gargoylesoftware.htmlunit.BrowserVersion.FIREFOX_ESR;
import static org.apache.commons.lang3.StringUtils.containsAny;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringsBetween;
import static org.apache.commons.lang3.StringUtils.trim;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ToolsConfigs {

    public static void checkStylesChecks() throws FailingHttpStatusCodeException, IOException {
	Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);

	final var webClient = new WebClient(FIREFOX_ESR);
	webClient.getOptions().setJavaScriptEnabled(false);
	webClient.getOptions().setCssEnabled(false);

	final var rulesUrls = List.of( //
			Map.entry("ANNOTATIONS", "https://checkstyle.sourceforge.io/config_annotation.html"), //
			Map.entry("BLOCKS", "https://checkstyle.sourceforge.io/config_blocks.html"), //
			Map.entry("DESIGN", "https://checkstyle.sourceforge.io/config_design.html"), //
			Map.entry("CODING", "https://checkstyle.sourceforge.io/config_coding.html"), //
			Map.entry("HEADER", "https://checkstyle.sourceforge.io/config_header.html"), //
			Map.entry("IMPORTS", "https://checkstyle.sourceforge.io/config_imports.html"), //
			Map.entry("JAVADOC", "https://checkstyle.sourceforge.io/config_javadoc.html"), //
			Map.entry("METRICS", "https://checkstyle.sourceforge.io/config_metrics.html"), //
			Map.entry("MISC", "https://checkstyle.sourceforge.io/config_misc.html"), //
			Map.entry("MODIFIER", "https://checkstyle.sourceforge.io/config_modifier.html"), //
			Map.entry("NAMING", "https://checkstyle.sourceforge.io/config_naming.html"), //
			Map.entry("REGEXP", "https://checkstyle.sourceforge.io/config_regexp.html"), //
			Map.entry("SIZES", "https://checkstyle.sourceforge.io/config_sizes.html"), //
			Map.entry("WHITE SPACES", "https://checkstyle.sourceforge.io/config_whitespace.html") //
	);

	try (webClient) {

	    for (final var ruleUrl : rulesUrls) {
		
		System.out.println(" <!-- ================================================================================================================================================== --> ");
		System.out.println(" <!-- ======== " + ruleUrl.getKey() + " ========================================================================================================================== --> ");
		System.out.println(" <!-- ================================================================================================================================================== --> ");	
		System.out.println();

		webClient.<HtmlPage>getPage(ruleUrl.getValue()) //
				.<HtmlAnchor>getByXPath("(//div/div/section/ul/li/a)") //
				.stream() //
				.skip(1) //
				.map(s -> "<module name=\"" + s.asNormalizedText() + "\" />") //
				.forEach(s -> System.out.println(s));
		
		System.out.println();
		System.out.println();
	    }
	}
    }

    public static void pmdRules() throws FailingHttpStatusCodeException, IOException {

	Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);

	final var webClient = new WebClient(FIREFOX_ESR);
	webClient.getOptions().setJavaScriptEnabled(false);
	webClient.getOptions().setCssEnabled(false);

	final var rulesUrls = List.of( //
			Map.entry("BEST PRACTICES", "https://pmd.sourceforge.io/pmd-6.50.0/pmd_rules_java_bestpractices.html"), // best practices
			Map.entry("CODE STYLE", "https://pmd.sourceforge.io/pmd-6.50.0/pmd_rules_java_codestyle.html"), // code style
			Map.entry("DESIGN", "https://pmd.sourceforge.io/pmd-6.50.0/pmd_rules_java_design.html"), // design
			Map.entry("DOCUMENTATION", "https://pmd.sourceforge.io/pmd-6.50.0/pmd_rules_java_documentation.html"), // documentation
			Map.entry("ERROR PRONE", "https://pmd.sourceforge.io/pmd-6.50.0/pmd_rules_java_errorprone.html"), // error prone
			Map.entry("MULTITHREADING", "https://pmd.sourceforge.io/pmd-6.50.0/pmd_rules_java_multithreading.html"), // multi threading
			Map.entry("PERFORMANCE", "https://pmd.sourceforge.io/pmd-6.50.0/pmd_rules_java_performance.html"), // perfomance
			Map.entry("SECURITY", "https://pmd.sourceforge.io/pmd-6.50.0/pmd_rules_java_security.html") // security
	);

	final var filters = new String[] { //
		"This rule is replaced".toLowerCase(), //
		"This rule is deprecated since".toLowerCase(), //
		"This rule has been deprecated".toLowerCase(), //
		"this rule is deprecated".toLowerCase() //
	};

	try (webClient) {

	    for (final var ruleUrl : rulesUrls) {
		
		System.out.println(" <!-- ================================================================================================================================================== --> ");
		System.out.println(" <!-- ======== " + ruleUrl.getKey() + " ========================================================================================================================== --> ");
		System.out.println(" <!-- ================================================================================================================================================== --> ");	
		System.out.println();
		
		final var text = webClient //
				.<HtmlPage>getPage(ruleUrl.getValue()) //
				.asNormalizedText();

		Stream.of(substringsBetween(text, "Since: PMD", "/>")) //
				.filter(s -> !containsAny(s.toLowerCase(), filters)) //
				.map(s -> "<rule " + trim(substringAfter(s, "<rule")) + " />") //
				.forEach(s -> System.out.println(s));

		System.out.println();
		System.out.println();
	    }

	}
    }

    public static void main(String[] args) throws Exception {
	checkStylesChecks();
    }

}
