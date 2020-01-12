package com.ajmi.simpleuserdirectoryservice.user;

import org.bouncycastle.jcajce.provider.digest.SHA3;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

/**
 * Static functions for encrypting passwords.
 */
public class PasswordCrypt {

    /** String used for exceptions dealing with an invalid password parameter. */
    private static final String INVALID_PASSWORD = "Invalid password string.";
    /** String used for exceptions dealing with an invalid salt parameter. */
    private static final String INVALID_SALT = "Invalid salt string.";
    /** SecureRandom object used to generate a salt string. */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    /** Length of the generated salt. */
    private static final int SALT_LENGTH = 32;

    /**
     * Generates a secure, random hex string to use as a salt.
     * @return a String of a hex number.
     */
    public static String nextSalt() {
        byte[] saltBytes = new byte[SALT_LENGTH];
        SECURE_RANDOM.nextBytes(saltBytes);
        return printHexBinary(saltBytes);
    }

    /**
     * Hashes the specified password using the specified salt.
     * @param password the password to hash.
     * @param salt the salt to use when hashing the password.
     * @return the hashed password as a new String.
     */
    public static String hashPassword(String password, String salt) {
        // check arguments
        if (password == null || password.length() == 0) {
            throw new IllegalArgumentException(INVALID_PASSWORD);
        }
        if (salt == null || salt.length() == 0) {
            throw new IllegalArgumentException(INVALID_SALT);
        }

        byte[] passwordBytes;
        try {
            passwordBytes = password.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(INVALID_PASSWORD, e);
        }

        byte[] saltBytes;
        try {
            saltBytes = parseHexBinary(salt);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(INVALID_SALT, e);
        }

        final byte[] bytes = concat(passwordBytes, saltBytes);
        SHA3.DigestSHA3 md = new SHA3.Digest512();
        md.update(bytes);
        byte[] digest = md.digest();
        return printHexBinary(digest);
    }

    /**
     * Concatenates any number of byte arrays.
     * @param args the Byte arrays to concatenate.
     * @return a new byte containing the concatenated arguments.
     */
    private static byte[] concat(byte[]... args) {
        int len = 0;
        for (byte[] a : args) {
            len += a.length;
        }
        byte[] dest = new byte[len];
        int destPos = 0;
        for (byte[] src : args) {
            System.arraycopy(src, 0, dest, destPos, src.length);
            destPos += src.length;
        }
        return dest;
    }
}
