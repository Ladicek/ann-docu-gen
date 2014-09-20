package cz.ladicek.annDocuGen.annotationProcessor;

public final class DocumentedDependency {
    public final String type; // type of the field or constructor parameter annotated with @Inject
    public final String qualifierAndScopeAnnotations;
    public final String javadoc;

    public DocumentedDependency(String type, String qualifierAndScopeAnnotations, String javadoc) {
        this.type = type;
        this.qualifierAndScopeAnnotations = qualifierAndScopeAnnotations;
        this.javadoc = javadoc;
    }
}
