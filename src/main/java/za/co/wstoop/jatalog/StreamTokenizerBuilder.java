package za.co.wstoop.jatalog;

import java.io.Reader;
import java.io.StreamTokenizer;

/**
 *
 * @author pi
 */
class StreamTokenizerBuilder {

    private final Reader reader;

    StreamTokenizerBuilder(Reader reader) {
        this.reader = reader;
    }

    /* Specific tokenizer for our syntax */
    StreamTokenizer build() {
        StreamTokenizer scan = new StreamTokenizer(reader);
        scan.ordinaryChar('.'); // '.' looks like a number to StreamTokenizer by default
        scan.commentChar('%'); // Prolog-style % comments; slashSlashComments and slashStarComments can stay as well.
        scan.quoteChar('"');
        scan.quoteChar('\'');
        // WTF? You can't disable parsing of numbers unless you reset the syntax (http://stackoverflow.com/q/8856750/115589)
        //scan.parseNumbers();
        return scan;
    }

}
