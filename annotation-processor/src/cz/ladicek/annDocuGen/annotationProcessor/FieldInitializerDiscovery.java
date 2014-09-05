package cz.ladicek.annDocuGen.annotationProcessor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public interface FieldInitializerDiscovery {
    String getFor(Element field);

    public static final FieldInitializerDiscovery VOID = new FieldInitializerDiscovery() {
        @Override
        public String getFor(Element field) {
            return null;
        }
    };

    public static final class Factory {
        public static FieldInitializerDiscovery create(ProcessingEnvironment processingEnv) {
            try {
                Class.forName("com.sun.tools.javac.code.Symbol");
                Class.forName("org.antlr.v4.runtime.Token");
                return new JavaParserBasedFieldInitializerDiscovery(processingEnv);
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                        "Documentation is not going to contain property initializers");
                return VOID;
            }
        }
    }
}
