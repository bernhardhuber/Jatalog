package za.co.wstoop.jatalog.shell2;

import org.junit.jupiter.api.Test;
import za.co.wstoop.jatalog.shell2.ShellCommands.Exit;
import za.co.wstoop.jatalog.shell2.ShellCommands.IShellCommand;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author pi
 */
public class ShellCommandsExitTest {

    @Test
    public void testExecute() {
        ShellUsingCommands parent = new ShellUsingCommands();
        Exit exit = new Exit(parent);
        int rc = exit.execute("");
        assertAll(
                () -> assertEquals(-1, rc),
                () -> assertEquals(IShellCommand.EXIT, rc)
        );
    }
}
