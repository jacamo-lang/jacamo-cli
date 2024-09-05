package jacamo.cli.app;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import java.io.File;
import java.io.IOException;

import static jacamo.cli.app.Create.copyFile;


public class Common {
    protected File getProjectFile(String masName) {
        if (!masName.isEmpty()) {
            if (!masName.endsWith(".jcm"))
                masName += ".jcm";

            var f = new File(masName);
            if (f.exists())
                return f;
        }

        // find a .jcm file in current directory
        for (var nf: new File(".").listFiles()) {
            if (nf.getName().endsWith(".jcm"))
                return nf;
        }

        return null;
    }
//    protected File createTempProjectFile(String masName) {
//        var f = new File( ".temp.mas2j");
//        CreateNewProject.copyFile("temp", "project", f, true);
//        return f;
//    }

    ProjectConnection getGradleConnection(File path) {
        return GradleConnector
                .newConnector()
                .forProjectDirectory(path)
                .connect();
    }

    BuildLauncher getGradleBuild(ProjectConnection conn) {
        return getGradleBuild(conn, true, true);
    }
    BuildLauncher getGradleBuild(ProjectConnection conn, boolean setStdOut, boolean setStdErr) {
        var b = conn.newBuild();
        if (setStdOut) b.setStandardOutput(System.out);
        if (setStdErr) b.setStandardError(System.err);
        return b;
    }

    boolean getOrCreateGradleFile(String masName) {
        var masFile = getProjectFile(masName);
        if (masFile == null)
            masFile = new File(".");
        var projectDir = masFile.getAbsoluteFile().getParentFile();
        try {
            projectDir = projectDir.getCanonicalFile();
        } catch (IOException e) {  }

        // checks settings.gradle
        var sets = new File(projectDir+"/settings.gradle");
        if (!sets.exists()) {
            try {
                sets.createNewFile();
            } catch (IOException e) {}
        }

        // checks jcm-deps
        var deps = new File(projectDir+"/.jcm-deps.gradle");
        if (!deps.exists()) {
            try {
                copyFile(masName,"jcm-deps.gradle", deps, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        var f = new File(projectDir+"/build.gradle");
        if (f.exists())
            return false;

        if (masName.isEmpty()) {
            // masName based on directory name
            masName = projectDir.getName();
        }
        var p = masName.lastIndexOf(File.separatorChar);
        if (p>=0) {
            masName = masName.substring(p+1);
        }
        if (masName.endsWith(".jcm")) {
            masName = masName.substring(0,masName.length()-4);
        }

        // create a temp file
        //f = new File(projectDir+"/.build-temp.gradle"); // does not work with gradle
        copyFile(masName, "build.gradle", f, true);
        return true;
    }
}   

