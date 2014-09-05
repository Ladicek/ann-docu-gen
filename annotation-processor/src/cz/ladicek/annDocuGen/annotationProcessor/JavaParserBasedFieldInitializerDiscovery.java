package cz.ladicek.annDocuGen.annotationProcessor;

import cz.ladicek.annDocuGen.annotationProcessor.javaParser.JavaBaseListener;
import cz.ladicek.annDocuGen.annotationProcessor.javaParser.JavaLexer;
import cz.ladicek.annDocuGen.annotationProcessor.javaParser.JavaParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.misc.NotNull;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import java.io.InputStream;

import static cz.ladicek.annDocuGen.annotationProcessor.Utils.declaringClassOf;

public final class JavaParserBasedFieldInitializerDiscovery implements FieldInitializerDiscovery {
    private final ProcessingEnvironment processingEnv;

    public JavaParserBasedFieldInitializerDiscovery(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    @Override
    public String getFor(Element field) {
        try {
            return doGetFor(field);
        } catch (Exception e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                    "Couldn't read field initializer, ignoring", field);
            return null;
        }
    }

    // TODO don't parse the same file repeatedly
    private String doGetFor(Element field) throws Exception {
        Element clazz = declaringClassOf(field);
        FileObject file = (FileObject) clazz.getClass().getField("sourcefile").get(clazz);
        InputStream fileStream = file.openInputStream();

        final String fieldName = field.toString();
        final String[] fieldInitializer = new String[1]; // mutable from inside parser listener
        try {
            Lexer lexer = new JavaLexer(new ANTLRInputStream(fileStream));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            JavaParser parser = new JavaParser(tokens);
            parser.addParseListener(new JavaBaseListener() {
                @Override
                public void exitFieldDeclaration(@NotNull JavaParser.FieldDeclarationContext ctx) {
                    for (JavaParser.VariableDeclaratorContext var : ctx.variableDeclarators().variableDeclarator()) {
                        if (fieldName.equals(var.variableDeclaratorId().Identifier().getSymbol().getText())
                                && var.variableInitializer() != null) {
                            fieldInitializer[0] = var.variableInitializer().expression().getText();
                        }
                    }
                }
            });
            parser.compilationUnit();
        } finally {
            fileStream.close();
        }

        return fieldInitializer[0];
    }
}
