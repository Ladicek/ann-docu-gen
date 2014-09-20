package cz.ladicek.annDocuGen.annotationProcessor;

import cz.ladicek.annDocuGen.api.Property;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class Documentation {
    private static final String OPTIONAL_TYPE_FQN = "cz.ladicek.annDocuGen.api.Optional";

    private final ProcessingEnvironment processingEnv;
    private final TypeMirror optionalTypeErased;
    private final FieldInitializerDiscovery fieldInitializerDiscovery;
    private final Map<String, DocumentedClass> classes = new HashMap<String, DocumentedClass>();

    public Documentation(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        this.optionalTypeErased = processingEnv.getTypeUtils().erasure(
                processingEnv.getElementUtils().getTypeElement(OPTIONAL_TYPE_FQN).asType());
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
        TypeMirror fieldTypeErased = processingEnv.getTypeUtils().erasure(annotatedField.asType());
        boolean isOptional = processingEnv.getTypeUtils().isSameType(optionalTypeErased, fieldTypeErased);
        boolean isPrimitive = fieldTypeErased.getKind().isPrimitive();

        String name = annotatedField.getAnnotation(Property.class).value();
        String type = annotatedField.asType().toString();
        String initializer = fieldInitializerDiscovery.getFor(annotatedField);
        if (initializer == null) {
            if (isOptional) {
                initializer = "Optional.absent() /* implied */";
            } else if (isPrimitive) {
                switch(fieldTypeErased.getKind()) {
                    case BOOLEAN:
                        initializer = "false /* primitive default */";
                        break;
                    case BYTE:
                        initializer = "0 /* primitive default */";
                        break;
                    case SHORT:
                        initializer = "0 /* primitive default */";
                        break;
                    case INT:
                        initializer = "0 /* primitive default */";
                        break;
                    case LONG:
                        initializer = "0L /* primitive default */";
                        break;
                    case FLOAT:
                        initializer = "0.0F /* primitive default */";
                        break;
                    case DOUBLE:
                        initializer = "0.0 /* primitive default */";
                        break;
                    case CHAR:
                        initializer = "'\\u0000' /* primitive default */";
                        break;
                }
            }
        }
        boolean mandatory = initializer == null;
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
