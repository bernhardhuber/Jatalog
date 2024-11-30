package za.co.wstoop.jatalog.shell2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Option(names = {"-e", "--evaluate"},
            description = "Evaluate datalog statements.")
    private List<String> evaluateStatementList;

    //---
    final HistoryOfCommands historyOfCommands;
    final Jatalog jatalog;
    private final Map<String, IShellCommand> shellCommandMap;
    boolean timer;

    public ShellUsingCommands() {
        timer = false;
        jatalog = new Jatalog();
        historyOfCommands = new HistoryOfCommands();
        shellCommandMap = new ShellCommandsFactory().createShellCommandsMap(this);
    }

    static class ShellCommandsFactory {

        protected Map<String, IShellCommand> createShellCommandsMap(ShellUsingCommands shellUsingCommands) {
            Map<String, IShellCommand> shellCommandMap = new HashMap<>();
            Object[][] data = {
                new Object[]{"dump", new ShellCommands.Dump(shellUsingCommands)},
                new Object[]{"exit", new ShellCommands.Exit(shellUsingCommands)},
                new Object[]{"history", new ShellCommands.History(shellUsingCommands)},
                new Object[]{"help", new ShellCommands.Help()},
                new Object[]{"evaluate", new ShellCommands.Evaluate(shellUsingCommands)},
                new Object[]{"load", new ShellCommands.Load(shellUsingCommands)},
                new Object[]{"recall", new ShellCommands.Recall(shellUsingCommands)},
                new Object[]{"removeall", new ShellCommands.Removeall(shellUsingCommands)},
                new Object[]{"timer", new ShellCommands.Timer(shellUsingCommands)},
                new Object[]{"validate", new ShellCommands.Validate(shellUsingCommands)}
            };
            for (Object[] o : data) {
                String n = (String) o[0];
                IShellCommand isc = (IShellCommand) o[1];
                shellCommandMap.put(n, isc);
            }
            return shellCommandMap;
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
                && !this.loadFileList.isEmpty()) {
            int rc = loadFiles(loadFileList);
            if (rc == EXIT) {
                return EXIT;
            }
        }
        if (this.evaluateStatementList != null
                && !this.evaluateStatementList.isEmpty()) {
            int rc = evaluate(this.evaluateStatementList);
            if (rc == EXIT) {
                return EXIT;
            }
        }
        if (this.interactiveActive) {
            this.replLoopUsingStdinStdout();
        }

        return 0;
    }

    int evaluate(List<String> evaluateStatementList) {
        IShellCommand isc = new ShellCommands.Evaluate(this);
        for (String evaluateStatement : evaluateStatementList) {
            int rc = isc.execute(evaluateStatement);
            if (rc == EXIT) {
                return EXIT;
            }
        }
        return CONTINUE;
    }

    int loadFiles(List<File> loadFileList) {
        IShellCommand isc = new ShellCommands.Load(this);
        for (File loadFile : loadFileList) {
            int rc = isc.execute("load " + loadFile.getPath());
            if (rc == EXIT) {
                return EXIT;
            }
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
