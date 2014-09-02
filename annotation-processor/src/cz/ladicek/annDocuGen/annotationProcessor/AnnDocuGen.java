package cz.ladicek.annDocuGen.annotationProcessor;

import cz.ladicek.annDocuGen.api.Inject;
import cz.ladicek.annDocuGen.api.Property;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("cz.ladicek.annDocuGen.api.Inject")
public class AnnDocuGen extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            return doProcess(annotations, roundEnv);
        } catch (Exception e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "ERROR " + e);
            return true;
        }
    }

    private boolean doProcess(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return true;
        }

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Collecting documentation for " + roundEnv.getRootElements());

        Documentation doc = new Documentation();

        for (Element annotated : roundEnv.getElementsAnnotatedWith(Inject.class)) {
            Element clazz = declaringClassOf(annotated);
            DocumentedType type = doc.documentedType(clazz, processingEnv.getElementUtils().getDocComment(clazz));

            if (annotated.getAnnotation(Property.class) != null) {
                String propertyName = annotated.getAnnotation(Property.class).value();
                String propertyClassName = annotated.asType().toString();
                String javadoc = processingEnv.getElementUtils().getDocComment(annotated);

                DocumentedProperty property = new DocumentedProperty(propertyName, propertyClassName, javadoc);
                type.addProperty(property);
            } else {
                String dependencyClassName = annotated.asType().toString();
                String javadoc = processingEnv.getElementUtils().getDocComment(annotated);

                DocumentedDependency dependency = new DocumentedDependency(dependencyClassName, javadoc);
                type.addDependency(dependency);
            }
        }

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Documentation collected, generating files");

        try {
            for (DocumentedType type : doc.allDocumentedTypes()) {
                FileObject file = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "annDocuGen",
                        type.clazz.toString() + ".md");
                Writer writer = file.openWriter();
                try {
                    PrintWriter printWriter = new PrintWriter(writer);
                    type.writeDocumentation(printWriter);
                } finally {
                    writer.close();
                }
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "IO problem: " + e);
            throw new RuntimeException(e);
        }

        return true;
    }

    private Element declaringClassOf(Element annotated) {
        Element clazz = annotated;
        while (clazz != null && clazz.getKind() != ElementKind.CLASS) {
            clazz = clazz.getEnclosingElement();
        }
        return clazz;
    }
}
