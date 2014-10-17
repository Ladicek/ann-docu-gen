package cz.ladicek.annDocuGen.annotationProcessor.model;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public interface CompilerBridge {
    boolean isSourceAvailable(Element clazz);
    FieldInitializer getFieldInitializer(Element field);

    public static final class Factory {
        public static CompilerBridge create(ProcessingEnvironment processingEnv) {
            try {
                Class.forName("com.sun.source.util.Trees");
                return new CompilerTreeApi(processingEnv);
            } catch (Throwable e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING,
                        "Compiler Tree API not available, generated documentation might be degraded"
                                + " (this requires Sun Java Compiler and tools.jar on the classpath)");

                return new DegradingVoidCompilerBridge();
            }
        }
    }
}
