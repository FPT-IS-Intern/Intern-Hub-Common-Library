package com.intern.hub.library.common.utils;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for generating random values such as secret keys, numeric strings,
 * alphanumeric strings, secure byte arrays, and boolean values.
 */
public class RandomGenerator {

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  /**
   * Private constructor to prevent instantiation of this utility class.
   */
  private RandomGenerator() {
    // Utility class - prevent instantiation
  }

  /**
   * Generates a secure random secret key encoded in URL-safe Base64 format.
   *
   * @return A URL-safe Base64 encoded secret key.
   */
  public static String generateSecretKey() {
    byte[] key = new byte[64];
    SECURE_RANDOM.nextBytes(key);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(key);
  }

  /**
   * Generates a random numeric string of specified length.
   *
   * @param length           The length of the numeric string to generate.
   * @param allowLeadingZero Whether to allow leading zeros in the generated string.
   * @return A random numeric string.
   */
  public static String randomNumberString(int length, boolean allowLeadingZero) {
    if (length <= 0) {
      throw new IllegalArgumentException("Length must be a positive integer.");
    }
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      int digit;
      if (i == 0 && !allowLeadingZero) {
        digit = SECURE_RANDOM.nextInt(9) + 1;
      } else {
        digit = SECURE_RANDOM.nextInt(10);
      }
      sb.append(digit);
    }
    return sb.toString();
  }

  /**
   * Generates a random alphanumeric string of specified length.
   *
   * @param length The length of the alphanumeric string to generate.
   * @return A random alphanumeric string.
   */
  public static String randomAlphaNumericString(int length) {
    if (length <= 0) {
      throw new IllegalArgumentException("Length must be a positive integer.");
    }
    final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      int index = SECURE_RANDOM.nextInt(CHARACTERS.length());
      sb.append(CHARACTERS.charAt(index));
    }
    return sb.toString();
  }

  /**
   * Generates a byte array of specified length filled with secure random bytes.
   *
   * @param length The length of the byte array to generate.
   * @return A byte array filled with secure random bytes.
   */
  public static byte[] randomSecureBytes(int length) {
    if (length <= 0) {
      throw new IllegalArgumentException("Length must be a positive integer.");
    }
    byte[] bytes = new byte[length];
    SECURE_RANDOM.nextBytes(bytes);
    return bytes;
  }

  /**
   * Generates a random boolean value.
   *
   * @return A random boolean value.
   */
  public static boolean randomBoolean() {
    return SECURE_RANDOM.nextBoolean();
  }

}
