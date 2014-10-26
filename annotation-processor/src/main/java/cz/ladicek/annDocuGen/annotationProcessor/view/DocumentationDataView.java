package cz.ladicek.annDocuGen.annotationProcessor.view;

import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentationData;
import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedClass;
import cz.ladicek.annDocuGen.annotationProcessor.model.TypeName;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DocumentationDataView {
    private final List<DocumentedClassView> documentedClassViews;

    public DocumentationDataView(DocumentationData documentationData) {
        Set<TypeName> allDocumentedClasses = new HashSet<TypeName>();
        for (DocumentedClass documentedClass : documentationData.documentedClasses()) {
            if (visibleInDocumentation(documentedClass)) {
                allDocumentedClasses.add(documentedClass.fullName);
            }
        }

        this.documentedClassViews = new ArrayList<DocumentedClassView>();
        for (DocumentedClass documentedClass : documentationData.documentedClasses()) {
            if (visibleInDocumentation(documentedClass)) {
                documentedClassViews.add(new DocumentedClassView(documentedClass, allDocumentedClasses));
            }
        }
    }

    private static boolean visibleInDocumentation(DocumentedClass clazz) {
        return clazz.isPublic && !clazz.isAbstract;
    }

    public List<DocumentedClassView> documentedClasses() {
        return documentedClassViews;
    }
}
