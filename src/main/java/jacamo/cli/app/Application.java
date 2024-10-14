package jacamo.cli.app;

import jacamo.cli.JaCaMoCommands;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "app",
    description = "commands to handle (the sources of) applications",
    subcommands = { Create.class, Compile.class  },
    synopsisSubcommandLabel = "(create | compile)"
)
public class Application {

    @CommandLine.ParentCommand
    protected JaCaMoCommands parent;

}

