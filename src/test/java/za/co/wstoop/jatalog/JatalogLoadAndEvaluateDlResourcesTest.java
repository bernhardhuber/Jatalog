package za.co.wstoop.jatalog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import org.junit.jupiter.api.Test;
import za.co.wstoop.jatalog.output.ResultQueryOutput;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author pi
 */
public class JatalogLoadAndEvaluateDlResourcesTest {

    @Test
    void testFamily1() throws UnsupportedEncodingException, IOException, DatalogException {
        System.out.printf("\n----\nRunning testcase %s%n", "testFamily1");

        String name = "dl/family-1.dl";
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(name);

        Jatalog jatalog = new Jatalog();
        ResultQueryOutput resultQueryOutput = new ResultQueryOutput();

        try (Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            jatalog.executeAll(reader, resultQueryOutput);
        }

        resultQueryOutput.output(System.out);
        assertAll(
                () -> assertEquals("unemployed(Unemployed)?", resultQueryOutput.getResult().getStatement().toString()),
                () -> assertEquals("[{Unemployed: dwayne}, {Unemployed: daisy}, {Unemployed: chris}, {Unemployed: dolly}, {Unemployed: connie}, {Unemployed: elvin}, {Unemployed: constance}, {Unemployed: cobus}, {Unemployed: dupont}, {Unemployed: carol}, {Unemployed: dwight}]",
                         resultQueryOutput.getResult().getAnswers().toString())
        );
    }

    @Test
    void testFamily2() throws UnsupportedEncodingException, IOException, DatalogException {
        System.out.printf("\n----\nRunning testcase %s%n", "testFamily2");
        String name = "dl/family-2.dl";
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(name);

        Jatalog jatalog = new Jatalog();
        ResultQueryOutput resultQueryOutput = new ResultQueryOutput();

        try (Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            jatalog.executeAll(reader, resultQueryOutput);
        }

        resultQueryOutput.output(System.out);
        assertAll(
                () -> assertEquals("details(bob, Age, Address, Weight)?", resultQueryOutput.getResult().getStatement().toString()),
                () -> assertEquals("[{Address: \"10 someplace, somewhere, Age: 40.5, Weight: 100.567}]",
                        resultQueryOutput.getResult().getAnswers().toString())
        );
    }

    @Test
    void testExpr() throws UnsupportedEncodingException, IOException, DatalogException {
        System.out.printf("\n----\nRunning testcase %s%n", "testExpr");
        String name = "dl/expr.dl";
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(name);

        Jatalog jatalog = new Jatalog();
        ResultQueryOutput resultQueryOutput = new ResultQueryOutput();

        try (Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            jatalog.executeAll(reader, resultQueryOutput);
        }

        resultQueryOutput.output(System.out);
        assertAll(
                () -> assertEquals("p(X), X = Y, q(Y)?", resultQueryOutput.getResult().getStatement().toString()),
                () -> assertEquals("[{X: a, Y: a}]",
                        resultQueryOutput.getResult().getAnswers().toString())
        );
    }

    @Test
    void testNum() throws UnsupportedEncodingException, IOException, DatalogException {
        System.out.printf("\n----\nRunning testcase %s%n", "testNum");
        String name = "dl/num.dl";
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(name);

        Jatalog jatalog = new Jatalog();
        ResultQueryOutput resultQueryOutput = new ResultQueryOutput();

        try (Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            jatalog.executeAll(reader, resultQueryOutput);
        }

        resultQueryOutput.output(System.out);
        assertAll(
                () -> assertEquals("p(X), X = Y, q(Y)?", resultQueryOutput.getResult().getStatement().toString()),
                () -> assertEquals("[{X: a, Y: a}]",
                        resultQueryOutput.getResult().getAnswers().toString())
        );
    }

    @Test
    void testSilbling() throws UnsupportedEncodingException, IOException, DatalogException {
        System.out.printf("\n----\nRunning testcase %s%n", "testSilbling");
        String name = "dl/silbling.dl";
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(name);

        Jatalog jatalog = new Jatalog();
        ResultQueryOutput resultQueryOutput = new ResultQueryOutput();

        try (Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            jatalog.executeAll(reader, resultQueryOutput);
        }

        resultQueryOutput.output(System.out);
        assertAll(
                () -> assertEquals("sibling(bob, B)?", resultQueryOutput.getResult().getStatement().toString()),
                () -> assertEquals("[{B: bart}, {B: betty}]",
                        resultQueryOutput.getResult().getAnswers().toString())
        );
    }

}
