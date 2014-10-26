package cz.ladicek.annDocuGen.annotationProcessor.model;

public final class DocumentedDependency {
    public final TypeName type; // type of the field or constructor parameter annotated with @Inject
    public final DocumentedAnnotations documentedAnnotations;
    public final Javadoc javadoc;
    public final boolean isDocumentedClass;

    public DocumentedDependency(TypeName type, DocumentedAnnotations documentedAnnotations, Javadoc javadoc) {
        this(type, documentedAnnotations, javadoc, false);
    }

    private DocumentedDependency(TypeName type, DocumentedAnnotations documentedAnnotations, Javadoc javadoc,
                                 boolean isDocumentedClass) {
        this.type = type;
        this.documentedAnnotations = documentedAnnotations;
        this.javadoc = javadoc;
        this.isDocumentedClass = isDocumentedClass;
    }

    public DocumentedDependency asDocumentedClass() {
        return new DocumentedDependency(type, documentedAnnotations, javadoc, true);
    }
}
