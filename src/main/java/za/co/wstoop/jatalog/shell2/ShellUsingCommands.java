package za.co.wstoop.jatalog.shell2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Option;

import za.co.wstoop.jatalog.DatalogException;
import za.co.wstoop.jatalog.Jatalog;
import za.co.wstoop.jatalog.output.DefaultQueryOutput;
import za.co.wstoop.jatalog.output.QueryOutput;
import za.co.wstoop.jatalog.shell2.ShellCommands.IShellCommand;

import static za.co.wstoop.jatalog.shell2.ShellCommands.IShellCommand.CONTINUE;
import static za.co.wstoop.jatalog.shell2.ShellCommands.IShellCommand.EXIT;

/**
 * Shell for Jatalog. This class contains a {@link #main(String...)} method that
 * uses picocli for parsing command line arguments.
 *
 * <ul>
 * <li> if supplied with a list of filenames will execute each one in turn
 * <li> interative mode is enabled presents the user with an interactive
 * Read-Evaluate-Print-Loop (REPL) through which the user can execute Datalog
 * statements (using {@code System.in} and {@code System.out}).
 * </ul>
 */
@CommandLine.Command(name = "DatalogCli",
        mixinStandardHelpOptions = true,
        showAtFileInUsageHelp = true,
        showDefaultValues = true,
        version = "DatalogCli 0.1-SNAPSHOT",
        description = "Run datalog from the command line%n"
)
public final class ShellUsingCommands implements Callable<Integer> {

    @Option(names = {"-i", "--interactive"},
            defaultValue = "false",
            description = "Run in interactive mode"
    )
    private boolean interactiveActive;

    @Option(names = {"-l", "--load-file"},
            split = ",",
            description = "Load datalog statements from specified file.")
    private List<File> loadFileList;

    //---
    final HistoryOfCommands historyOfCommands;
    final Jatalog jatalog;
    private final HashMap<String, IShellCommand> shellCommandMap;
    boolean timer;

    public ShellUsingCommands() {
        timer = false;
        jatalog = new Jatalog();
        historyOfCommands = new HistoryOfCommands();
        shellCommandMap = new HashMap<>();
        registerShellCommands();
    }

    protected void registerShellCommands() {
        Object[][] data = {
            new Object[]{"dump", new ShellCommands.Dump(this)},
            new Object[]{"exit", new ShellCommands.Exit(this)},
            new Object[]{"history", new ShellCommands.History(this)},
            new Object[]{"help", new ShellCommands.Help()},
            new Object[]{"evaluate", new ShellCommands.Evaluate(this)},
            new Object[]{"load", new ShellCommands.Load(this)},
            new Object[]{"recall", new ShellCommands.Recall(this)},
            new Object[]{"removeall", new ShellCommands.Removeall(this)},
            new Object[]{"timer", new ShellCommands.Timer(this)},
            new Object[]{"validate", new ShellCommands.Validate(this)}
        };
        for (Object[] o : data) {
            String n = (String) o[0];
            IShellCommand isc = (IShellCommand) o[1];
            shellCommandMap.put(n, isc);
        }
    }

    //-------------------------------------------------------------------------
    /**
     * Command line entry point.
     *
     * @param args
     */
    public static void main(String[] args) {
        final int exitCode = new CommandLine(new ShellUsingCommands()).execute(args);
        System.exit(exitCode);
    }

    //-------------------------------------------------------------------------
    /**
     * Entry point running this application.
     *
     * @return
     * @throws Exception
     */
    @Override
    public Integer call() throws Exception {
        if (this.loadFileList != null
                && !this.loadFileList.isEmpty()
                && loadFiles(loadFileList) == EXIT) {
            return EXIT;
        }
        if (this.interactiveActive) {
            this.replLoopUsingStdinStdout();
        }

        return 0;
    }

    int loadFiles(List<File> loadFileList) {
        try {
            QueryOutput qo = new DefaultQueryOutput();
            for (File loadFile : loadFileList) {
                try (Reader reader = new BufferedReader(new FileReader(loadFile))) {
                    this.jatalog.executeAll(reader, qo);
                }
            }
        } catch (DatalogException | IOException e) {
            e.printStackTrace();
            return EXIT;
        }
        return CONTINUE;
    }

    void replLoopUsingStdinStdout() {
        // Get input from command line
        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
        System.out.printf("Jatalog: Java Datalog engine\nInteractive mode;"
                + " Type 'help' for commands, 'exit' to quit.%n");

        while (true) {
            try {
                System.out.printf("> ");

                String line = buffer.readLine();
                if (line == null) {
                    break; // EOF
                }
                int rc = replLoopDispatch(line);
                if (rc == EXIT) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    int replLoopDispatch(String line) {
        line = line.trim();
        StringTokenizer tokenizer = new StringTokenizer(line);
        if (!tokenizer.hasMoreTokens()) {
            return CONTINUE;
        }
        //---
        String command = tokenizer.nextToken().toLowerCase();
        IShellCommand iShellCommand = this.shellCommandMap.get(command);
        if (iShellCommand != null) {
            return iShellCommand.execute(line);
        }
        // fall through implicit command query
        IShellCommand queryShellCommand = this.shellCommandMap.get("evaluate");
        return queryShellCommand.execute(line);
    }

}
