package cz.ladicek.annDocuGen.mavenPlugin;

import cz.ladicek.annDocuGen.annotationProcessor.gson.GsonHolder;
import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentationData;
import cz.ladicek.annDocuGen.annotationProcessor.model.DocumentedClass;
import cz.ladicek.annDocuGen.annotationProcessor.model.TypeName;
import cz.ladicek.annDocuGen.annotationProcessor.view.DocumentationWriter;
import cz.ladicek.annDocuGen.annotationProcessor.view.FileCreator;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.artifact.filter.PatternIncludesArtifactFilter;

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

    /**
     * A list of dependency patterns for artifacts that will always be included. If such artifact doesn't contain
     * AnnDocuGen output, it will be treated as an error.
     */
    @Parameter
    private List<String> includes;

    /**
     * A list of dependency patterns for artifacts that will always be excluded, even if they contain AnnDocuGen output.
     */
    @Parameter
    private List<String> excludes;

    private ArtifactFilter includesFilter;
    private ArtifactFilter excludesFilter;

    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("Skipping AnnDocuGen aggregation");
            return;
        }

        try {
            setUpFilters();
            List<DocumentationData> allDocumentationData = collectDocumentationData();
            DocumentationData data = aggregate(allDocumentationData);
            FileCreator fileCreator = new MavenPluginFileCreator(aggregateDirectory);
            new DocumentationWriter(data, fileCreator).write();
        } catch (IOException e) {
            failOnError("IOException: Error while creating aggregate", e);
        } catch (AnnDocuGenMissingException e) {
            failOnError("AnnDocuGen missing", e);
        }
    }

    private void setUpFilters() {
        if (includes != null && !includes.isEmpty()) {
            includesFilter = new PatternIncludesArtifactFilter(includes);
        }

        if (excludes != null && !excludes.isEmpty()) {
            // this is in fact correct, as I need to test if an artifact was _included_ in the "exclusion set"
            excludesFilter = new PatternIncludesArtifactFilter(excludes);
        }
    }

    private List<DocumentationData> collectDocumentationData() throws IOException, AnnDocuGenMissingException {
        List<DocumentationData> result = new ArrayList<DocumentationData>();
        for (Artifact artifact : project.getDependencyArtifacts()) {
            boolean includedExplicitly = includesFilter != null && includesFilter.include(artifact);
            boolean excludedExplicitly = excludesFilter != null && excludesFilter.include(artifact);

            if (excludedExplicitly) {
                getLog().info("Artifact " + artifact.getGroupId() + ":" + artifact.getArtifactId()
                        + ":" + artifact.getVersion() + " excluded");
                continue;
            }

            if ("jar".equals(artifact.getType())) {
                File artifactFile = artifact.getFile();
                if (artifactFile != null) {
                    DocumentationData documentationData = readDocumentationDataFromArtifactFile(artifactFile);

                    if (includedExplicitly && documentationData == null) {
                        throw new AnnDocuGenMissingException("Artifact " + artifact.getGroupId() + ":"
                                + artifact.getArtifactId() + ":" + artifact.getVersion()
                                + " doesn't contain AnnDocuGen output, but it was included explicitly");
                    }

                    if (documentationData != null) {
                        getLog().info("Artifact " + artifact.getGroupId() + ":" + artifact.getArtifactId()
                                + ":" + artifact.getVersion() + " included");
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
