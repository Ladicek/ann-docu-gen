package cz.ladicek.annDocuGen.annotationProcessor.view;

import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedAnnotations;
import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedClass;
import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedDependency;
import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedProperty;
import cz.ladicek.annDocuGen.annotationProcessor.model.Javadoc;
import cz.ladicek.annDocuGen.annotationProcessor.model.TypeName;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public final class DocumentedClassView {
    private final DocumentedClass documentedClass;
    public final List<DocumentedPropertyView> properties = new ArrayList<DocumentedPropertyView>();
    public final List<DocumentedDependencyView> dependencies = new ArrayList<DocumentedDependencyView>();

    DocumentedClassView(DocumentedClass documentedClass, Set<TypeName> allDocumentedClasses,
                        List<DocumentedClass> inheritanceChain) {
        this.documentedClass = documentedClass;
        for (DocumentedClass clazz : inheritanceChain) {
            boolean isInherited = !documentedClass.fullName.equals(clazz.fullName);
            for (DocumentedProperty property : clazz.properties) {
                properties.add(new DocumentedPropertyView(property, isInherited));
            }
            for (DocumentedDependency dependency : clazz.dependencies) {
                boolean isDocumented = allDocumentedClasses.contains(dependency.type);
                dependencies.add(new DocumentedDependencyView(dependency, isDocumented, isInherited));
            }
        }
    }

    public String simpleName() {
        return documentedClass.simpleName;
    }

    public TypeName fullName() {
        return documentedClass.fullName;
    }

    public DocumentedAnnotations documentedAnnotations() {
        return documentedClass.documentedAnnotations;
    }

    public boolean isUnit() {
        return documentedClass.isUnit;
    }

    public Javadoc javadoc() {
        return documentedClass.javadoc;
    }

    // ---

    public static final Comparator<DocumentedClassView> SIMPLE_NAME_COMPARATOR = new Comparator<DocumentedClassView>() {
        @Override
        public int compare(DocumentedClassView o1, DocumentedClassView o2) {
            return o1.simpleName().compareTo(o2.simpleName());
        }
    };
}
