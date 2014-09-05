package cz.ladicek.annDocuGen.annotationProcessor;

public final class DocumentedProperty {
    public final String name; // value of the @Property annotation
    public final String type; // type of the field annotated with @Property
    public final String initializer;
    public final boolean mandatory;
    public final String javadoc;

    public DocumentedProperty(String name, String type, String initializer, boolean mandatory, String javadoc) {
        this.name = name;
        this.type = type;
        this.initializer = initializer;
        this.mandatory = mandatory;
        this.javadoc = javadoc;
    }
}
