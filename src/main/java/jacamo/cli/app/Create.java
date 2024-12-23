package jacamo.cli.app;

import jacamo.cli.JaCaMoCLI;
import jacamo.cli.VersionProvider;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


@Command(
    name = "create",
    description = "creates the files of a new application"
)
public class Create extends Common implements Runnable {

    @CommandLine.Parameters(paramLabel = "<MAS name>", defaultValue = "",
            arity = "1")
    String masName;

    @CommandLine.Option(names = { "--console" }, defaultValue = "false", description = "output will be sent to the console instead of a GUI")
    boolean console;

    @CommandLine.ParentCommand
    protected Application parent;

    @Override
    public void run() {
        if (masName.isEmpty()) {
            parent.parent.errorMsg("the name of the MAS should be informed, e.g., 'app create my_app1'.");
            return;
        }

//        try {
//            if (!ASSyntax.parseTerm(masName).isAtom()) {
//                parent.parent.errorMsg("the name of the MAS should be a valid identifier (an atom).");
//                return;
//            }
//        } catch (Exception e) {
//            parent.parent.errorMsg("the name of the MAS should be a valid identifier (an atom).");
//            return;
//        }
//
        try {
            var path = new File(masName);
            var p = masName.lastIndexOf("/");
            if (p > 0) {
                masName = masName.substring(p+1);
            }
            if (path.exists()) {
                parent.parent.errorMsg("a directory for application '"+masName+"' exists already, chose another name for your MAS.");
                return;
            }

            createDirs(path);
            copyFiles(masName, path, console);
            createGradle(path);
            usage(masName, path);
        } catch(Exception e) {
            parent.parent.errorMsg("error creating project: " + e);
            e.printStackTrace();
        }
    }

    void createGradle(File path) {
        try (var connection = getGradleConnection(path)) {
            getGradleBuild(connection)
                    .forTasks("wrapper")
                    .run();
        } catch (Exception e) {
            System.err.println("Error creating gradle wrapper");
        }
    }

    void usage(String masName, File path) {
        parent.parent.println("\nYou can run your application with:");
        parent.parent.println("   $ jacamo "+path+"/"+masName+".jcm");
    }

    void createDirs(File path) {
        if (!path.exists()) {
            System.out.println("Creating directory "+path);
            path.mkdirs();
        }
        new File(path + "/doc").mkdirs();
        new File(path + "/lib").mkdirs();
        new File(path + "/log").mkdirs();
        new File(path + "/src/agt").mkdirs();
        new File(path + "/src/agt/inc").mkdirs();
        //new File(path + "/src/agt/jia").mkdirs();
        new File(path + "/src/env/example").mkdirs();
        new File(path + "/src/org").mkdirs();
        //new File(path + "/src/int").mkdirs();
        new File(path + "/src/test/agt").mkdirs();
    }

    void copyFiles(String masName, File path, boolean console) {
        copyFile(masName, "logging.properties", new File( path + "/logging.properties"), console);

        copyFile(masName,"project.jcm", new File( path+"/"+masName+".jcm"), console);
        copyFile(masName,"agent.asl", new File( path + "/src/agt/sample_agent.asl"), console);

        File f = new File(path+"/src/env/example/Counter.java");
        copyFile(masName,"CArtAgOartifact", f, console);
        copyFile(masName,"organization.xml", new File( path + "/src/org/org.xml"), console);
        copyFile(masName,"build.gradle", new File( path + "/build.gradle"), console);

        copyFile(masName,"test.asl",       new File( path + "/src/test/agt/test-sample.asl"), console);
        copyFile(masName,"tests.jcm", new File( path + "/src/test/tests.jcm"), console);

        copyFile(masName,"jcm-deps.gradle", new File( path + "/.jcm-deps.gradle"), console);
        copyFile(masName,"settings.gradle", new File( path + "/settings.gradle"), console);

    }

    public static void copyFile(String id, String source, File target, boolean consoleApp) {
        try (var in  = new BufferedReader(new InputStreamReader( getDefaultResource(source) ));
             var out = new BufferedWriter(new FileWriter(target))) {
            String l = in.readLine();
            while (l != null) {
                l = l.replace("<PROJECT_NAME>", id);
                l = l.replace("<PROJECT-FILE>", id+".jcm");
                l = l.replace("<PROJECT-FILE>", id+".jcm");
                l = l.replace("<PROJECT-FILE>", id+".jcm");
                l = l.replace("<PLATFORM>", "");

                l = l.replace("<PROJECT-RUNNER-CLASS>", "jacamo.infra.JaCaMoLauncher");

                l = l.replace("<DEFAULT_AGENT>", "agent bob: sample_agent.asl {\n      focus: w.c1 \n    }");
                l = l.replace("<AG_NAME>", "bob");

                l = l.replace("<VERSION>", VersionProvider.vFromJAR());
                l = l.replace("<DATE>", new SimpleDateFormat("MMMM dd, yyyy - HH:mm:ss").format(new Date()));

                if (consoleApp) {
                    l = l.replace("handlers = jason.runtime.MASConsoleLogHandler", "#handlers = jason.runtime.MASConsoleLogHandler");
                    l = l.replace("#handlers= java.util.logging.ConsoleHandler", "handlers= java.util.logging.ConsoleHandler");
                }

                l = l.replace("<PCK>", "example");
                l = l.replace("<ARTIFACT_NAME>", "Counter");
                l = l.replace("<SUPER_CLASS>", "Artifact");
                l = l.replace("<JCM_ENV>", "workspace w {\n      artifact c1: example.Counter(3) \n    }");

                l = l.replace("<JCM_ORG>", "organisation o: org.xml {\n      group g1 : group1 {\n        players: bob role1\n      }\n    }");

                l = l.replace("<ORGANIZATION_NAME>", id);
                l = l.replace("<DEPENDENCIES>", "");

                out.append(l+"\n");
                l = in.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static InputStream getDefaultResource(String templateName) throws IOException {
        return JaCaMoCLI.class.getResource("/jcm_templates/"+templateName).openStream();
    }

}

