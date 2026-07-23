package simplenem12;

import simplenem12.exception.InvalidMeterReadException;
import simplenem12.model.MeterRead;
import simplenem12.service.SimpleNem12ParserImpl;

import java.io.File;
import java.util.Collection;

/**
 * Simple test harness for trying out SimpleNem12Parser implementation
 */
public class TestHarness {

  public static void main(String[] args) {
    try {
      File simpleNem12File = new File(args[0]);

      if (!simpleNem12File.exists()) {
        throw new InvalidMeterReadException("Meter read file not found.");
      }

      if (simpleNem12File.length() == 0) {
        throw new InvalidMeterReadException("Meter read file is empty.");
      }

      // Uncomment below to try out test harness.
      Collection<MeterRead> meterReads = new SimpleNem12ParserImpl().parseSimpleNem12(simpleNem12File);

      MeterRead read6123456789 = meterReads.stream().filter(mr -> mr.getNmi().equals("6123456789")).findFirst().get();
      System.out.println(String.format("Total volume for NMI 6123456789 is %.2f", read6123456789.getTotalVolume()));  // Should be -36.84

      MeterRead read6987654321 = meterReads.stream().filter(mr -> mr.getNmi().equals("6987654321")).findFirst().get();
      System.out.println(String.format("Total volume for NMI 6987654321 is %.2f", read6987654321.getTotalVolume()));  // Should be 14.33
    } catch (InvalidMeterReadException e) {
      System.err.println("InvalidMeterReadFileException: " + e.getMessage());
    } catch (Exception e) {
      System.err.println("Exception: " + e.getMessage());
    }
  }
}
