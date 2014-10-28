package cz.ladicek.annDocuGen.mavenPlugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

abstract class AbstractAnnDocuGenMojo extends AbstractMojo {
    /** Directory where AnnDocuGen-generated documentation resides. */
    @Parameter(property = "annDocuGen.directory",
            defaultValue = "${project.build.directory}/generated-sources/annotations/annDocuGen", required = true)
    protected File annDocuGenDirectory;

    /** Whether to skip this goal. */
    @Parameter(property = "annDocuGen.skip", defaultValue = "false")
    protected boolean skip;

    /** Whether to fail the build if an error happens. */
    @Parameter(property = "annDocuGen.failOnError", defaultValue = "true")
    protected boolean failOnError;

    // ---

    protected final boolean directoryContainsAnnDocuGenOutput(File dir) {
        return dir.exists()
                && new File(dir, "index.html").exists()
                && new File(dir, "data.json").exists()
                && new File(dir, "raw-data.json").exists();
    }

    protected final void failOnError(String prefix, Exception e) throws MojoExecutionException {
        if (failOnError) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new MojoExecutionException(prefix + ": " + e.getMessage(), e);
        }

        getLog().error(prefix + ": " + e.getMessage(), e);
    }
}
