package cz.ladicek.annDocuGen.annotationProcessor.view;

import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedOutputProperty;
import cz.ladicek.annDocuGen.annotationProcessor.model.Javadoc;
import cz.ladicek.annDocuGen.annotationProcessor.model.TypeName;

public class DocumentedOutputPropertyView {
    private final DocumentedOutputProperty delegate;
    public final boolean isInherited;

    DocumentedOutputPropertyView(DocumentedOutputProperty delegate, boolean isInherited) {
        this.delegate = delegate;
        this.isInherited = isInherited;
    }

    public String name() {
        return delegate.name;
    }

    public TypeName type() {
        return delegate.type;
    }

    public Javadoc javadoc() {
        return delegate.javadoc;
    }
}
