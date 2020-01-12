package com.ajmi.simpleuserdirectoryservice.tests.cryptography;

import com.ajmi.simpleuserdirectoryservice.cryptography.PasswordCrypt;
import org.junit.Test;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

/**
 * Tests for the PasswordCrypt class.
 */
public class TestPasswordCrypt {

    /**
     * Tests that two different salts produce two different hashes with the hashPassword() method.
     */
    @Test
    public void testHashInequality() {
        String salt1 = "deadbeef";
        String salt2 = "feedc0ffee";
        String pass = "qwerty";

        String hash1 = PasswordCrypt.hashPassword(pass, salt1);
        String hash2 = PasswordCrypt.hashPassword(pass, salt2);

        assertNotSame(hash1, hash2);
    }

    /**
     * Tests that the same salt produces the same hash with the hashPassword() method.
     */
    @Test
    public void testHashEquality() {
        String salt = "deadbeef";
        String pass = "qwerty";

        String hash1 = PasswordCrypt.hashPassword(pass, salt);
        String hash2 = PasswordCrypt.hashPassword(pass, salt);

        assertEquals(hash1, hash2);
    }

    /**
     * Tests that the nextSalt() method produces a valid hex string.
     */
    @Test
    public void testSaltValidHexString() {
        parseHexBinary(PasswordCrypt.nextSalt());
    }

    /**
     * Tests that the nextSalt() method produces two different salts.
     */
    @Test
    public void testSaltInequality() {
        String salt1 = PasswordCrypt.nextSalt();
        String salt2 = PasswordCrypt.nextSalt();
        String pass = "qwerty";

        String hash1 = PasswordCrypt.hashPassword(pass, salt1);
        String hash2 = PasswordCrypt.hashPassword(pass, salt2);

        assertNotSame(hash1, hash2);
    }

    /**
     * Tests that hashPassword produces the same hash when using the same salt from nextSalt().
     */
    @Test
    public void testSaltEquality() {
        String salt = PasswordCrypt.nextSalt();
        String pass = "qwerty";

        String hash1 = PasswordCrypt.hashPassword(pass, salt);
        String hash2 = PasswordCrypt.hashPassword(pass, salt);

        assertEquals(hash1, hash2);
    }

    /**
     * Test that an Illegal Argument Exception is thrown when trying to hash a password with a length of zero.
     */
    @Test
    public void testHashEmptyPass() {
        String pass = "";
        String salt = "This is my salt.";
        // this should throw a runtime exception
        try {
            PasswordCrypt.hashPassword(pass, salt);
            // if the program doesn't crash, then the test failed.
            fail("Failed to throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * Tests that an Illegal Argument Exception is thrown when trying to hash a null password.
     */
    @Test
    public void testHashNullPass() {
        String pass = null;
        String salt = "This is my salt.";
        // this should throw a runtime exception
        try {
            PasswordCrypt.hashPassword(pass, salt);
            // if the program doesn't crash, then the test failed.
            fail("Failed to throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * Tests that an Illegal Argument Exception is thrown when trying to hash a password with a salt of length zero.
     */
    @Test
    public void testHashEmptySalt() {
        String pass = "qwerty";
        String salt = "";
        // this should throw a runtime exception
        try {
            PasswordCrypt.hashPassword(pass, salt);
            // if the program doesn't crash, then the test failed.
            fail("Failed to throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * Tests that an Illegal Argument Exception is thrown when trying to hash a password with a null salt.
     */
    @Test
    public void testHashNullSalt() {
        String pass = "qwerty";
        String salt = null;
        // this should throw a runtime exception
        try {
            PasswordCrypt.hashPassword(pass, salt);
            // if the program doesn't crash, then the test failed.
            fail("Failed to throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
}
