package cz.ladicek.annDocuGen.annotationProcessor.model;

import com.google.common.base.Optional;

import javax.inject.Qualifier;
import javax.inject.Scope;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public final class DocumentedAnnotations {
    private final Optional<String> description;

    public DocumentedAnnotations(Element element) {
        List<AnnotationMirror> allAnnotations = new ArrayList<AnnotationMirror>();
        allAnnotations.addAll(annotationsOf(element, Qualifier.class));
        allAnnotations.addAll(annotationsOf(element, Scope.class));

        if (allAnnotations.isEmpty()) {
            this.description = Optional.absent();
            return;
        }

        StringBuilder result = new StringBuilder();
        for (AnnotationMirror annotation : allAnnotations) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(annotation.toString());
        }

        this.description = Optional.of(result.toString());
    }

    private static List<AnnotationMirror> annotationsOf(Element element, Class<? extends Annotation> metaAnnotation) {
        List<AnnotationMirror> result = new ArrayList<AnnotationMirror>();
        for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
            Element annotationType = annotation.getAnnotationType().asElement();
            if (annotationType.getAnnotation(metaAnnotation) != null) {
                result.add(annotation);
            }
        }
        return result;
    }


    public boolean exist() {
        return description.isPresent();
    }

    @Override
    public String toString() {
        return description.isPresent() ? TypeName.shorten(description.get()) : "";
    }
}
