package cz.ladicek.annDocuGen.annotationProcessor;

public final class DocumentedDependency {
    public final String dependencyClassName;
    public final String javadoc;

    public DocumentedDependency(String dependencyClassName, String javadoc) {
        this.dependencyClassName = dependencyClassName;
        this.javadoc = javadoc;
    }
}
