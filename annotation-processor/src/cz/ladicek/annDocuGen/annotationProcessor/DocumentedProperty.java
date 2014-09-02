package cz.ladicek.annDocuGen.annotationProcessor;

import javax.lang.model.element.Element;

public final class DocumentedProperty {
    public final String propertyName;
    public final String propertyClassName;
    public final String javadoc;

    public DocumentedProperty(String propertyName, String propertyClassName, String javadoc) {
        this.propertyName = propertyName;
        this.propertyClassName = propertyClassName;
        this.javadoc = javadoc;
    }
}
