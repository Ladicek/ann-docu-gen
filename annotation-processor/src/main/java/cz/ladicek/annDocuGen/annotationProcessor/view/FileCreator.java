package cz.ladicek.annDocuGen.annotationProcessor.view;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public interface FileCreator {
    /**
     * Creates a file at given {@code path} and opens an {@link OutputStream}. It is the caller's responsibility
     * to close the stream.
     */
    OutputStream newOutputStream(String path) throws IOException;

    /**
     * Creates a file at given {@code path} and opens a {@link Writer}. It is the caller's responsibility
     * to close the writer.
     */
    Writer newWriter(String path) throws IOException;
}
