package cz.ladicek.annDocuGen.annotationProcessor;

import javax.lang.model.element.Element;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class Documentation {
    private final Map<String, DocumentedType> types = new HashMap<String, DocumentedType>();

    public DocumentedType documentedType(Element clazz, String javadoc) {
        DocumentedType type = types.get(clazz.toString());
        if (type == null) {
            type = new DocumentedType(clazz, javadoc);
            types.put(clazz.toString(), type);
        }
        return type;
    }

    public Collection<DocumentedType> allDocumentedTypes() {
        return types.values();
    }
}
