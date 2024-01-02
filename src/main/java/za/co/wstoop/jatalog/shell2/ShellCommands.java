package za.co.wstoop.jatalog.shell2;

import za.co.wstoop.jatalog.DatalogException;
import za.co.wstoop.jatalog.EdbProvider;
import za.co.wstoop.jatalog.Rule;
import za.co.wstoop.jatalog.output.DefaultQueryOutput;
import za.co.wstoop.jatalog.output.OutputUtils;
import za.co.wstoop.jatalog.output.QueryOutput;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;

class ShellCommands {
    static String[] tokenizeTheLine(String line) {
        List<String> tokens = new ArrayList<>();

        StringTokenizer tokenizer = new StringTokenizer(line);
        while (tokenizer.hasMoreTokens()) {
            String s = tokenizer.nextToken();
            tokens.add(s);
        }
        return tokens.toArray(new String[0]);
    }

    interface IShellCommand {
        int EXIT = -1;
        int CONTINUE = 0;

        int execute(String line);
    }

    static class Exit implements IShellCommand {
        private final ShellUsingCommands parent;

        public Exit(ShellUsingCommands parent) {
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
            parent.historyOfCommands.addToHistory(line);
        }
    }

    static class Removeall implements IShellCommand {
        private final ShellUsingCommands parent;

        public Removeall(ShellUsingCommands parent) {
            this.parent = parent;
        }

        @Override
        public int execute(String line) {
            removeAllFactsAndRules(line);
            return 0;
        }

        void removeAllFactsAndRules(String line) {
            int removedFactsCount = removeFacts();
            int removeRulesCount = removeRules();
            System.out.printf("Removed %d facts, %d rules%n", removedFactsCount, removeRulesCount);
            parent.historyOfCommands.addToHistory(line);
        }

        int removeFacts() {
            EdbProvider edbProvider = parent.jatalog.getEdbProvider();
            int n = edbProvider.allFacts().size();
            edbProvider.allFacts().clear();
            return n;
        }

        int removeRules() {
            Collection<Rule> idbRules = parent.jatalog.getIdb();
            int m = idbRules.size();
            idbRules.clear();
            return m;
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
            for (String item : parent.historyOfCommands.retrieveAllHistoryEntries()) {
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
            recall(line);
            return 0;
        }

        void recall(String line) {
            String[] lineTokens = tokenizeTheLine(line);
            if (lineTokens.length <= 1) {
                System.err.printf("error: history-index expected%n");
                return;
            }
            String historyIndex = lineTokens[1];
            int historyIndexInt;
            try {
                historyIndexInt = Integer.parseInt(historyIndex);
            } catch (NumberFormatException nfex) {
                nfex.printStackTrace();
                return;
            }

            Optional<String> historyOptionalLine = parent.historyOfCommands.retrieveFromHistory(historyIndexInt);
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
            try {
                load(line);
            } catch (IOException | DatalogException ex) {
                ex.printStackTrace();
            }
            return 0;
        }

        void load(String line) throws IOException, DatalogException {
            String[] lineTokens = tokenizeTheLine(line);
            if (lineTokens.length <= 1) {
                System.err.printf("error: filename expected%n");
                return;
            }
            String filename = lineTokens[1];
            QueryOutput qo = new DefaultQueryOutput();
            try (Reader reader = new BufferedReader(new FileReader(filename))) {
                parent.jatalog.executeAll(reader, qo);
            }
            System.out.printf("OK.%n"); // exception not thrown
            parent.historyOfCommands.addToHistory(line);
        }
    }

    static class Timer implements IShellCommand {
        private final ShellUsingCommands parent;

        public Timer(ShellUsingCommands parent) {
            this.parent = parent;
        }

        @Override
        public int execute(String line) {
            timer(line);
            return 0;
        }

        void timer(String line) {
            String[] lineTokens = tokenizeTheLine(line);
            if (lineTokens.length <= 1) {
                parent.timer = !parent.timer;
            } else {
                parent.timer = lineTokens[1].matches("(?i:yes|on|true)");
            }
            System.out.printf("Timer is now %s%n", parent.timer ? "on" : "off");
            parent.historyOfCommands.addToHistory(line);
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
            } catch (DatalogException daex) {
                daex.printStackTrace();
            }
            return CONTINUE;
        }

        void validate(String line) throws DatalogException {
            parent.jatalog.validate();
            System.out.printf("OK.%n"); // exception not thrown
            parent.historyOfCommands.addToHistory(line);
        }
    }

    static class Evaluate implements IShellCommand {
        private final ShellUsingCommands parent;

        public Evaluate(ShellUsingCommands parent) {
            this.parent = parent;
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

        void evaluate(String line) throws DatalogException {
            long start = System.currentTimeMillis();
            Collection<Map<String, String>> answers = parent.jatalog.executeAll(line);
            double elapsed = (System.currentTimeMillis() - start) / 1000.0;

            if (answers != null) {
                // line contained a query with an answer.
                String result = OutputUtils.answersToString(answers);
                System.out.printf("query result:%n%s%n", result);

                if (parent.timer) {
                    System.out.printf(" %.3fs elapsed%n", elapsed);
                }
            }
            parent.historyOfCommands.addToHistory(line);
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
