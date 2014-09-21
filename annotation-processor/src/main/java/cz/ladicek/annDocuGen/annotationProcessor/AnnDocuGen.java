package cz.ladicek.annDocuGen.annotationProcessor;

import cz.ladicek.annDocuGen.api.Property;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.inject.Inject;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Arrays;
import java.util.Set;

import static cz.ladicek.annDocuGen.annotationProcessor.Elements.declaringClassOf;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes({"javax.inject.Inject", "cz.ladicek.annDocuGen.api.Property"})
public class AnnDocuGen extends AbstractProcessor {
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
        if (annotations.isEmpty()) {
            return false;
        }

        Documentation doc = new Documentation(processingEnv);

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Collecting documentation for "
                + roundEnv.getRootElements());
        collectDocumentation(roundEnv, doc);

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Documentation collected, generating files");
        doc.generateDocumentationFiles();

        return false;
    }

    private void collectDocumentation(RoundEnvironment roundEnv, Documentation doc) {
        collectDocumentationForDependencies(roundEnv, doc);
        collectDocumentationForProperties(roundEnv, doc);
    }

    private void collectDocumentationForDependencies(RoundEnvironment roundEnv, Documentation doc) {
        for (Element annotated : roundEnv.getElementsAnnotatedWith(Inject.class)) {
            Element clazz = declaringClassOf(annotated);
            DocumentedClass type = doc.documentClass(clazz);

            if (annotated.getAnnotation(Property.class) != null) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                        "@Inject @Property is invalid. Use @Inject for dependencies and @Property for properties.",
                        annotated);
                continue;
            }

            if (annotated.getKind() != ElementKind.CONSTRUCTOR && annotated.getKind() != ElementKind.FIELD) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                        "@Inject is only supported for fields and constructors", annotated);
                continue;
            }

            switch (annotated.getKind()) {
                case FIELD:
                    type.addDependency(doc.documentDependency(annotated));
                    break;
                case CONSTRUCTOR:
                    for (Element param : ((ExecutableElement) annotated).getParameters()) {
                        type.addDependency(doc.documentDependency(param));
                    }
                    break;
            }
        }
    }

    private void collectDocumentationForProperties(RoundEnvironment roundEnv, Documentation doc) {
        for (Element annotated : roundEnv.getElementsAnnotatedWith(Property.class)) {
            Element clazz = declaringClassOf(annotated);
            DocumentedClass type = doc.documentClass(clazz);

            if (annotated.getKind() != ElementKind.FIELD) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                        "@Property is only supported for fields", annotated);
                continue;
            }

            type.addProperty(doc.documentProperty(annotated));
        }
    }
}
