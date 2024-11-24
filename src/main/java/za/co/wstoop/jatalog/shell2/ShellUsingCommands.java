package za.co.wstoop.jatalog.shell2;

import za.co.wstoop.jatalog.DatalogException;
import za.co.wstoop.jatalog.Jatalog;
import za.co.wstoop.jatalog.output.DefaultQueryOutput;
import za.co.wstoop.jatalog.output.QueryOutput;
import za.co.wstoop.jatalog.shell2.ShellCommands.IShellCommand;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;

import static za.co.wstoop.jatalog.shell2.ShellCommands.IShellCommand.CONTINUE;
import static za.co.wstoop.jatalog.shell2.ShellCommands.IShellCommand.EXIT;

/**
 * Shell for Jatalog. This class contains a {@link #main(String...)} method that
 * <ul>
 * <li> if supplied with a list of filenames will execute each one in turn, or
 * <li> if no parameters are specified presents the user with an interactive
 * Read-Evaluate-Print-Loop (REPL) through which the user can execute Datalog
 * statements (using {@code System.in} and {@code System.out}).
 * </ul>
 */
public final class ShellUsingCommands implements Callable<Integer> {

    final HistoryOfCommands historyOfCommands;
    final Jatalog jatalog;
    private final HashMap<String, IShellCommand> shellCommandMap;
    boolean timer = false;

    public ShellUsingCommands() {
        timer = false;
        jatalog = new Jatalog();
        historyOfCommands = new HistoryOfCommands();
        shellCommandMap = new HashMap<>();
        registerShellCommands();
    }

    enum ProcessingMode {
        UNDEFINED,
        INTERACTIVE,
        COMMANDLINEONLY;
    }
    ProcessingMode processingMode = ProcessingMode.UNDEFINED;
    String[] args;

    /**
     * Main method.
     *
     * @param args Names of files containing datalog statements to execute. If
     * none are specified the Shell defaults to a REPL through which the user
     * can interact with the engine.
     */
    public static void main(String... args) {
        ShellUsingCommands shellUsingCommands = new ShellUsingCommands();
        shellUsingCommands.processingMode = args.length > 0
                ? ProcessingMode.COMMANDLINEONLY
                : ProcessingMode.INTERACTIVE;
        shellUsingCommands.args = args;

        int exitCode = 0;
        try {
            exitCode = shellUsingCommands.call();
        } catch (Exception ex) {
            exitCode = -100;
        }
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
        switch (this.processingMode) {
            case COMMANDLINEONLY: {
                // Read input from a file...
                try {

                    QueryOutput qo = new DefaultQueryOutput();
                    for (String arg : args) {
                        try (Reader reader = new BufferedReader(new FileReader(arg))) {
                            this.jatalog.executeAll(reader, qo);
                        }
                    }
                } catch (DatalogException | IOException e) {
                    e.printStackTrace();
                    return -1;
                }
            }
            break;
            case INTERACTIVE: {
                this.replLoopUsingStdinStdout();
            }
            break;
            default: {
            }
        }
        return 0;
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
        //return new Evaluate(this).execute(line);
    }

}
