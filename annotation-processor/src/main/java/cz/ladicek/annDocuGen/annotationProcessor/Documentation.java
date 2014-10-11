package cz.ladicek.annDocuGen.annotationProcessor;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedAnnotations;
import cz.ladicek.annDocuGen.annotationProcessor.model.EncounteredDependency;
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
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Documentation {
    private static final String OPTIONAL_TYPE_FQN = "com.google.common.base.Optional";
    private static final String UNIT_TYPE_FQN = "cz.ladicek.annDocuGen.api.Unit";

    private final ProcessingEnvironment processingEnv;
    private final TypeMirror optionalTypeErased;
    private final TypeMirror unitType;
    private final FieldInitializerDiscovery fieldInitializerDiscovery;
    private final Map<TypeName, DocumentedClass> classes = new HashMap<TypeName, DocumentedClass>();
    private final Set<EncounteredDependency> encounteredDependencies = new HashSet<EncounteredDependency>();

    public Documentation(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        this.optionalTypeErased = processingEnv.getTypeUtils().erasure(
                processingEnv.getElementUtils().getTypeElement(OPTIONAL_TYPE_FQN).asType());
        this.unitType = processingEnv.getElementUtils().getTypeElement(UNIT_TYPE_FQN).asType();
        this.fieldInitializerDiscovery = FieldInitializerDiscovery.Factory.create(processingEnv);
    }

    public DocumentedClass documentClass(Element clazz) {
        TypeName fullName = new TypeName(clazz);

        DocumentedClass documentedClass = classes.get(fullName);
        if (documentedClass == null) {
            documentedClass = createDocumentedClass(fullName, clazz);
            classes.put(fullName, documentedClass);
        }

        return documentedClass;
    }

    private DocumentedClass createDocumentedClass(TypeName fullName, Element clazz) {
        boolean isUnit = processingEnv.getTypeUtils().isAssignable(clazz.asType(), unitType);
        DocumentedAnnotations documentedAnnotations = new DocumentedAnnotations(clazz);
        Javadoc javadoc = new Javadoc(processingEnv, clazz);
        return new DocumentedClass(clazz.getSimpleName().toString(), fullName, isUnit, documentedAnnotations, javadoc);
    }

    public DocumentedDependency documentDependency(Element fieldOrCtorParam) {
        Element dependencyClass = processingEnv.getTypeUtils().asElement(fieldOrCtorParam.asType());
        if (EncounteredDependency.isValid(dependencyClass)) {
            encounteredDependencies.add(new EncounteredDependency(dependencyClass));
        }

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

    public void processEncounteredDependencies() {
        for (EncounteredDependency encounteredDependency : encounteredDependencies) {
            TypeName fullName = encounteredDependency.fullName;
            if (classes.containsKey(fullName)) {
                continue; // was already documented
            }

            DocumentedClass documentedClass = createDocumentedClass(fullName, encounteredDependency.clazz);
            classes.put(fullName, documentedClass);
        }

        Set<TypeName> allDocumentedClasses = classes.keySet();
        for (DocumentedClass documentedClass : classes.values()) {
            documentedClass.markDependenciesThatAreDocumentedAsClasses(allDocumentedClasses);
        }
    }

    // ---

    public void generateDocumentationFiles() {
        try {
            doGenerateDocumentationFiles();
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "IO problem: " + e);
        }
    }

    private void doGenerateDocumentationFiles() throws IOException {
        copyStaticAsset("thirdparty/bootstrap.css");
        copyStaticAsset("style.css");

        copyStaticAsset("thirdparty/jquery.js");
        copyStaticAsset("thirdparty/underscore.js");
        copyStaticAsset("index-filter.js");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        ImmutableMap<String, Object> staticContext = ImmutableMap.<String, Object>builder()
                .put("now", dateFormat.format(new Date()))
                .build();

        MustacheFactory mustache = new DefaultMustacheFactory();

        {
            Mustache template = mustache.compile("index.mustache");
            FileObject file = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "annDocuGen",
                    "index.html");
            Writer writer = file.openWriter();
            try {
                generateIndex(template, writer, staticContext);
            } finally {
                writer.close();
            }
        }

        Mustache template = mustache.compile("class.mustache");
        for (DocumentedClass documentedClass : classes.values()) {
            FileObject file = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "annDocuGen",
                    documentedClass.fullName + ".html");
            Writer writer = file.openWriter();
            try {
                template.execute(writer, new Object[] {documentedClass, staticContext});
            } finally {
                writer.close();
            }
        }
    }

    private void copyStaticAsset(String filePath) throws IOException {
        FileObject file = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "annDocuGen",
                filePath);
        OutputStream outputStream = file.openOutputStream();
        try {
            URL url = Resources.getResource(Documentation.class, "/" + filePath);
            Resources.copy(url, outputStream);
        } finally {
            outputStream.close();
        }
    }

    private void generateIndex(Mustache template, Writer out, ImmutableMap<String, Object> staticContext) {
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

        ImmutableMap<String, Object> context = ImmutableMap.<String, Object>builder()
                .put("title", "Index")
                .put("units", units)
                .put("services", services)
                .putAll(staticContext)
                .build();
        template.execute(out, context);
    }
}
