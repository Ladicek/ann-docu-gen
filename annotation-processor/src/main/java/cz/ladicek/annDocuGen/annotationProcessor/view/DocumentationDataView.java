package cz.ladicek.annDocuGen.annotationProcessor.view;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentationData;
import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedClass;
import cz.ladicek.annDocuGen.annotationProcessor.model.TypeName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DocumentationDataView {
    private final List<DocumentedClassView> documentedClasses;

    public DocumentationDataView(DocumentationData documentationData) {
        Set<TypeName> allVisibleDocumentedClasses = new HashSet<TypeName>();
        Map<TypeName, DocumentedClass> allDocumentedClasses = new HashMap<TypeName, DocumentedClass>();
        for (DocumentedClass documentedClass : documentationData.documentedClasses()) {
            if (visibleInDocumentation(documentedClass)) {
                allVisibleDocumentedClasses.add(documentedClass.fullName);
            }
            allDocumentedClasses.put(documentedClass.fullName, documentedClass);
        }

        ListMultimap<TypeName, DocumentedClass> inheritanceChains = ArrayListMultimap.create();
        for (Map.Entry<TypeName, DocumentedClass> entry : allDocumentedClasses.entrySet()) {
            TypeName typeName = entry.getKey();
            DocumentedClass clazz = entry.getValue();
            while (clazz != null) {
                inheritanceChains.put(typeName, clazz);
                clazz = allDocumentedClasses.get(clazz.parent.orNull());
            }
        }

        this.documentedClasses = new ArrayList<DocumentedClassView>();
        for (DocumentedClass documentedClass : documentationData.documentedClasses()) {
            if (visibleInDocumentation(documentedClass)) {
                List<DocumentedClass> inheritanceChain = Lists.reverse(inheritanceChains.get(documentedClass.fullName));
                documentedClasses.add(new DocumentedClassView(documentedClass, allVisibleDocumentedClasses,
                        inheritanceChain));
            }
        }
    }

    private static boolean visibleInDocumentation(DocumentedClass clazz) {
        return clazz.isPublic && !clazz.isAbstract;
    }

    public List<DocumentedClassView> documentedClasses() {
        return documentedClasses;
    }
}
