package cz.ladicek.annDocuGen.annotationProcessor;

import com.github.mustachejava.Mustache;
import com.google.common.collect.ImmutableMap;
import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedAnnotations;
import cz.ladicek.annDocuGen.annotationProcessor.model.Javadoc;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class DocumentedClass {
    public final String simpleName;
    public final String fullName;
    public final DocumentedAnnotations documentedAnnotations;
    public final boolean isUnit; // if it implements (directly or indirectly) the Unit interface
    public final Javadoc javadoc;

    public final List<DocumentedProperty> properties = new ArrayList<DocumentedProperty>();
    public final List<DocumentedDependency> dependencies = new ArrayList<DocumentedDependency>();

    public DocumentedClass(String simpleName, String fullName, boolean isUnit,
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

    public static final Comparator<DocumentedClass> SIMPLE_NAME_COMPARATOR = new Comparator<DocumentedClass>() {
        @Override
        public int compare(DocumentedClass o1, DocumentedClass o2) {
            return o1.simpleName.compareTo(o2.simpleName);
        }
    };
}
