package cz.ladicek.annDocuGen.annotationProcessor.model;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public interface FieldInitializerDiscovery {
    FieldInitializer getFor(Element field);

    public static final FieldInitializerDiscovery VOID = new FieldInitializerDiscovery() {
        @Override
        public FieldInitializer getFor(Element field) {
            return new FieldInitializer(null);
        }
    };

    public static final class Factory {
        public static FieldInitializerDiscovery create(ProcessingEnvironment processingEnv) {
            try {
                Class.forName("com.sun.source.util.Trees");
                return new CompilerTreeApiBasedFieldInitializerDiscovery(processingEnv);
            } catch (Throwable e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                        "Documentation is not going to contain property initializers"
                                + " (this is only supported with Sun Java Compiler and tools.jar on the classpath)");
                return VOID;
            }
        }
    }
}
