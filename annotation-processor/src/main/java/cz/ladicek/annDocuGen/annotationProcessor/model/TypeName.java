package cz.ladicek.annDocuGen.annotationProcessor.model;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TypeName {
    private final String fullyQualifiedName;

    public TypeName(Element element) {
        this.fullyQualifiedName = element.asType().toString();
    }

    @Override
    public String toString() {
        return fullyQualifiedName;
    }

    public String simpleName() {
        return shorten(fullyQualifiedName);
    }

    private static final Pattern TYPE = Pattern.compile("(\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*\\.)+(\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*)");

    static String shorten(String string) {
        Matcher matcher = TYPE.matcher(string);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, matcher.group(2));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
