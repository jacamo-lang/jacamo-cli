package jacamo.cli;

import picocli.CommandLine.IVersionProvider;

// program "inspired" by https://github.com/remkop/picocli/tree/v4.7.1/picocli-shell-jline3


public class VersionProvider implements IVersionProvider {
    public String[] getVersion() {
        return new String[] { "JaCaMo CLI " + vFromJAR() };
    }

    public static String vFromJAR() {
        Package j = Package.getPackage("jacamo.cli");
        if (j != null && j.getSpecificationVersion() != null) {
            return j.getSpecificationVersion();
        }
        return "0.0.0";
    }
}
