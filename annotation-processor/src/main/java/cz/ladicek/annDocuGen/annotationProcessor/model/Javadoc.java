package cz.ladicek.annDocuGen.annotationProcessor.model;

import com.google.common.base.Optional;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.text.BreakIterator;
import java.util.Locale;

public final class Javadoc {
    private final Optional<String> value;

    public Javadoc(ProcessingEnvironment processingEnv, Element element) {
        String docComment = processingEnv.getElementUtils().getDocComment(element);
        if (docComment != null && docComment.trim().isEmpty()) {
            docComment = null;
        }
        this.value = Optional.fromNullable(docComment);
    }

    public boolean exists() {
        return value.isPresent();
    }

    @Override
    public String toString() {
        return renderJavadocTags(value.or(""));
    }

    public String firstSentence() {
        if (!value.isPresent()) {
            return "";
        }

        String javadoc = value.get();

        BreakIterator boundary = BreakIterator.getSentenceInstance(Locale.US);
        boundary.setText(javadoc);
        int start = boundary.first();
        int end = boundary.next();

        String firstSentence = javadoc.substring(start, end);
        String cleanedFirstSentence = Jsoup.clean(firstSentence, Whitelist.none());
        return renderJavadocTags(cleanedFirstSentence);
    }

    private static String renderJavadocTags(String javadoc) {
        if (!javadoc.contains("{@")) { // fast path
            return javadoc;
        }

        return javadoc
                .replaceAll("\\{@code (.*?)\\}", "<code>$1</code>")
                .replaceAll("\\{@literal (.*?)\\}", "$1");
    }
}
