package za.co.wstoop.jatalog.shell2;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

class HistoryOfCommands {

    private final int maxHistory = 1000;
    private final List<String> history = new LinkedList<>();

    public int addToHistory(String line) {
        history.add(line);
        assertHistorySize();

        return history.size();
    }

    public Optional<String> retrieveFromHistory(int index) {
        if (index >= 0 && index < history.size()) {
            return Optional.of(history.get(index));
        }
        return Optional.empty();
    }

    void assertHistorySize() {
        if (history.size() > maxHistory) {
            history.remove(0);
        }
    }

    public List<String> retrieveAllHistoryEntries() {
        return Collections.unmodifiableList(history);
    }
}
