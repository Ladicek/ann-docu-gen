package cz.ladicek.annDocuGen.annotationProcessor.model;

import javax.lang.model.element.Element;

public final class EncounteredClass {
    public final TypeName fullName;
    public final Element clazz;

    public EncounteredClass(Element clazz) {
        this.fullName = new TypeName(clazz);
        this.clazz = clazz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EncounteredClass that = (EncounteredClass) o;

        if (!fullName.equals(that.fullName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }
}
