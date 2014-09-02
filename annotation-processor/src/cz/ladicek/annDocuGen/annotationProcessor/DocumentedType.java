package cz.ladicek.annDocuGen.annotationProcessor;

import javax.lang.model.element.Element;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public final class DocumentedType {
    public final Element clazz;
    public final String javadoc;
    public final List<DocumentedProperty> properties = new ArrayList<DocumentedProperty>();
    public final List<DocumentedDependency> dependencies = new ArrayList<DocumentedDependency>();

    public DocumentedType(Element clazz, String javadoc) {
        this.clazz = clazz;
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
        out.println("# " + clazz.getSimpleName());
        out.println();
        out.println("`" + clazz.toString() + "`");
        out.println();
        out.println(formatJavadoc(javadoc));
        out.println();

        out.println("## Properties");
        out.println();
        if (properties.isEmpty()) {
            out.println("_None_");
            out.println();
        } else {
            for (DocumentedProperty property : properties) {
                out.println("__" + property.propertyName + "__: `" + property.propertyClassName + "`");
                out.println();
                out.println(formatJavadoc(property.javadoc));
                out.println();
            }
        }

        out.println("## Dependencies");
        out.println();
        if (dependencies.isEmpty()) {
            out.println("_None_");
            out.println();
        } else {
            for (DocumentedDependency dependency : dependencies) {
                out.println("__`" + dependency.dependencyClassName + "`__");
                out.println();
                out.println(formatJavadoc(dependency.javadoc));
                out.println();
            }
        }
    }

    private String formatJavadoc(String javadoc) {
        return javadoc == null ? "" : "> " + javadoc.replaceAll("\n", "\n> ");
    }
}
