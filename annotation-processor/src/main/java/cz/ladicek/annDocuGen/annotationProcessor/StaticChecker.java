package cz.ladicek.annDocuGen.annotationProcessor;

import cz.ladicek.annDocuGen.annotationProcessor.model.OutputProperties;
import cz.ladicek.annDocuGen.api.OutputProperty;
import cz.ladicek.annDocuGen.api.Property;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.inject.Inject;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes({"cz.ladicek.annDocuGen.api.Property", "cz.ladicek.annDocuGen.api.OutputProperty"})
public class StaticChecker extends AbstractProcessor {
    private OutputProperties outputProperties;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.outputProperties = new OutputProperties(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            return doProcess(annotations, roundEnv);
        } catch (Exception e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unexpected error: " + e.getMessage()
                    + " " + Arrays.toString(e.getStackTrace()));
            return false;
        }
    }

    private boolean doProcess(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element annotated : roundEnv.getElementsAnnotatedWith(Property.class)) {
            checkProperty(annotated);
        }

        for (Element annotated : roundEnv.getElementsAnnotatedWith(OutputProperty.class)) {
            checkOutputProperty(annotated);
        }

        return false;
    }

    private static final Pattern PROPERTY_NAME = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_.-]*");

    private void checkProperty(Element annotated) {
        if (annotated.getKind() != ElementKind.FIELD) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@Property can only be applied to fields",
                    annotated);
        }

        if (annotated.getAnnotation(Inject.class) != null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@Property can't be combined with @Inject",
                    annotated);
        }

        if (annotated.getAnnotation(OutputProperty.class) != null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "@Property can't be combined with @OutputProperty", annotated);
        }

        Property property = annotated.getAnnotation(Property.class);
        if (!PROPERTY_NAME.matcher(property.value()).matches()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@Property name must be " + PROPERTY_NAME,
                    annotated);
        }
    }

    private void checkOutputProperty(Element annotated) {
        if (annotated.getKind() != ElementKind.FIELD) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "@OutputProperty can only be applied to fields", annotated);
        }

        if (annotated.getAnnotation(Inject.class) != null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "@OutputProperty can't be combined with @Inject", annotated);
        }

        OutputProperty property = annotated.getAnnotation(OutputProperty.class);
        if (!PROPERTY_NAME.matcher(property.value()).matches()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "@OutputProperty name must be " + PROPERTY_NAME, annotated);
        }

        if (annotated.getKind() == ElementKind.FIELD && !outputProperties.typeOf(annotated).isPresent()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "@OutputProperty field must be of type Output<T>", annotated);
        }
    }
}
