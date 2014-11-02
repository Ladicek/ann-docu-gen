package cz.ladicek.annDocuGen.annotationProcessor.model;

public final class DocumentedOutputProperty {
    public final String name; // value of the @OutputProperty annotation
    public final TypeName type; // type of the field annotated with @OutputProperty
    public final Javadoc javadoc;

    public DocumentedOutputProperty(String name, TypeName type, Javadoc javadoc) {
        this.name = name;
        this.type = type;
        this.javadoc = javadoc;
    }
}
