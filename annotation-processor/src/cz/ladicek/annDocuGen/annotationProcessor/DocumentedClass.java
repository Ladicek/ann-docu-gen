package cz.ladicek.annDocuGen.annotationProcessor;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static cz.ladicek.annDocuGen.annotationProcessor.Utils.shortenTypes;

public final class DocumentedClass {
    public final String simpleName;
    public final String fullName;
    public final String qualifierAndScopeAnnotations;
    public final boolean isUnit; // if it implements (directly or indirectly) the Unit interface
    public final String javadoc;

    public final List<DocumentedProperty> properties = new ArrayList<DocumentedProperty>();
    public final List<DocumentedDependency> dependencies = new ArrayList<DocumentedDependency>();

    public DocumentedClass(String simpleName, String fullName, boolean isUnit, String qualifierAndScopeAnnotations,
                           String javadoc) {
        this.simpleName = simpleName;
        this.fullName = fullName;
        this.isUnit = isUnit;
        this.qualifierAndScopeAnnotations = qualifierAndScopeAnnotations;
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
        if (qualifierAndScopeAnnotations != null) {
            out.print("`" + shortenTypes(qualifierAndScopeAnnotations) + "` ");
        }
        out.println("__`" + fullName + "`__");
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
                out.print("__" + property.name + "__: `" + shortenTypes(property.type) + "`");
                if (property.initializer != null) {
                    out.print(" = `" + property.initializer + "`");
                }
                if (property.mandatory) {
                    out.print(" __mandatory__");
                }
                out.println();
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
                if (dependency.qualifierAndScopeAnnotations != null) {
                    out.print("`" + shortenTypes(dependency.qualifierAndScopeAnnotations) + "` ");
                }
                out.println("__`" + shortenTypes(dependency.type) + "`__");
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
