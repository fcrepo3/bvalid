package net.sf.bvalid;

import java.io.*;

public class BValid {

    public static void main(String[] args) throws Exception {

        // tell commons-logging to use log4j
        System.setProperty("org.apache.commons.logging.LogFactory", "org.apache.commons.logging.impl.Log4jFactory");
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Log4JLogger");

        Validator validator = ValidatorFactory.getValidator(
                                     SchemaLanguage.XSD);

        try {
            validator.validate(new FileInputStream(new File(args[0])));
            System.out.println("OK");
            System.exit(0);
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
            if (e.getCause() != null) {
                System.out.println("Underlying error:");
                e.getCause().printStackTrace();
            }
            System.exit(1);
        }
    }

}
