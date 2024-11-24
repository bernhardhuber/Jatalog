package za.co.wstoop.jatalog.shell2;

import org.junit.jupiter.api.Test;
import za.co.wstoop.jatalog.shell2.ShellCommands.IShellCommand;
import za.co.wstoop.jatalog.shell2.ShellCommands.Removeall;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author pi
 */
public class ShellCommandsRemoveallTest {

    @Test
    public void testExecute() {
        ShellUsingCommands parent = new ShellUsingCommands();
        Removeall exit = new Removeall(parent);
        int rc = exit.execute("");
        assertAll(
                () -> assertEquals(0, rc),
                () -> assertEquals(IShellCommand.CONTINUE, rc)
        );
    }
}
