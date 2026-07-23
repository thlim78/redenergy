// Copyright Red Energy Limited 2017
package simplenem12.model.type;

import java.util.Optional;

/**
 * Represents the KWH energy unit in SimpleNem12
 */
public enum EnergyUnit {
  KWH;

  public static EnergyUnit lookup(String value) {
    try {
      return EnergyUnit.valueOf(value);
    } catch (IllegalArgumentException exception) {
      throw new IllegalArgumentException("Invalid energy unit value - " + value);
    }
  }
}
