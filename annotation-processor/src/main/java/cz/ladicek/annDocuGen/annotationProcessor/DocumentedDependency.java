package cz.ladicek.annDocuGen.annotationProcessor;

import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedAnnotations;
import cz.ladicek.annDocuGen.annotationProcessor.model.Javadoc;
import cz.ladicek.annDocuGen.annotationProcessor.model.TypeName;

public final class DocumentedDependency {
    public final TypeName type; // type of the field or constructor parameter annotated with @Inject
    public final DocumentedAnnotations documentedAnnotations;
    public final Javadoc javadoc;

    public DocumentedDependency(TypeName type, DocumentedAnnotations documentedAnnotations, Javadoc javadoc) {
        this.type = type;
        this.documentedAnnotations = documentedAnnotations;
        this.javadoc = javadoc;
    }
}
