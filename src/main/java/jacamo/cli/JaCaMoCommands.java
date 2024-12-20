package jacamo.cli;

import jacamo.cli.app.Application;
import jason.cli.JasonCommands;
import org.jline.reader.LineReader;
import picocli.CommandLine.Command;

import java.io.PrintWriter;

// program "inspired" by https://github.com/remkop/picocli/tree/v4.7.1/picocli-shell-jline3
// see https://picocli.info/#_sharing_options_in_subcommands

@Command(name = "jacamo",
        // version = "1.0",
        versionProvider = VersionProvider.class,
        mixinStandardHelpOptions = true,
        //subcommands = {  Application.class }, //jason.cli.agent.Agent.class },
        synopsisSubcommandLabel = "(app | mas | agent | <jcm file>)"
)
public class JaCaMoCommands extends JasonCommands {
}
