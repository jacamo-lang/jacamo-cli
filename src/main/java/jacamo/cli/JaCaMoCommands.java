package jacamo.cli;

import jacamo.cli.app.Application;
import org.jline.reader.LineReader;
import picocli.CommandLine.Command;

import java.io.PrintWriter;

// program "inspired" by https://github.com/remkop/picocli/tree/v4.7.1/picocli-shell-jline3

@Command(name = "jacamo",
        // version = "1.0",
        versionProvider = VersionProvider.class,
        mixinStandardHelpOptions = true,
        subcommands = {  Application.class },
        synopsisSubcommandLabel = "(app | mas | agent | <jcm file>)"
)
public class JaCaMoCommands {

    private PrintWriter out = null;
    private PrintWriter err = null;

    public PrintWriter getOut() {
        return out;
    }

    public void println(String s) {
        if (out == null) {
            System.out.println(s);
        } else {
            out.println(s);
            out.flush();
        }
    }
    public void errorMsg(String s) {
        if (out == null) {
            System.err.println(s);
        } else {
            out.println(s);
        }
    }

    public boolean isTerminal() {
        return out != null;
    }

    public void setReader(LineReader reader) {
        out = reader.getTerminal().writer();
    }
}
