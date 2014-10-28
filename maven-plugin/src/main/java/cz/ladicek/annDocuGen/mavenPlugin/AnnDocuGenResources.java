package cz.ladicek.annDocuGen.mavenPlugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.shared.utils.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Mojo(name = "resources", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class AnnDocuGenResources extends AbstractAnnDocuGenMojo {
    /** The output directory into which to copy the AnnDocuGen resources. */
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("Skipping AnnDocuGen resources copying");
            return;
        }

        if (!directoryContainsAnnDocuGenOutput(annDocuGenDirectory)) {
            getLog().warn("No resources will be copied, no AnnDocuGen found in " + annDocuGenDirectory);
            return;
        }

        try {
            getLog().info("Copying AnnDocuGen resources");
            copy("data.json");
            copy("raw-data.json");
        } catch (IOException e) {
            failOnError("IOException: Error while copying resources", e);
        }
    }

    private void copy(String fileName) throws IOException {
        FileUtils.copyFile(new File(annDocuGenDirectory, fileName),
                new File(outputDirectory, "anndocugen-" + fileName));
    }
}
