package cz.ladicek.annDocuGen.annotationProcessor.view;

import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedProperty;
import cz.ladicek.annDocuGen.annotationProcessor.model.FieldInitializer;
import cz.ladicek.annDocuGen.annotationProcessor.model.Javadoc;
import cz.ladicek.annDocuGen.annotationProcessor.model.TypeName;

public class DocumentedPropertyView {
    private final DocumentedProperty delegate;
    public final boolean isInherited;

    DocumentedPropertyView(DocumentedProperty delegate, boolean isInherited) {
        this.delegate = delegate;
        this.isInherited = isInherited;
    }

    public String name() {
        return delegate.name;
    }

    public TypeName type() {
        return delegate.type;
    }

    public FieldInitializer initializer() {
        return delegate.initializer;
    }

    public boolean mandatory() {
        return delegate.mandatory;
    }

    public Javadoc javadoc() {
        return delegate.javadoc;
    }
}
