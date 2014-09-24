package cz.ladicek.annDocuGen.annotationProcessor.model;

import javax.lang.model.element.Element;

public final class EncounteredDependency {
    public final TypeName fullName;
    public final Element clazz;

    public EncounteredDependency(Element clazz) {
        this.fullName = new TypeName(clazz);
        this.clazz = clazz;
    }

    public static boolean isValid(Element encounteredClass) {
        if (encounteredClass == null) {
            return false;
        }

        String fullName = encounteredClass.toString();
        if (fullName.startsWith("java.") || fullName.startsWith("com.google.")) {
            return false;
        }

        return true;
    }

    // ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EncounteredDependency that = (EncounteredDependency) o;

        if (!fullName.equals(that.fullName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }
}
