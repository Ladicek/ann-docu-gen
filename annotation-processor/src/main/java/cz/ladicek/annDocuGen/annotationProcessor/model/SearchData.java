package cz.ladicek.annDocuGen.annotationProcessor.model;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.util.List;

public final class SearchData {
    private final String name;
    private final String type;
    private final String description;
    private final String fqn;
    private final List<String> tokens;

    public SearchData(DocumentedClass documentedClass) {
        this.name = documentedClass.simpleName;
        this.type = documentedClass.isUnit ? "Unit" : "Service";
        this.description = Jsoup.clean(documentedClass.javadoc.firstSentence(), Whitelist.none());
        this.fqn = documentedClass.fullName.toString();
        this.tokens = Splitter
                .on(CharMatcher.JAVA_LETTER_OR_DIGIT.or(CharMatcher.is('\'')).negate()) // "'" is common in English
                .trimResults()
                .omitEmptyStrings()
                .splitToList(name + " " + Jsoup.clean(documentedClass.javadoc.toString(), Whitelist.none()));
    }
}
