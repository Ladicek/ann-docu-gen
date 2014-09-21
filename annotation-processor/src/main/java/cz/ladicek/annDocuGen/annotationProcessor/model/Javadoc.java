package cz.ladicek.annDocuGen.annotationProcessor.model;

import com.google.common.base.Optional;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.text.BreakIterator;
import java.util.Locale;

public final class Javadoc {
    private final Optional<String> value;

    public Javadoc(ProcessingEnvironment processingEnv, Element element) {
        this.value = Optional.fromNullable(processingEnv.getElementUtils().getDocComment(element));
    }

    public boolean exists() {
        return value.isPresent();
    }

    public String formatForOutput() {
        return value.isPresent() ? "> " + value.get().replaceAll("\n", "\n> ") : "";
    }
}
