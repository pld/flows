package io.rapidpro.excellent.functions;

import io.rapidpro.excellent.EvaluationContext;
import io.rapidpro.excellent.EvaluationError;
import io.rapidpro.excellent.functions.annotations.IntegerDefault;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link FunctionManager}
 */
public class FunctionManagerTest {

    private EvaluationContext m_context;

    @Before
    public void setup() {
        m_context = new EvaluationContext();
    }

    @Test
    public void test_invokeFunction() {
        FunctionManager manager = new FunctionManager();
        manager.addLibrary(TestFunctions.class);

        assertThat(manager.invokeFunction(m_context, "foo", new ArrayList<>(Arrays.asList(12))), is(24));
        assertThat(manager.invokeFunction(m_context, "FOO", new ArrayList<>(Arrays.asList(12))), is(24));
        assertThat(manager.invokeFunction(m_context, "bar", new ArrayList<>(Arrays.asList(12, 5))), is(17));
        assertThat(manager.invokeFunction(m_context, "bar", new ArrayList<>(Arrays.asList(12))), is(14));
        assertThat(manager.invokeFunction(m_context, "doh", new ArrayList<>(Arrays.asList(12, 1, 2, 3))), is(36));
    }

    @Test(expected = EvaluationError.class)
    public void test_invokeFunction_nonPublic() {
        FunctionManager manager = new FunctionManager();
        manager.addLibrary(TestFunctions.class);
        manager.invokeFunction(m_context, "zed", new ArrayList<>(Arrays.asList(12)));
    }

    public static class TestFunctions {

        public static int foo(EvaluationContext ctx, int a) {
            return a * 2;
        }

        public static int _bar(EvaluationContext ctx, int a, @IntegerDefault(2) int b) {
            return a + b;
        }

        public static int doh(int a, Object... args) {
            return args.length * a;
        }

        private static int zed(int a) {
            return a / 2;
        }
    }
}