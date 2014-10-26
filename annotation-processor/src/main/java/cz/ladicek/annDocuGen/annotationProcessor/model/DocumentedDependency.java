package cz.ladicek.annDocuGen.annotationProcessor.model;

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
