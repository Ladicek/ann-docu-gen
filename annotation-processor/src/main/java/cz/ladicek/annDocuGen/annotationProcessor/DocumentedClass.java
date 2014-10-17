package cz.ladicek.annDocuGen.annotationProcessor;

import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedAnnotations;
import cz.ladicek.annDocuGen.annotationProcessor.model.Javadoc;
import cz.ladicek.annDocuGen.annotationProcessor.model.TypeName;

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

    public void markDependenciesThatAreDocumentedClasses(Set<TypeName> allDocumentedClasses) {
        List<DocumentedDependency> newDependencies = new ArrayList<DocumentedDependency>();
        for (DocumentedDependency dependency : dependencies) {
            if (allDocumentedClasses.contains(dependency.type)) {
                newDependencies.add(dependency.asDocumentedClass());
            } else {
                newDependencies.add(dependency);
            }
        }

        dependencies.clear();
        dependencies.addAll(newDependencies);
    }

    public static final Comparator<DocumentedClass> SIMPLE_NAME_COMPARATOR = new Comparator<DocumentedClass>() {
        @Override
        public int compare(DocumentedClass o1, DocumentedClass o2) {
            return o1.simpleName.compareTo(o2.simpleName);
        }
    };
}
