package cz.ladicek.annDocuGen.annotationProcessor.model;

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public final class DocumentedClass {
    public final boolean isPublic;
    public final boolean isAbstract;
    public final String simpleName;
    public final TypeName fullName;
    public final DocumentedAnnotations documentedAnnotations;
    public final Optional<TypeName> parent; // absent if the parent is java.lang.Object
    public final boolean isUnit; // if it implements (directly or indirectly) the Unit interface
    public final Javadoc javadoc;

    public final List<DocumentedProperty> properties = new ArrayList<DocumentedProperty>();
    public final List<DocumentedOutputProperty> outputProperties = new ArrayList<DocumentedOutputProperty>();
    public final List<DocumentedDependency> dependencies = new ArrayList<DocumentedDependency>();

    public DocumentedClass(boolean isPublic, boolean isAbstract, String simpleName, TypeName fullName,
                           DocumentedAnnotations documentedAnnotations, Optional<TypeName> parent, boolean isUnit,
                           Javadoc javadoc) {
        this.isPublic = isPublic;
        this.isAbstract = isAbstract;
        this.simpleName = simpleName;
        this.fullName = fullName;
        this.documentedAnnotations = documentedAnnotations;
        this.parent = parent;
        this.isUnit = isUnit;
        this.javadoc = javadoc;
    }

    public void addProperty(DocumentedProperty property) {
        properties.add(property);
    }

    public void addOutputProperty(DocumentedOutputProperty outputProperty) {
        outputProperties.add(outputProperty);
    }

    public void addDependency(DocumentedDependency dependency) {
        dependencies.add(dependency);
    }
}
