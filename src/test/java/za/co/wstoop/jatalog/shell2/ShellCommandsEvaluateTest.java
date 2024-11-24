package za.co.wstoop.jatalog.shell2;

import org.junit.jupiter.api.Test;
import za.co.wstoop.jatalog.shell2.ShellCommands.Evaluate;
import za.co.wstoop.jatalog.shell2.ShellCommands.IShellCommand;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author pi
 */
public class ShellCommandsEvaluateTest {

    @Test
    public void testExecute() {
        ShellUsingCommands parent = new ShellUsingCommands();
        Evaluate exit = new Evaluate(parent);
        int rc = exit.execute("");
        assertAll(
                () -> assertEquals(0, rc),
                () -> assertEquals(IShellCommand.CONTINUE, rc)
        );
    }
}
