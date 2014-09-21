package cz.ladicek.annDocuGen.annotationProcessor;

import cz.ladicek.annDocuGen.api.Property;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cz.ladicek.annDocuGen.annotationProcessor.Utils.qualifierAndScopeAnnotationsOf;

public final class Documentation {
    private static final String OPTIONAL_TYPE_FQN = "com.google.common.base.Optional";
    private static final String UNIT_TYPE_FQN = "cz.ladicek.annDocuGen.api.Unit";

    private final ProcessingEnvironment processingEnv;
    private final TypeMirror optionalTypeErased;
    private final TypeMirror unitType;
    private final FieldInitializerDiscovery fieldInitializerDiscovery;
    private final Map<String, DocumentedClass> classes = new HashMap<String, DocumentedClass>();

    public Documentation(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        this.optionalTypeErased = processingEnv.getTypeUtils().erasure(
                processingEnv.getElementUtils().getTypeElement(OPTIONAL_TYPE_FQN).asType());
        this.unitType = processingEnv.getElementUtils().getTypeElement(UNIT_TYPE_FQN).asType();
        this.fieldInitializerDiscovery = FieldInitializerDiscovery.Factory.create(processingEnv);
    }

    public DocumentedClass documentClass(Element clazz) {
        String fullName = clazz.toString();

        DocumentedClass type = classes.get(fullName);
        if (type == null) {
            boolean isUnit = processingEnv.getTypeUtils().isAssignable(clazz.asType(), unitType);
            type = new DocumentedClass(clazz.getSimpleName().toString(), fullName, isUnit,
                    qualifierAndScopeAnnotationsOf(clazz), processingEnv.getElementUtils().getDocComment(clazz));
            classes.put(fullName, type);
        }
        return type;
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
                switch (fieldTypeErased.getKind()) {
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
        String annotations = qualifierAndScopeAnnotationsOf(annotatedField);
        String javadoc = processingEnv.getElementUtils().getDocComment(annotatedField);
        return new DocumentedDependency(className, annotations, javadoc);
    }

    public DocumentedDependency documentDependencyConstructorParam(Element ctorParam) {
        String className = ctorParam.asType().toString();
        String annotations = qualifierAndScopeAnnotationsOf(ctorParam);
        return new DocumentedDependency(className, annotations, null); // no javadoc for constructor parameters
    }


    public void generateDocumentationFiles() {
        try {
            doGenerateDocumentationFiles();
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "IO problem: " + e);
        }
    }

    private void doGenerateDocumentationFiles() throws IOException {
        {
            FileObject file = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "annDocuGen",
                    "index.md");
            Writer writer = file.openWriter();
            try {
                PrintWriter printWriter = new PrintWriter(writer);
                generateIndex(printWriter);
            } finally {
                writer.close();
            }
        }

        for (DocumentedClass type : classes.values()) {
            FileObject file = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "annDocuGen",
                    type.fullName + ".md");
            Writer writer = file.openWriter();
            try {
                PrintWriter printWriter = new PrintWriter(writer);
                type.writeDocumentation(printWriter);
            } finally {
                writer.close();
            }
        }
    }

    private void generateIndex(PrintWriter out) {
        List<DocumentedClass> units = new ArrayList<DocumentedClass>();
        List<DocumentedClass> services = new ArrayList<DocumentedClass>();
        for (DocumentedClass clazz : classes.values()) {
            if (clazz.isUnit) {
                units.add(clazz);
            } else {
                services.add(clazz);
            }
        }
        Collections.sort(units, CLASS_COMPARATOR);
        Collections.sort(services, CLASS_COMPARATOR);

        out.println("# Index");
        out.println();

        out.println("## Units");
        out.println();
        if (units.isEmpty()) {
            out.println("_None_");
            out.println();
        } else {
            for (DocumentedClass unit : units) {
                out.println("- " + unit.simpleName);
            }
        }
        out.println();

        out.println("## Services");
        out.println();
        if (services.isEmpty()) {
            out.println("_None_");
            out.println();
        } else {
            for (DocumentedClass service : services) {
                out.println("- " + service.simpleName);
            }
        }
    }

    private static final Comparator<DocumentedClass> CLASS_COMPARATOR = new Comparator<DocumentedClass>() {
        @Override
        public int compare(DocumentedClass o1, DocumentedClass o2) {
            return o1.simpleName.compareTo(o2.simpleName);
        }
    };
}
