package za.co.wstoop.jatalog.shell2;

import za.co.wstoop.jatalog.Jatalog;
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
import java.util.Optional;
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
public class ShellUsingCommands {

    final HistoryOfCommands historyOfCommands;
    final Jatalog jatalog;
    final HashMap<String, ShellCommands.IShellCommand> shellCommandMap;
    boolean timer = false;

    public ShellUsingCommands() {
        timer = false;
        jatalog = new Jatalog();
        historyOfCommands = new HistoryOfCommands();
        shellCommandMap = new HashMap<>();
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
            } catch (za.co.wstoop.jatalog.DatalogException | IOException e) {
                e.printStackTrace();
            }
        } else {
            ShellUsingCommands shellUsingCommands = new ShellUsingCommands();
            shellUsingCommands.replLoopUsingStdinStdout();
        }
    }

    private void registerShellCommands() {
        Object[][] data = {
                new Object[]{"dump", new ShellCommands.Dump(this)},
                new Object[]{"exit", new ShellCommands.Exit(this)},
                new Object[]{"history", new ShellCommands.History(this)},
                new Object[]{"help", new ShellCommands.Help()},
                //new Object[]{"evaluate", new ShellCommands.Evaluate(this)},
                new Object[]{"load", new ShellCommands.Load(this)},
                new Object[]{"recall", new ShellCommands.Recall(this)},
                new Object[]{"removeall", new ShellCommands.Removeall(this)},
                new Object[]{"timer", new ShellCommands.Timer(this)},
                new Object[]{"validate", new ShellCommands.Validate(this)}
        };
        for (Object[] o : data) {
            String n = (String) o[0];
            ShellCommands.IShellCommand isc = (ShellCommands.IShellCommand) o[1];
            shellCommandMap.put(n, isc);
        }
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

        return new ShellCommands.Evaluate(this).execute(line);
    }

    static class ShellCommands {

        interface IShellCommand {
            int EXIT = -1;
            int CONTINUE = 0;

            int execute(String line);
        }


        static class Exit implements IShellCommand {
            private final ShellUsingCommands parent;

            public Exit(za.co.wstoop.jatalog.shell2.ShellUsingCommands parent) {
                this.parent = parent;
            }

            @Override
            public int execute(String line) {
                return EXIT;
            }
        }

        static class Dump implements IShellCommand {
            private final ShellUsingCommands parent;

            public Dump(ShellUsingCommands parent) {
                this.parent = parent;
            }

            @Override
            public int execute(String line) {
                dump(line);
                return 0;
            }

            void dump(String line) {
                System.out.printf("dump:%n%s%n", parent.jatalog);
                parent.historyOfCommands.add(line);
            }
        }

        static class Removeall implements IShellCommand {
            private final ShellUsingCommands parent;

            public Removeall(ShellUsingCommands parent) {
                this.parent = parent;
            }

            @Override
            public int execute(String line) {
                removeall(line);
                return 0;
            }

            void removeall(String line) {
                za.co.wstoop.jatalog.EdbProvider edbProvider = parent.jatalog.getEdbProvider();
                int n = edbProvider.allFacts().size();
                edbProvider.allFacts().clear();

                Collection<za.co.wstoop.jatalog.Rule> idbRules = parent.jatalog.getIdb();
                int m = idbRules.size();
                idbRules.clear();
                System.out.printf("Removed %d facts, %d rules%n", n, m);
                parent.historyOfCommands.add(line);
            }
        }

        static class History implements IShellCommand {
            private final ShellUsingCommands parent;

            public History(ShellUsingCommands parent) {
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
                for (String item : parent.historyOfCommands.getList()) {
                    System.out.printf("%d: %s%n", i, item);
                    i += 1;
                }
            }
        }

        static class Recall implements IShellCommand {
            private final ShellUsingCommands parent;

            public Recall(ShellUsingCommands parent) {
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
                Optional<String> historyOptionalLine = parent.historyOfCommands.get(historyIndexInt);
                historyOptionalLine.ifPresent(s -> {
                    if (!s.startsWith("recall")) {
                        // dispatch historyIndexLine
                        parent.replLoopDispatch(s);
                    }
                });
            }
        }

        static class Load implements IShellCommand {
            private final ShellUsingCommands parent;

            public Load(ShellUsingCommands parent) {
                this.parent = parent;
            }

            @Override
            public int execute(String line) {
                StringTokenizer tokenizer = new StringTokenizer(line);
                tokenizer.nextToken();
                try {
                    load(line, tokenizer);
                } catch (IOException | za.co.wstoop.jatalog.DatalogException ex) {
                    ex.printStackTrace();
                }
                return 0;
            }

            void load(String line, StringTokenizer tokenizer) throws IOException, za.co.wstoop.jatalog.DatalogException {
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
                parent.historyOfCommands.add(line);
            }
        }

        static class Timer implements IShellCommand {
            private final ShellUsingCommands parent;

            public Timer(ShellUsingCommands parent) {
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
                parent.historyOfCommands.add(line);
            }
        }

        static class Validate implements IShellCommand {
            private final ShellUsingCommands parent;

            public Validate(ShellUsingCommands parent) {
                this.parent = parent;
            }

            @Override
            public int execute(String line) {
                try {
                    validate(line);
                } catch (za.co.wstoop.jatalog.DatalogException daex) {
                    daex.printStackTrace();
                }
                return CONTINUE;
            }

            void validate(String line) throws za.co.wstoop.jatalog.DatalogException {
                parent.jatalog.validate();
                System.out.printf("OK.%n"); // exception not thrown
                parent.historyOfCommands.add(line);
            }
        }

        static class Evaluate implements IShellCommand {
            private final ShellUsingCommands parent;


            public Evaluate(ShellUsingCommands parent) {
                this.parent = parent;
            }

            void evaluate(String line) throws za.co.wstoop.jatalog.DatalogException {
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
                parent.historyOfCommands.add(line);
            }

            @Override
            public int execute(String line) {
                try {
                    evaluate(line);
                } catch (za.co.wstoop.jatalog.DatalogException daex) {
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

    static class HistoryOfCommands {
        int maxHistory = 1000;
        List<String> history = new LinkedList<>();

        public int add(String line) {
            history.add(line);

            assertHistorySize();

            return history.size();
        }

        public Optional<String> get(int index) {
            if (index >= 0 && index < history.size()) {
                return java.util.Optional.of(history.get(index));
            }
            return java.util.Optional.empty();
        }

        void assertHistorySize() {
            if (history.size() > maxHistory) {
                history.remove(0);
            }
        }

        public List<String> getList() {
            return java.util.Collections.unmodifiableList(history);
        }
    }
}
