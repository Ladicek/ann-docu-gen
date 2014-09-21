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

    public String formatForOutput() {
        return value.isPresent() ? "> " + value.get().trim().replaceAll("\n", "\n> ") : "";
    }

    public String firstSentence() {
        if (!value.isPresent()) {
            return "";
        }

        BreakIterator boundary = BreakIterator.getSentenceInstance(Locale.US);
        boundary.setText(value.get());
        int start = boundary.first();
        int end = boundary.next();

        String firstSentence = value.get().substring(start, end);
        return Jsoup.clean(firstSentence, Whitelist.none());
    }
}
