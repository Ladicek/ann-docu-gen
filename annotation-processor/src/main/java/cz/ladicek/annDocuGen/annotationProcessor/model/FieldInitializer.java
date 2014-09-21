package cz.ladicek.annDocuGen.annotationProcessor.model;

import com.google.common.base.Optional;

import javax.lang.model.type.TypeKind;

public final class FieldInitializer {
    private final Optional<String> expressionText;

    public static FieldInitializer impliedOptionalAbsent() {
        return new FieldInitializer("Optional.absent() /* implied */");
    }

    public static FieldInitializer primitiveTypeDefault(TypeKind primitiveTypeKind) {
        switch (primitiveTypeKind) {
            case BOOLEAN:
                return new FieldInitializer("false /* primitive default */");
            case BYTE:
                return new FieldInitializer("0 /* primitive default */");
            case SHORT:
                return new FieldInitializer("0 /* primitive default */");
            case INT:
                return new FieldInitializer("0 /* primitive default */");
            case LONG:
                return new FieldInitializer("0L /* primitive default */");
            case FLOAT:
                return new FieldInitializer("0.0F /* primitive default */");
            case DOUBLE:
                return new FieldInitializer("0.0 /* primitive default */");
            case CHAR:
                return new FieldInitializer("'\\u0000' /* primitive default */");
            default:
                return new FieldInitializer(null);
        }
    }

    public FieldInitializer(String expressionText) {
        this.expressionText = Optional.fromNullable(expressionText);
    }

    public boolean exists() {
        return expressionText.isPresent();
    }

    @Override
    public String toString() {
        return expressionText.or("");
    }
}
