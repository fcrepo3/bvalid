package net.sf.bvalid;

import junit.framework.TestCase;
import junit.textui.TestRunner;

public class ValidatorOptionTest extends TestCase {

    public ValidatorOptionTest(String name) { super (name); }

    public void setUp() {
    }
            
    public void tearDown() {
    }

    //---------------------------------------------------------[ Test methods ]

    public void testValidOptionValues() {

        String[] validValues = new String[] { "a", "b" };

        ValidatorOption opt = new ValidatorOption("optName",
                                                  "optDescription",
                                                  validValues,
                                                  "a");

        assertEquals("a should have been considered a valid option value",
                     opt.isValidValue("a"),
                     true);

        assertEquals("b should have been considered a valid option value",
                     opt.isValidValue("b"),
                     true);

        opt = new ValidatorOption("optName", "optDescription", null, "a");

        assertEquals("c should have been considered a valid option value",
                     opt.isValidValue("c"),
                     true);

        assertEquals("<null> should have been considered a valid option value",
                     opt.isValidValue(null),
                     true);

    }

    public void testInvalidOptionValues() {

        String[] validValues = new String[] { "a", "b" };

        ValidatorOption opt = new ValidatorOption("optName",
                                                  "optDescription",
                                                  validValues,
                                                  "a");

        assertEquals("c should NOT have been considered a valid option value",
                     opt.isValidValue("c"),
                     false);

        assertEquals("<null> should NOT have been considered a valid option value",
                     opt.isValidValue(null),
                     false);
    }

    public static void main(String[] args) {
        TestRunner.run(ValidatorOptionTest.class);
    }   

}
