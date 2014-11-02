package cz.ladicek.annDocuGen.annotationProcessor.model;

import com.google.common.base.Optional;
import cz.ladicek.annDocuGen.api.Output;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;

public final class OutputProperties {
    private final ProcessingEnvironment processingEnv;
    private final TypeMirror outputTypeErased;

    public OutputProperties(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        this.outputTypeErased = processingEnv.getTypeUtils().erasure(
                processingEnv.getElementUtils().getTypeElement(Output.class.getName()).asType());
    }

    public Optional<TypeMirror> typeOf(Element field) {
        TypeMirror fieldType = field.asType();
        TypeMirror fieldTypeErased = processingEnv.getTypeUtils().erasure(fieldType);
        if (!processingEnv.getTypeUtils().isSameType(outputTypeErased, fieldTypeErased)) {
            return Optional.absent();
        }

        if (fieldType.getKind() != TypeKind.DECLARED) {
            return Optional.absent();
        }

        DeclaredType declaredType = (DeclaredType) fieldType;
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        if (typeArguments.size() != 1) {
            return Optional.absent();
        }

        return Optional.of(typeArguments.get(0));
    }
}
