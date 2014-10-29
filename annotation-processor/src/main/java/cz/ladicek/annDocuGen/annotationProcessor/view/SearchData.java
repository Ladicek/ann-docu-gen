package cz.ladicek.annDocuGen.annotationProcessor.view;

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

    public SearchData(DocumentedClassView documentedClassView) {
        this.name = documentedClassView.simpleName();
        this.type = documentedClassView.isUnit() ? "Unit" : "Service";
        this.description = Jsoup.clean(documentedClassView.javadoc().firstSentence(), Whitelist.none());
        this.fqn = documentedClassView.fullName().toString();
        String data = name + " " + type + " " + Jsoup.clean(documentedClassView.javadoc().toString(), Whitelist.none());
        this.tokens = Splitter
                .on(CharMatcher.JAVA_LETTER_OR_DIGIT.or(CharMatcher.is('\'')).negate()) // "'" is common in English
                .trimResults()
                .omitEmptyStrings()
                .splitToList(data);
    }
}
