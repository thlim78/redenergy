// Copyright Red Energy Limited 2017

package simplenem12.model.type;

/**
 * Represents meter read quality in SimpleNem12
 */
public enum Quality {

  A,
  E;

  public static Quality lookup(String value) {
    try {
      return Quality.valueOf(value);
    } catch (IllegalArgumentException exception) {
      throw new IllegalArgumentException("Invalid quality value - " + value);
    }
  }
}
