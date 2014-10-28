package cz.ladicek.annDocuGen.mavenPlugin;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;

import java.io.File;
import java.io.IOException;

@Mojo(name = "jar", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE)
public class AnnDocuGenJar extends AbstractAnnDocuGenMojo {
    @Component
    private MavenProjectHelper projectHelper;

    @Component(role = Archiver.class, hint = "jar")
    private JarArchiver jarArchiver;

    @Parameter
    private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

    @Parameter(defaultValue = "${session}", required = true, readonly = true)
    private MavenSession session;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(property = "project.build.directory", required = true, readonly = true)
    private String outputDirectory;

    @Parameter(property = "project.build.finalName", required = true, readonly = true)
    private String finalName;

    /** Whether to attach the generated file to project's artifacts. */
    @Parameter(property = "annDocuGen.attach", defaultValue = "true")
    private boolean attach;

    /** The classifier of the generated artifact. */
    @Parameter(property = "annDocuGen.classifier", defaultValue = "anndocugen", required = true)
    private String classifier;

    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("Skipping AnnDocuGen archivation");
            return;
        }

        if (!"pom".equalsIgnoreCase(project.getPackaging())) {
            ArtifactHandler artifactHandler = project.getArtifact().getArtifactHandler();
            if (!"java".equals(artifactHandler.getLanguage())) {
                getLog().info("Not archiving AnnDocuGen as the project is not a Java classpath-capable package");
                return;
            }
        }

        try {
            File outputFile = generateArchive(annDocuGenDirectory, finalName + "-" + classifier + ".jar");

            if (!attach) {
                getLog().info("NOT adding AnnDocuGen JAR to attached artifacts list");
            } else {
                projectHelper.attachArtifact(project, outputFile, classifier);
            }
        } catch (ArchiverException e) {
            failOnError("ArchiverException: Error while creating archive", e);
        } catch (IOException e) {
            failOnError("IOException: Error while creating archive", e);
        } catch (RuntimeException e) {
            failOnError("RuntimeException: Error while creating archive", e);
        }
    }

    private File generateArchive(File annDocuGenDirectory, String jarFileName) throws ArchiverException, IOException {
        File annDocuGenJar = new File(outputDirectory, jarFileName);

        if (annDocuGenJar.exists()) {
            annDocuGenJar.delete();
        }

        MavenArchiver archiver = new MavenArchiver();
        archiver.setArchiver(jarArchiver);
        archiver.setOutputFile(annDocuGenJar);

        if (!directoryContainsAnnDocuGenOutput(annDocuGenDirectory)) {
            getLog().warn("JAR will be empty, no AnnDocuGen found in " + annDocuGenDirectory);
        } else {
            archiver.getArchiver().addDirectory(annDocuGenDirectory);
        }

        try {
            archive.setAddMavenDescriptor(false);
            archiver.createArchive(session, project, archive);
        } catch (ManifestException e) {
            throw new ArchiverException("ManifestException: " + e.getMessage(), e);
        } catch (DependencyResolutionRequiredException e) {
            throw new ArchiverException("DependencyResolutionRequiredException: " + e.getMessage(), e);
        }

        return annDocuGenJar;
    }
}
