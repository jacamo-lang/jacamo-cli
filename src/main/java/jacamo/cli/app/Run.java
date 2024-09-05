package jacamo.cli.app;

import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.GradleProject;
import org.gradle.tooling.model.Task;

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

            if (gradleHasTask("buildJCMDeps", connection)) {
                getGradleBuild(connection, verbose, true)
                        .forTasks("buildJCMDeps")
                        .run();
            }
            getGradleBuild(connection, verbose, true)
                    .forTasks("run")
                    .run();
        } catch (Exception e) {
            System.err.println("Error running 'gradle run': "+e.getClass());
        }
    }

    boolean gradleHasTask(String taskName, ProjectConnection connection) {
        GradleProject project = connection.getModel(GradleProject.class);
        for (Task task : project.getTasks()) {
            if (task.getName().equals( taskName))
                return true;
        }
        return false;
    }
}
