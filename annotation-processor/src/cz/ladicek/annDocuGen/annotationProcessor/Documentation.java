package cz.ladicek.annDocuGen.annotationProcessor;

import cz.ladicek.annDocuGen.api.Property;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class Documentation {
    private final ProcessingEnvironment processingEnv;
    private final FieldInitializerDiscovery fieldInitializerDiscovery;
    private final Map<String, DocumentedClass> classes = new HashMap<String, DocumentedClass>();

    public Documentation(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        this.fieldInitializerDiscovery = FieldInitializerDiscovery.Factory.create(processingEnv);
    }

    public DocumentedClass documentClass(Element clazz) {
        String fullName = clazz.toString();

        DocumentedClass type = classes.get(fullName);
        if (type == null) {
            type = new DocumentedClass(clazz.getSimpleName().toString(), fullName,
                    processingEnv.getElementUtils().getDocComment(clazz));
            classes.put(fullName, type);
        }
        return type;
    }

    public Collection<DocumentedClass> allDocumentedClasses() {
        return classes.values();
    }

    public DocumentedProperty documentPropertyField(Element annotatedField) {
        String name = annotatedField.getAnnotation(Property.class).value();
        String type = annotatedField.asType().toString();
        String initializer = fieldInitializerDiscovery.getFor(annotatedField);
        boolean mandatory = initializer == null && !type.contains("Optional");
        String javadoc = processingEnv.getElementUtils().getDocComment(annotatedField);
        return new DocumentedProperty(name, type, initializer, mandatory, javadoc);
    }

    public DocumentedDependency documentDependencyField(Element annotatedField) {
        String className = annotatedField.asType().toString();
        String javadoc = processingEnv.getElementUtils().getDocComment(annotatedField);
        return new DocumentedDependency(className, javadoc);
    }

    public DocumentedDependency documentDependencyConstructorParam(Element ctorParam) {
        String className = ctorParam.asType().toString();
        return new DocumentedDependency(className, null); // no javadoc for constructor parameters
    }
}
