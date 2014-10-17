package cz.ladicek.annDocuGen.annotationProcessor;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import cz.ladicek.annDocuGen.annotationProcessor.model.CompilerBridge;
import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedAnnotations;
import cz.ladicek.annDocuGen.annotationProcessor.model.EncounteredClass;
import cz.ladicek.annDocuGen.annotationProcessor.model.FieldInitializer;
import cz.ladicek.annDocuGen.annotationProcessor.model.Javadoc;
import cz.ladicek.annDocuGen.annotationProcessor.model.TypeName;
import cz.ladicek.annDocuGen.api.Property;
import cz.ladicek.annDocuGen.api.Unit;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
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
    private final ProcessingEnvironment processingEnv;
    private final TypeMirror optionalTypeErased;
    private final TypeMirror unitType;
    private final CompilerBridge compilerBridge;
    private final Map<TypeName, DocumentedClass> classes = new HashMap<TypeName, DocumentedClass>();
    private final Set<EncounteredClass> encounteredClasses = new HashSet<EncounteredClass>();

    public Documentation(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        this.optionalTypeErased = processingEnv.getTypeUtils().erasure(
                processingEnv.getElementUtils().getTypeElement(Optional.class.getName()).asType());
        this.unitType = processingEnv.getElementUtils().getTypeElement(Unit.class.getName()).asType();
        this.compilerBridge = CompilerBridge.Factory.create(processingEnv);
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

    public void encounterRootClass(Element clazz) {
        // from the root set, only encounter unit classes; services will be encountered later, if they are used
        boolean isUnit = processingEnv.getTypeUtils().isAssignable(clazz.asType(), unitType)
                && !clazz.getModifiers().contains(Modifier.ABSTRACT);
        if (isUnit) {
            encounteredClasses.add(new EncounteredClass(clazz));
        }
    }

    private DocumentedClass createDocumentedClass(TypeName fullName, Element clazz) {
        boolean isUnit = processingEnv.getTypeUtils().isAssignable(clazz.asType(), unitType);
        DocumentedAnnotations documentedAnnotations = new DocumentedAnnotations(clazz);
        Javadoc javadoc = new Javadoc(processingEnv, clazz);
        return new DocumentedClass(clazz.getSimpleName().toString(), fullName, isUnit, documentedAnnotations, javadoc);
    }

    public DocumentedDependency documentDependency(Element fieldOrCtorParam) {
        Element dependencyClass = processingEnv.getTypeUtils().asElement(fieldOrCtorParam.asType());
        if (compilerBridge.isSourceAvailable(dependencyClass)) {
            encounteredClasses.add(new EncounteredClass(dependencyClass));
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
        FieldInitializer initializer = compilerBridge.getFieldInitializer(field);
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
        for (EncounteredClass encounteredClass : encounteredClasses) {
            TypeName fullName = encounteredClass.fullName;
            if (classes.containsKey(fullName)) {
                continue; // was already documented
            }

            DocumentedClass documentedClass = createDocumentedClass(fullName, encounteredClass.clazz);
            classes.put(fullName, documentedClass);
        }

        encounteredClasses.clear();

        Set<TypeName> allDocumentedClasses = classes.keySet();
        for (DocumentedClass documentedClass : classes.values()) {
            documentedClass.markDependenciesThatAreDocumentedClasses(allDocumentedClasses);
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
            FileObject file = createFileObject("index.html");
            Writer writer = file.openWriter();
            try {
                generateIndex(template, writer, staticContext);
            } finally {
                writer.close();
            }
        }

        Mustache template = mustache.compile("class.mustache");
        for (DocumentedClass documentedClass : classes.values()) {
            FileObject file = createFileObject(documentedClass.fullName + ".html");
            Writer writer = file.openWriter();
            try {
                template.execute(writer, new Object[] {documentedClass, staticContext});
            } finally {
                writer.close();
            }
        }
    }

    private void copyStaticAsset(String filePath) throws IOException {
        FileObject file = createFileObject(filePath);
        OutputStream outputStream = file.openOutputStream();
        try {
            URL url = Resources.getResource(Documentation.class, "/" + filePath);
            Resources.copy(url, outputStream);
        } finally {
            outputStream.close();
        }
    }

    private FileObject createFileObject(String path) throws IOException {
        return processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "annDocuGen", path);
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
