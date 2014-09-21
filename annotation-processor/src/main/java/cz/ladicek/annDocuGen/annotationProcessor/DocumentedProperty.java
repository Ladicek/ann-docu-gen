package cz.ladicek.annDocuGen.annotationProcessor;

import cz.ladicek.annDocuGen.annotationProcessor.model.FieldInitializer;
import cz.ladicek.annDocuGen.annotationProcessor.model.Javadoc;
import cz.ladicek.annDocuGen.annotationProcessor.model.TypeName;

public final class DocumentedProperty {
    public final String name; // value of the @Property annotation
    public final TypeName type; // type of the field annotated with @Property
    public final FieldInitializer initializer;
    public final boolean mandatory;
    public final Javadoc javadoc;

    public DocumentedProperty(String name, TypeName type, FieldInitializer initializer, boolean mandatory,
                              Javadoc javadoc) {
        this.name = name;
        this.type = type;
        this.initializer = initializer;
        this.mandatory = mandatory;
        this.javadoc = javadoc;
    }
}
