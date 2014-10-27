package cz.ladicek.annDocuGen.annotationProcessor.view;

import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedAnnotations;
import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedDependency;
import cz.ladicek.annDocuGen.annotationProcessor.model.Javadoc;
import cz.ladicek.annDocuGen.annotationProcessor.model.TypeName;

public final class DocumentedDependencyView {
    private final DocumentedDependency delegate;
    public final boolean isDocumentedClass;
    public final boolean isInherited;

    DocumentedDependencyView(DocumentedDependency delegate, boolean isDocumentedClass,
                             boolean isInherited) {
        this.delegate = delegate;
        this.isDocumentedClass = isDocumentedClass;
        this.isInherited = isInherited;
    }

    public TypeName type() {
        return delegate.type;
    }

    public DocumentedAnnotations documentedAnnotations() {
        return delegate.documentedAnnotations;
    }

    public Javadoc javadoc() {
        return delegate.javadoc;
    }
}
