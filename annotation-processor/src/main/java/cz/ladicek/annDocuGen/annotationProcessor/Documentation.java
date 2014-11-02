package cz.ladicek.annDocuGen.annotationProcessor;

import com.google.common.base.Optional;
import cz.ladicek.annDocuGen.annotationProcessor.model.CompilerBridge;
import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentationData;
import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedAnnotations;
import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedClass;
import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedDependency;
import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedOutputProperty;
import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedProperty;
import cz.ladicek.annDocuGen.annotationProcessor.model.EncounteredClass;
import cz.ladicek.annDocuGen.annotationProcessor.model.FieldInitializer;
import cz.ladicek.annDocuGen.annotationProcessor.model.Javadoc;
import cz.ladicek.annDocuGen.annotationProcessor.model.OutputProperties;
import cz.ladicek.annDocuGen.annotationProcessor.model.TypeName;
import cz.ladicek.annDocuGen.annotationProcessor.view.DocumentationWriter;
import cz.ladicek.annDocuGen.annotationProcessor.view.FileCreator;
import cz.ladicek.annDocuGen.api.OutputProperty;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Documentation {
    private final ProcessingEnvironment processingEnv;
    private final TypeMirror optionalTypeErased;
    private final TypeMirror unitType;
    private final TypeMirror objectType;
    private final CompilerBridge compilerBridge;
    private final OutputProperties outputProperties;
    private final Map<TypeName, DocumentedClass> classes = new HashMap<TypeName, DocumentedClass>();
    private final Set<EncounteredClass> encounteredClasses = new HashSet<EncounteredClass>();

    public Documentation(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        this.optionalTypeErased = processingEnv.getTypeUtils().erasure(
                processingEnv.getElementUtils().getTypeElement(Optional.class.getName()).asType());
        this.unitType = processingEnv.getElementUtils().getTypeElement(Unit.class.getName()).asType();
        this.objectType = processingEnv.getElementUtils().getTypeElement(Object.class.getName()).asType();
        this.compilerBridge = CompilerBridge.Factory.create(processingEnv);
        this.outputProperties = new OutputProperties(processingEnv);
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
        if (isUnit(clazz)) {
            encounteredClasses.add(new EncounteredClass(clazz));
        }
    }

    private boolean isUnit(Element clazz) {
        return processingEnv.getTypeUtils().isAssignable(clazz.asType(), unitType);
    }

    private DocumentedClass createDocumentedClass(TypeName fullName, Element clazz) {
        Optional<TypeName> parentClass = Optional.absent();
        List<? extends TypeMirror> superTypes = processingEnv.getTypeUtils().directSupertypes(clazz.asType());
        if (!superTypes.isEmpty()) {
            TypeMirror superClass = superTypes.get(0);
            if (!processingEnv.getTypeUtils().isSameType(superClass, objectType)) {
                parentClass = Optional.of(new TypeName(processingEnv.getTypeUtils().asElement(superClass)));
            }
        }

        boolean isPublic = clazz.getModifiers().contains(Modifier.PUBLIC);
        boolean isAbstract = clazz.getModifiers().contains(Modifier.ABSTRACT);
        DocumentedAnnotations documentedAnnotations = new DocumentedAnnotations(clazz);
        boolean isUnit = isUnit(clazz);
        Javadoc javadoc = new Javadoc(processingEnv, clazz);
        return new DocumentedClass(isPublic, isAbstract, clazz.getSimpleName().toString(), fullName,
                documentedAnnotations, parentClass, isUnit, javadoc);
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

    public DocumentedOutputProperty documentOutputProperty(Element field) {
        Optional<TypeMirror> outputPropertyType = outputProperties.typeOf(field);
        if (!outputPropertyType.isPresent()) {
            // this method should be only called when valid, so no need to care much about error handling
            throw new IllegalArgumentException("Type of @OutputProperty field " + field + " is not Output<T>");
        }

        String name = field.getAnnotation(OutputProperty.class).value();
        TypeName type = new TypeName(processingEnv.getTypeUtils().asElement(outputPropertyType.get()));
        Javadoc javadoc = new Javadoc(processingEnv, field);
        return new DocumentedOutputProperty(name, type, javadoc);
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
    }

    // ---

    public void generateDocumentationFiles() {
        try {
            DocumentationData data = new DocumentationData(new ArrayList<DocumentedClass>(classes.values()));
            FileCreator fileCreator = new AnnotationProcessorFileCreator();
            new DocumentationWriter(data, fileCreator).write();
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "IO problem: " + e);
        }
    }

    private final class AnnotationProcessorFileCreator implements FileCreator {
        @Override
        public OutputStream newOutputStream(String path) throws IOException {
            return createFileObject(path).openOutputStream();
        }

        @Override
        public Writer newWriter(String path) throws IOException {
            return createFileObject(path).openWriter();
        }

        private FileObject createFileObject(String path) throws IOException {
            return processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "annDocuGen", path);
        }
    }
}
