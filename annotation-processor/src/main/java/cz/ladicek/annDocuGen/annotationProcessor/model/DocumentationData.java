package cz.ladicek.annDocuGen.annotationProcessor.model;

import java.util.List;

public final class DocumentationData {
    private final List<DocumentedClass> documentedClasses;

    public DocumentationData(List<DocumentedClass> documentedClasses) {
        this.documentedClasses = documentedClasses;
    }

    public List<DocumentedClass> documentedClasses() {
        return documentedClasses;
    }
}
