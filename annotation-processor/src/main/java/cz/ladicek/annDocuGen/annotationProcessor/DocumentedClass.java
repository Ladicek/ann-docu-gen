package cz.ladicek.annDocuGen.annotationProcessor;

import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedAnnotations;
import cz.ladicek.annDocuGen.annotationProcessor.model.Javadoc;

import java.io.PrintWriter;
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

    // ---

    public void writeDocumentation(PrintWriter out) {
        out.println("# " + (isUnit ? "Unit" : "Service") + " `" + simpleName + "`");
        out.println();
        if (documentedAnnotations.exist()) {
            out.print("`" + documentedAnnotations + "` ");
        }
        out.println("__`" + fullName + "`__");
        out.println();
        if (javadoc.exists()) {
            out.println(javadoc.formatForOutput());
            out.println();
        }

        out.println("## Properties");
        out.println();
        if (properties.isEmpty()) {
            out.println("_None_");
            out.println();
        } else {
            for (DocumentedProperty property : properties) {
                out.print("__" + property.name + "__: `" + property.type.simpleName() + "`");
                if (property.initializer.exists()) {
                    out.print(" = `" + property.initializer + "`");
                }
                if (property.mandatory) {
                    out.print(" __mandatory__");
                }
                out.println();
                out.println();
                if (property.javadoc.exists()) {
                    out.println(property.javadoc.formatForOutput());
                    out.println();
                }
            }
        }

        out.println("## Dependencies");
        out.println();
        if (dependencies.isEmpty()) {
            out.println("_None_");
            out.println();
        } else {
            for (DocumentedDependency dependency : dependencies) {
                if (dependency.documentedAnnotations.exist()) {
                    out.print("`" + dependency.documentedAnnotations + "` ");
                }
                out.println("__`" + dependency.type.simpleName() + "`__");
                out.println();
                if (dependency.javadoc.exists()) {
                    out.println(dependency.javadoc.formatForOutput());
                    out.println();
                }
            }
        }
    }

    public static final Comparator<DocumentedClass> SIMPLE_NAME_COMPARATOR = new Comparator<DocumentedClass>() {
        @Override
        public int compare(DocumentedClass o1, DocumentedClass o2) {
            return o1.simpleName.compareTo(o2.simpleName);
        }
    };
}
