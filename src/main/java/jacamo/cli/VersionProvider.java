package jacamo.cli;

import picocli.CommandLine.IVersionProvider;

// program "inspired" by https://github.com/remkop/picocli/tree/v4.7.1/picocli-shell-jline3


public class VersionProvider implements IVersionProvider {
    public String[] getVersion() {
        return new String[] { "JaCaMo CLI " + vFromJAR() };
    }

    public static String vFromJAR() {
        return "1.2"; // TODO implement
    }
}
