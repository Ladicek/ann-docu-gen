package cz.ladicek.annDocuGen.annotationProcessor;

import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedAnnotations;
import cz.ladicek.annDocuGen.annotationProcessor.model.FieldInitializer;
import cz.ladicek.annDocuGen.annotationProcessor.model.FieldInitializerDiscovery;
import cz.ladicek.annDocuGen.annotationProcessor.model.Javadoc;
import cz.ladicek.annDocuGen.annotationProcessor.model.TypeName;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            DocumentedAnnotations documentedAnnotations = new DocumentedAnnotations(clazz);
            Javadoc javadoc = new Javadoc(processingEnv, clazz);
            type = new DocumentedClass(clazz.getSimpleName().toString(), fullName, isUnit, documentedAnnotations,
                    javadoc);
            classes.put(fullName, type);
        }

        return type;
    }

    public DocumentedDependency documentDependency(Element fieldOrCtorParam) {
        TypeName className = new TypeName(fieldOrCtorParam);
        DocumentedAnnotations documentedAnnotations = new DocumentedAnnotations(fieldOrCtorParam);
        Javadoc javadoc = new Javadoc(processingEnv, fieldOrCtorParam);
        return new DocumentedDependency(className, documentedAnnotations, javadoc);
    }

    public DocumentedProperty documentProperty(Element field) {
        TypeMirror fieldTypeErased = processingEnv.getTypeUtils().erasure(field.asType());
        boolean isOptional = processingEnv.getTypeUtils().isSameType(optionalTypeErased, fieldTypeErased);
        boolean isPrimitive = fieldTypeErased.getKind().isPrimitive();

        String name = field.getAnnotation(Property.class).value();
        TypeName type = new TypeName(field);
        FieldInitializer initializer = fieldInitializerDiscovery.getFor(field);
        if (!initializer.exists()) {
            if (isOptional) {
                initializer = FieldInitializer.impliedOptionalAbsent();
            } else if (isPrimitive) {
                initializer = FieldInitializer.primitiveTypeDefault(fieldTypeErased.getKind());
            }
        }
        boolean mandatory = !initializer.exists();
        Javadoc javadoc = new Javadoc(processingEnv, field);
        return new DocumentedProperty(name, type, initializer, mandatory, javadoc);
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
        Collections.sort(units, DocumentedClass.SIMPLE_NAME_COMPARATOR);
        Collections.sort(services, DocumentedClass.SIMPLE_NAME_COMPARATOR);

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
}
