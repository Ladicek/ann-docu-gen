package cz.ladicek.annDocuGen.annotationProcessor.model;

import javax.lang.model.element.Element;

final class DegradingVoidCompilerBridge implements CompilerBridge {
    @Override
    public boolean isSourceAvailable(Element clazz) {
        return false;
    }

    @Override
    public FieldInitializer getFieldInitializer(Element field) {
        return new FieldInitializer(null);
    }
}
