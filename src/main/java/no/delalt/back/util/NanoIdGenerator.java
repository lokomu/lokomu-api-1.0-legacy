package no.delalt.back.util;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

public class NanoIdGenerator {
  private static final char[] CUSTOM_ALPHABET_CHARS =
    "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

  private static final int SIZE = 21;

  /**
   * Generates a unique Nano ID using a custom alphabet.
   *
   * @return         	The generated Nano ID.
   */
  public static String generateNanoID() {
    return NanoIdUtils.randomNanoId(
      NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
      CUSTOM_ALPHABET_CHARS,
      SIZE
    );
  }
}
