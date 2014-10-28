package cz.ladicek.annDocuGen.mavenPlugin;

import cz.ladicek.annDocuGen.annotationProcessor.gson.GsonHolder;
import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentationData;
import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedClass;
import cz.ladicek.annDocuGen.annotationProcessor.model.TypeName;
import cz.ladicek.annDocuGen.annotationProcessor.view.DocumentationWriter;
import cz.ladicek.annDocuGen.annotationProcessor.view.FileCreator;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Mojo(name = "aggregate", defaultPhase = LifecyclePhase.PREPARE_PACKAGE,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class AnnDocuGenAggregate extends AbstractAnnDocuGenMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    /** Directory where aggregate AnnDocuGen documentation should be generated. */
    @Parameter(property = "annDocuGen.aggregateDirectory",
            defaultValue = "${project.build.directory}/annDocuGen", required = true)
    protected File aggregateDirectory;

    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("Skipping AnnDocuGen aggregation");
            return;
        }

        try {
            DocumentationData data = aggregate(collectDocumentationData());
            FileCreator fileCreator = new MavenPluginFileCreator(aggregateDirectory);
            new DocumentationWriter(data, fileCreator).write();
        } catch (IOException e) {
            failOnError("IOException: Error while creating aggregate", e);
        }
    }

    private List<DocumentationData> collectDocumentationData() throws IOException {
        List<DocumentationData> result = new ArrayList<DocumentationData>();
        for (Artifact artifact : project.getDependencyArtifacts()) {
            if ("jar".equals(artifact.getType())) {
                File artifactFile = artifact.getFile();
                if (artifactFile != null) {
                    DocumentationData documentationData = readDocumentationDataFromArtifactFile(artifactFile);
                    if (documentationData != null) {
                        getLog().info("Adding " + artifact.getGroupId() + ":" + artifact.getArtifactId()
                                + " to AnnDocuGen aggregate");
                        result.add(documentationData);
                    }
                }
            }
        }
        return result;
    }

    private DocumentationData aggregate(List<DocumentationData> allDocumentationData) {
        Set<TypeName> typeNames = new HashSet<TypeName>(); // for duplicate detection
        List<DocumentedClass> classes = new ArrayList<DocumentedClass>();

        for (DocumentationData documentationData : allDocumentationData) {
            for (DocumentedClass clazz : documentationData.documentedClasses()) {
                if (typeNames.contains(clazz.fullName)) {
                    getLog().warn("Duplicate class " + clazz.fullName + " found");
                } else {
                    typeNames.add(clazz.fullName);
                    classes.add(clazz);
                }
            }
        }

        return new DocumentationData(classes);
    }

    // ---

    private DocumentationData readDocumentationDataFromArtifactFile(File file) throws IOException {
        ZipFile zip = null;
        try {
            zip = new ZipFile(file);
            ZipEntry dataEntry = zip.getEntry("anndocugen-raw-data.json");
            if (dataEntry == null) {
                return null;
            }
            InputStream stream = zip.getInputStream(dataEntry);
            return GsonHolder.instance.fromJson(new InputStreamReader(stream, "utf-8"), DocumentationData.class);
        } finally {
            if (zip != null) {
                zip.close();
            }
        }
    }

    private static final class MavenPluginFileCreator implements FileCreator {
        private final File baseDir;

        private MavenPluginFileCreator(File baseDir) {
            this.baseDir = baseDir;
        }

        @Override
        public OutputStream newOutputStream(String path) throws IOException {
            return new FileOutputStream(ensureDirectoryExists(path));
        }

        @Override
        public Writer newWriter(String path) throws IOException {
            return new OutputStreamWriter(newOutputStream(path), "utf-8");
        }

        private File ensureDirectoryExists(String path) {
            File result = new File(baseDir, path);
            result.getParentFile().mkdirs();
            return result;
        }
    }
}
