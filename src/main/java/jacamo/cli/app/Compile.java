package jacamo.cli.app;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;


@Command(
    name = "compile",
    description = "compiles the java classes of the application in the current directory (using Gradle) and produces a jar file with all necessary to run the application"
)
public class Compile extends Common implements Runnable {

    @CommandLine.ParentCommand
    protected Application parent;

    @Override
    public void run() {
        var created = getOrCreateGradleFile("");

        try (var connection = getGradleConnection(new File("." ))) {
            getGradleBuild(connection)
                    .forTasks("jar")
                    .run();

            getGradleBuild(connection)
                    .forTasks("shadowJar")
                    .run();

            if (created) {
                // delete created files
                new File("build.gradle").delete();
                new File("settings.gradle").delete();
            }
        } catch(Exception e) {
            parent.parent.errorMsg("error compiling");
        }
    }
}

