package org.kuali.student.sqlOrganizer;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: lsymms
 * Date: 12/30/13
 * Time: 3:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestConfigEncoding {
    @Test
    public void testEncryptions() {
        testEncryption("2013-11-25-KRIM-update-final-exam-permissions.sql");
        testEncryption("2013-04-23-KRMS-temp-fix-krms-sequences.sql");
        testEncryption("BOOTSTRAP");
        testEncryption("REFERENCE");
    }


    public void testEncryption(String original) {
        String encoded = InsecureStringEncoder.encode(original);
        String decoded = InsecureStringEncoder.decode(encoded);
        System.out.println("encrypted string: {" + original + "} {" + encoded + "}");
        Assert.assertTrue(original.equals(decoded));
    }

}
