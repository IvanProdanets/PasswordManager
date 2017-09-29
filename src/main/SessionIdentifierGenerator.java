
package main;

import java.security.SecureRandom;
import java.math.BigInteger;

/**
 * Генерує випадковий рядок 150 bit
 * @author Admin
 */
public final class SessionIdentifierGenerator {
  private SecureRandom random = new SecureRandom();

  public String nextSessionId() {
    return new BigInteger(150, random).toString(32);
  }
}
