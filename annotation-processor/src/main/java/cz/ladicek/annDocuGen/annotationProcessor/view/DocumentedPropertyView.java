package cz.ladicek.annDocuGen.annotationProcessor.view;

import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedProperty;
import cz.ladicek.annDocuGen.annotationProcessor.model.FieldInitializer;
import cz.ladicek.annDocuGen.annotationProcessor.model.Javadoc;
import cz.ladicek.annDocuGen.annotationProcessor.model.TypeName;

public class DocumentedPropertyView {
    private final DocumentedProperty documentedProperty;
    public final boolean isInherited;

    DocumentedPropertyView(DocumentedProperty documentedProperty, boolean isInherited) {
        this.documentedProperty = documentedProperty;
        this.isInherited = isInherited;
    }

    public String name() {
        return documentedProperty.name;
    }

    public TypeName type() {
        return documentedProperty.type;
    }

    public FieldInitializer initializer() {
        return documentedProperty.initializer;
    }

    public boolean mandatory() {
        return documentedProperty.mandatory;
    }

    public Javadoc javadoc() {
        return documentedProperty.javadoc;
    }
}
