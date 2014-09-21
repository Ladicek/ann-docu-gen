package cz.ladicek.annDocuGen.annotationProcessor;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

public final class Elements {
    private Elements() {} // avoid instantitation

    public static Element declaringClassOf(Element element) {
        Element clazz = element;
        while (clazz != null && clazz.getKind() != ElementKind.CLASS) {
            clazz = clazz.getEnclosingElement();
        }
        return clazz;
    }
}
