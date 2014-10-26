package cz.ladicek.annDocuGen.annotationProcessor.view;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.google.gson.GsonBuilder;
import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentationData;
import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedClass;
import cz.ladicek.annDocuGen.annotationProcessor.model.SearchData;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DocumentationWriter {
    private final DocumentationData documentationData;
    private final FileCreator fileCreator;

    public DocumentationWriter(DocumentationData documentationData, FileCreator fileCreator) {
        this.documentationData = documentationData;
        this.fileCreator = fileCreator;
    }

    public void write() throws IOException {
        copyStaticAsset("thirdparty/bootstrap.css");
        copyStaticAsset("style.css");

        copyStaticAsset("thirdparty/jquery.js");
        copyStaticAsset("thirdparty/typeahead.js");
        copyStaticAsset("index-search.js");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        ImmutableMap<String, Object> staticContext = ImmutableMap.<String, Object>builder()
                .put("now", dateFormat.format(new Date()))
                .build();

        MustacheFactory mustache = new DefaultMustacheFactory();

        {
            Mustache template = mustache.compile("index.mustache");
            Writer writer = fileCreator.newWriter("index.html");
            try {
                generateIndex(template, writer, staticContext);
            } finally {
                writer.close();
            }
        }

        Mustache template = mustache.compile("class.mustache");
        for (DocumentedClass documentedClass : documentationData.documentedClasses()) {
            Writer writer = fileCreator.newWriter(documentedClass.fullName + ".html");
            try {
                template.execute(writer, new Object[] {documentedClass, staticContext});
            } finally {
                writer.close();
            }
        }

        generateSearchData();
    }

    private void copyStaticAsset(String filePath) throws IOException {
        OutputStream outputStream = fileCreator.newOutputStream(filePath);
        try {
            URL url = Resources.getResource(DocumentationWriter.class, "/" + filePath);
            Resources.copy(url, outputStream);
        } finally {
            outputStream.close();
        }
    }

    private void generateIndex(Mustache template, Writer out, ImmutableMap<String, Object> staticContext) {
        List<DocumentedClass> units = new ArrayList<DocumentedClass>();
        List<DocumentedClass> services = new ArrayList<DocumentedClass>();
        for (DocumentedClass clazz : documentationData.documentedClasses()) {
            if (clazz.isUnit) {
                units.add(clazz);
            } else {
                services.add(clazz);
            }
        }
        Collections.sort(units, DocumentedClass.SIMPLE_NAME_COMPARATOR);
        Collections.sort(services, DocumentedClass.SIMPLE_NAME_COMPARATOR);

        ImmutableMap<String, Object> context = ImmutableMap.<String, Object>builder()
                .put("title", "Index")
                .put("units", units)
                .put("services", services)
                .putAll(staticContext)
                .build();
        template.execute(out, context);
    }

    private void generateSearchData() throws IOException {
        List<SearchData> searchData = new ArrayList<SearchData>();
        for (DocumentedClass documentedClass : documentationData.documentedClasses()) {
            searchData.add(new SearchData(documentedClass));
        }

        Writer writer = fileCreator.newWriter("search.json");
        try {
            new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(searchData, writer);
        } finally {
            writer.close();
        }
    }
}
