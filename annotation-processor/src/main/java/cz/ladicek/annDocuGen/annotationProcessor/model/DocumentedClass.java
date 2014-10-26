package cz.ladicek.annDocuGen.annotationProcessor.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public final class DocumentedClass {
    public final String simpleName;
    public final TypeName fullName;
    public final DocumentedAnnotations documentedAnnotations;
    public final boolean isUnit; // if it implements (directly or indirectly) the Unit interface
    public final Javadoc javadoc;

    public final List<DocumentedProperty> properties = new ArrayList<DocumentedProperty>();
    public final List<DocumentedDependency> dependencies = new ArrayList<DocumentedDependency>();

    public DocumentedClass(String simpleName, TypeName fullName, boolean isUnit,
                           DocumentedAnnotations documentedAnnotations, Javadoc javadoc) {
        this.simpleName = simpleName;
        this.fullName = fullName;
        this.isUnit = isUnit;
        this.documentedAnnotations = documentedAnnotations;
        this.javadoc = javadoc;
    }

    public void addProperty(DocumentedProperty property) {
        properties.add(property);
    }

    public void addDependency(DocumentedDependency dependency) {
        dependencies.add(dependency);
    }
}
