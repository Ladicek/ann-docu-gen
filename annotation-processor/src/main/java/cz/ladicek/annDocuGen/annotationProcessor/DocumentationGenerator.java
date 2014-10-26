package cz.ladicek.annDocuGen.annotationProcessor;

import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedClass;
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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Arrays;
import java.util.Set;

import static cz.ladicek.annDocuGen.annotationProcessor.Elements.declaringClassOf;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes({"javax.inject.Inject", "cz.ladicek.annDocuGen.api.Property"})
public class DocumentationGenerator extends AbstractProcessor {
    private Documentation doc;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.doc = new Documentation(processingEnv);
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
        if (!annotations.isEmpty()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Collecting documentation for "
                    + roundEnv.getRootElements());
            collectDocumentation(roundEnv, doc);

            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Collecting documentation for additional "
                    + "encountered classes");
            doc.processEncounteredDependencies();
        }

        if (roundEnv.processingOver()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Documentation collected, generating files");
            doc.generateDocumentationFiles();
        }

        return false;
    }

    private void collectDocumentation(RoundEnvironment roundEnv, Documentation doc) {
        encounterRootClasses(roundEnv, doc);
        collectDocumentationForDependencies(roundEnv, doc);
        collectDocumentationForProperties(roundEnv, doc);
    }

    private void encounterRootClasses(RoundEnvironment roundEnv, Documentation doc) {
        for (Element root : roundEnv.getRootElements()) {
            if (root.getKind() == ElementKind.CLASS) {
                doc.encounterRootClass(root);
            }
        }
    }

    private void collectDocumentationForDependencies(RoundEnvironment roundEnv, Documentation doc) {
        for (Element annotated : roundEnv.getElementsAnnotatedWith(Inject.class)) {
            if (annotated.getAnnotation(Property.class) != null) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                        "@Inject @Property is invalid. Use @Inject for dependencies and @Property for properties.",
                        annotated);
                continue;
            }

            Element clazz = declaringClassOf(annotated);
            DocumentedClass type = doc.documentClass(clazz);

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
            if (annotated.getAnnotation(Inject.class) != null) {
                // warning is printed out in collectDocumentationForDependencies, no need to do it here too
                continue;
            }

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
