package cz.ladicek.annDocuGen.annotationProcessor;

import javax.inject.Qualifier;
import javax.inject.Scope;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utils {
    private Utils() {} // avoid instantitation

    public static Element declaringClassOf(Element element) {
        Element clazz = element;
        while (clazz != null && clazz.getKind() != ElementKind.CLASS) {
            clazz = clazz.getEnclosingElement();
        }
        return clazz;
    }

    public static String qualifierAndScopeAnnotationsOf(Element element) {
        List<AnnotationMirror> allAnnotations = new ArrayList<AnnotationMirror>();
        allAnnotations.addAll(annotationsOf(element, Qualifier.class));
        allAnnotations.addAll(annotationsOf(element, Scope.class));

        if (allAnnotations.isEmpty()) {
            return null;
        }

        StringBuilder result = new StringBuilder();
        for (AnnotationMirror annotation : allAnnotations) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(annotation.toString());
        }

        return result.toString();
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

    private static final Pattern TYPE = Pattern.compile("(\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*\\.)+(\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*)");

    public static String shortenTypes(String string) {
        Matcher matcher = TYPE.matcher(string);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, matcher.group(2));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
