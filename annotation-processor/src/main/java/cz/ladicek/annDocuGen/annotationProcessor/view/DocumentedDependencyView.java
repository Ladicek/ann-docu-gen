package cz.ladicek.annDocuGen.annotationProcessor.view;

import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedAnnotations;
import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedDependency;
import cz.ladicek.annDocuGen.annotationProcessor.model.Javadoc;
import cz.ladicek.annDocuGen.annotationProcessor.model.TypeName;

public final class DocumentedDependencyView {
    private final DocumentedDependency documentedDependency;
    public final boolean isDocumentedClass;
    public final boolean isInherited;

    DocumentedDependencyView(DocumentedDependency documentedDependency, boolean isDocumentedClass,
                             boolean isInherited) {
        this.documentedDependency = documentedDependency;
        this.isDocumentedClass = isDocumentedClass;
        this.isInherited = isInherited;
    }

    public TypeName type() {
        return documentedDependency.type;
    }

    public DocumentedAnnotations documentedAnnotations() {
        return documentedDependency.documentedAnnotations;
    }

    public Javadoc javadoc() {
        return documentedDependency.javadoc;
    }
}
