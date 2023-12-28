package za.co.wstoop.jatalog;

import za.co.wstoop.jatalog.ShellBerni1.ShellCommands.Dump;
import za.co.wstoop.jatalog.ShellBerni1.ShellCommands.History;
import za.co.wstoop.jatalog.output.DefaultQueryOutput;
import za.co.wstoop.jatalog.output.OutputUtils;
import za.co.wstoop.jatalog.output.QueryOutput;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Shell for Jatalog.
 * This class contains a {@link #main(String...)} method that
 * <ul>
 * <li> if supplied with a list of filenames will execute each one in turn, or
 * <li> if no parameters are specified presents the user with an interactive Read-Evaluate-Print-Loop (REPL)
 *  through which the user can execute Datalog statements (using {@code System.in} and {@code System.out}).
 * </ul>
 */
public class ShellBerni1 {
    boolean timer = false;
    List<String> history = new LinkedList<>();

    Jatalog jatalog = new Jatalog();

    HashMap<String, ShellCommands.IShellCommand> shellCommandMap = new HashMap<>();

    public ShellBerni1() {
        registerShellCommands();
    }

    /**
     * Main method.
     *
     * @param args Names of files containing datalog statements to execute.
     *             If none are specified the Shell defaults to a REPL through which the user can interact with the engine.
     */
    public static void main(String... args) {
        if (args.length > 0) {
            // Read input from a file...
            try {
                Jatalog jatalog = new Jatalog();
                QueryOutput qo = new DefaultQueryOutput();
                for (String arg : args) {
                    try (Reader reader = new BufferedReader(new FileReader(arg))) {
                        jatalog.executeAll(reader, qo);
                    }
                }
            } catch (DatalogException | IOException e) {
                e.printStackTrace();
            }
        } else {
            ShellBerni1 shellBerni1 = new ShellBerni1();
            shellBerni1.replLoopUsingStdinStdout();
        }
    }

    private void registerShellCommands() {
        shellCommandMap.put("dump", new Dump(this));
        shellCommandMap.put("history", new History(this));
        shellCommandMap.put("help", new ShellBerni1.ShellCommands.Help());
        shellCommandMap.put("evaluate", new ShellBerni1.ShellCommands.Evaluate(this));
        shellCommandMap.put("load", new ShellBerni1.ShellCommands.Load(this));
        shellCommandMap.put("recall", new ShellBerni1.ShellCommands.Recall(this));
        shellCommandMap.put("removeall", new ShellBerni1.ShellCommands.Removeall(this));
        shellCommandMap.put("timer", new ShellBerni1.ShellCommands.Timer(this));
        shellCommandMap.put("validate", new ShellBerni1.ShellCommands.Validate(this));
    }

    void replLoopUsingStdinStdout() {
        // Get input from command line
        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
        System.out.printf("Jatalog: Java Datalog engine\nInteractive mode; Type 'help' for commands, 'exit' to quit.%n");

        while (true) {
            try {
                System.out.printf("> ");

                String line = buffer.readLine();
                if (line == null) {
                    break; // EOF
                }
                int rc = replLoopDispatch(line);
                if (rc == -1) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    int replLoopDispatch(String line) {
        int rc = 0;
        line = line.trim();
        StringTokenizer tokenizer = new StringTokenizer(line);
        if (!tokenizer.hasMoreTokens())
            return rc;
        //---
        String command = tokenizer.nextToken().toLowerCase();

        ShellCommands.IShellCommand iShellCommand = this.shellCommandMap.get(command);
        if (iShellCommand != null) {
            rc = iShellCommand.execute(line);
            return rc;
        }
        if (command.equals("exit")) {
            return -1;
        }
        return rc;
    }

    static class ShellCommands {

        interface IShellCommand {
            int EXIT = -1;
            int CONTINUE = 0;

            int execute(String line);
        }

        static class Dump implements IShellCommand {
            private final ShellBerni1 parent;

            public Dump(za.co.wstoop.jatalog.ShellBerni1 parent) {
                this.parent = parent;
            }

            void dump(String line) {
                System.out.printf("dump:%n%s%n", parent.jatalog);
                parent.history.add(line);
            }

            @Override
            public int execute(String line) {
                dump(line);
                return 0;
            }
        }

        static class Removeall implements IShellCommand {
            private final ShellBerni1 parent;

            public Removeall(za.co.wstoop.jatalog.ShellBerni1 parent) {
                this.parent = parent;
            }

            @Override
            public int execute(String line) {
                removeall(line);
                return 0;
            }

            void removeall(String line) {
                EdbProvider edbProvider = parent.jatalog.getEdbProvider();
                int n = edbProvider.allFacts().size();
                edbProvider.allFacts().clear();

                Collection<Rule> idbRules = parent.jatalog.getIdb();
                int m = idbRules.size();
                idbRules.clear();
                System.out.printf("Removed %d facts, %d rules%n", n, m);
                parent.history.add(line);
            }
        }

        static class History implements IShellCommand {
            private final ShellBerni1 parent;

            public History(za.co.wstoop.jatalog.ShellBerni1 parent) {
                this.parent = parent;
            }

            @Override
            public int execute(String line) {
                history();
                return 0;
            }

            void history() {
                int i = 0;
                System.out.printf("history:%n");
                for (String item : parent.history) {
                    System.out.printf("%d: %s%n", i, item);
                    i += 1;
                }
            }
        }

        static class Recall implements IShellCommand {
            private final ShellBerni1 parent;

            public Recall(za.co.wstoop.jatalog.ShellBerni1 parent) {
                this.parent = parent;
            }

            @Override
            public int execute(String line) {
                StringTokenizer tokenizer = new StringTokenizer(line);
                tokenizer.nextToken();
                recall(line, tokenizer);
                return 0;
            }

            void recall(String line, StringTokenizer tokenizer) {
                if (!tokenizer.hasMoreTokens()) {
                    System.err.printf("error: history-index expected%n");
                    return;
                }
                String historyIndex = tokenizer.nextToken();
                int historyIndexInt = Integer.parseInt(historyIndex);
                if (historyIndexInt >= 0 && historyIndexInt < parent.history.size()) {
                    String historyIndexLine = parent.history.get(historyIndexInt);
                    if (!historyIndexLine.startsWith("recall")) {
                        // dispatch historyIndexLine
                        parent.replLoopDispatch(historyIndexLine);
                    }
                }
            }
        }

        static class Load implements IShellCommand {
            private final ShellBerni1 parent;

            public Load(za.co.wstoop.jatalog.ShellBerni1 parent) {
                this.parent = parent;
            }

            @Override
            public int execute(String line) {
                StringTokenizer tokenizer = new StringTokenizer(line);
                tokenizer.nextToken();
                try {
                    load(line, tokenizer);
                } catch (IOException | DatalogException ex) {
                    ex.printStackTrace();
                }
                return 0;
            }

            void load(String line, StringTokenizer tokenizer) throws IOException, DatalogException {
                if (!tokenizer.hasMoreTokens()) {
                    System.err.printf("error: filename expected%n");
                    return;
                }
                String filename = tokenizer.nextToken();
                QueryOutput qo = new DefaultQueryOutput();
                try (Reader reader = new BufferedReader(new FileReader(filename))) {
                    parent.jatalog.executeAll(reader, qo);
                }
                System.out.printf("OK.%n"); // exception not thrown
                parent.history.add(line);
            }
        }

        static class Timer implements IShellCommand {
            private final ShellBerni1 parent;

            public Timer(za.co.wstoop.jatalog.ShellBerni1 parent) {
                this.parent = parent;
            }

            @Override
            public int execute(String line) {
                StringTokenizer tokenizer = new StringTokenizer(line);
                tokenizer.nextToken();
                timer(line, tokenizer);
                return 0;
            }

            void timer(String line, StringTokenizer tokenizer) {
                if (!tokenizer.hasMoreTokens()) {
                    parent.timer = !parent.timer;
                } else {
                    parent.timer = tokenizer.nextToken().matches("(?i:yes|on|true)");
                }
                System.out.printf("Timer is now %s%n", parent.timer ? "on" : "off");
                parent.history.add(line);
            }
        }

        static class Validate implements IShellCommand {
            private final ShellBerni1 parent;

            public Validate(za.co.wstoop.jatalog.ShellBerni1 parent) {
                this.parent = parent;
            }

            @Override
            public int execute(String line) {
                try {
                    validate(line);
                } catch (DatalogException daex) {
                    daex.printStackTrace();
                }
                return CONTINUE;
            }

            void validate(String line) throws DatalogException {
                parent.jatalog.validate();
                System.out.printf("OK.%n"); // exception not thrown
                parent.history.add(line);
            }
        }

        static class Evaluate implements IShellCommand {
            private final ShellBerni1 parent;


            public Evaluate(za.co.wstoop.jatalog.ShellBerni1 parent) {
                this.parent = parent;
            }

            void evaluate(String line) throws DatalogException {
                long start = System.currentTimeMillis();
                Collection<java.util.Map<String, String>> answers = parent.jatalog.executeAll(line);
                double elapsed = (System.currentTimeMillis() - start) / 1000.0;

                if (answers != null) {
                    // line contained a query with an answer.
                    String result = OutputUtils.answersToString(answers);
                    System.out.printf("query result:%n%s%n", result);

                    if (parent.timer) {
                        System.out.printf(" %.3fs elapsed%n", elapsed);
                    }
                }
                parent.history.add(line);
            }

            @Override
            public int execute(String line) {
                try {
                    evaluate(line);
                } catch (DatalogException daex) {
                    daex.printStackTrace();
                }
                return CONTINUE;
            }
        }

        static class Help implements IShellCommand {
            public int execute(String line) {
                System.out.printf("load filename  - Loads and executes the specified file.%n"
                        + "timer [on|off] - Enable/disable the query timer.%n"
                        + "validate       - Validates the facts and rules in the database.%n"
                        + "dump           - Displays the facts and rules on the console.%n"
                        + "removeall       - Remove all the facts and rules.%n"
                        + "history        - Displays all commands entered in this session.%n"
                        + "recall n       - Recall history item having index n%n"
                        + "help           - Displays this message.%n"
                        + "exit           - Quits the program.%n");
                return CONTINUE;
            }
        }
    }
}
