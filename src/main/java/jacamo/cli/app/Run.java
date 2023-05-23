package jacamo.cli.app;

import java.io.File;


/** runs a mas2j using gradle */
public class Run extends Common {

    public void run(String jcm, boolean verbose) {
        // use gradle to run
        var jcmFile = new File(jcm);
        if (!jcmFile.exists()) {
            System.err.println("the application file "+jcm+" does not exist!");
            return;
        }
        var projectDir = jcmFile.getAbsoluteFile().getParentFile();

        var created = getOrCreateGradleFile( jcm );

        var jcmDepsFile = new File(projectDir.getAbsoluteFile()+"/.jcm-deps.gradle");
        if (!jcmDepsFile.exists()) {
            Create.copyFile(jcm,"jcm-deps.gradle", new File( projectDir + "/.jcm-deps.gradle"), true);
        }

        try (var connection = getGradleConnection(projectDir)) {
            getGradleBuild(connection, verbose, true)
                    .forTasks("buildJCMDeps")
                    .run();
            getGradleBuild(connection, verbose, true)
                    .forTasks("run")
                    .run();
        } catch (Exception e) {
            System.err.println("Error running 'gradle run'");
        }
    }
}
