// Copyright Red Energy Limited 2017

package simplenem12.model.type;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum RecordType {
  HEADER(Arrays.asList("100")),
  BODY(Arrays.asList("200","300")),
  FOOTER(Arrays.asList("900"));

  private List<String> values;

  RecordType (List<String> values) {
    this.values = values;
  }

  public static Boolean isHeader(String value) {
    return isPresent(value) && HEADER.values.contains(value);
  }
  public static Boolean isBody(String value) {
    return isPresent(value) && BODY.values.stream().anyMatch(type -> value.startsWith(type));
  }
  public static Boolean isMeterReadBlock(String value) {
    return isPresent(value) && BODY.values.get(0).equalsIgnoreCase(value);
  }
  public static Boolean isVolumeOfMeterRead(String value) {
    return isPresent(value) && BODY.values.get(1).equalsIgnoreCase(value);
  }
  public static Boolean isFooter(String value) {
    return isPresent(value) && FOOTER.values.contains(value);
  }

  private static Boolean isPresent(String value) {
    return Optional.ofNullable(value).isPresent();
  }
}
