// Copyright Red Energy Limited 2017

package simplenem12.service;

import simplenem12.exception.*;
import simplenem12.model.*;
import simplenem12.model.type.EnergyUnit;
import simplenem12.model.type.Quality;
import simplenem12.model.type.RecordType;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class SimpleNem12ParserImpl implements SimpleNem12Parser {

  /**
   * Parses Simple NEM12 file.
   * 
   * @param simpleNem12File file in Simple NEM12 format
   * @return Collection of <code>MString line;eterRead</code> that represents the data in the given file.
   */
  private static final String commarDelimiter = ",";
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

  public Collection<MeterRead> parseSimpleNem12(File simpleNem12File) {

      try {
          List<String> stringList = Files.lines(Paths.get(simpleNem12File.getAbsolutePath())).collect(Collectors.toList());

          if (stringList.isEmpty()) {
              throw new InvalidMeterReadException("File content is empty.");
          }

          validateRecordType100FirstLineInFile(stringList.get(0));
          validateRecordType900LastLineInFile(stringList.get(stringList.size() - 1));

          List<MeterRead> meterReads = new ArrayList<>();

          stringList.stream()
                  .filter(RecordType::isBody)
                  .forEach(record -> {
                      String[] values = record.split(commarDelimiter);
                      if (RecordType.isMeterReadBlock(values[0])) {
                          String NMI = values[1];
                          validateNMILength(NMI);
                          EnergyUnit energyUnit = EnergyUnit.valueOf(values[2]);
                          MeterRead newMeterRead = new MeterRead(NMI, energyUnit);
                          if (meterReads.stream().anyMatch(meterRead -> meterRead.equals(newMeterRead))) {
                              throw new InvalidMeterReadException("Duplicate NMI found in meter read: " + NMI);
                          }
                          meterReads.add(newMeterRead);
                      } else if (RecordType.isVolumeOfMeterRead(values[0])) {
                          validateRecordType200AtTheStartOfMeterReadBlock(meterReads);
                          LocalDate localDate = parseStringToDate(values[1]);
                          Quality quality = Quality.lookup(values[3]);
                          MeterVolume meterVolume = new MeterVolume(new BigDecimal(values[2]), quality);
                          MeterRead meterRead = meterReads.get(meterReads.size() - 1);
                          meterRead.appendVolume(localDate, meterVolume);
                      }
                  });

          return meterReads;

      } catch (IllegalArgumentException | IOException | InvalidMeterReadException e) {
          throw new InvalidMeterReadException("InvalidMeterReadFileException: " + e.getMessage());
      } catch (Exception e ) {
          throw e;
      }
  }

  private void validateRecordType100FirstLineInFile(String firstLine) {
    if (!RecordType.isHeader(firstLine)) {
        throw new InvalidMeterReadException("RecordType 100 must be the first line in the file");
    }
  }

  private void validateRecordType900LastLineInFile(String lastLine) {
    if (!RecordType.isFooter(lastLine)) {
        throw new InvalidMeterReadException("RecordType 900 must be the last line in the file");
    }
  }

  private void validateRecordType200AtTheStartOfMeterReadBlock(List<MeterRead> meterReads) {
    if (meterReads.isEmpty()) {
        throw new InvalidMeterReadException("RecordType 200 must be the start of meter read block");
    }
  }

  private void validateNMILength(String NMI) {
      Optional.ofNullable(NMI).ifPresent(nmi -> {
          if (nmi.length() != 10) {
              throw new InvalidMeterReadException("NMI should always be 10 characters long");
          }
      });
  }

  private LocalDate parseStringToDate(String date) {
      try {
          return LocalDate.parse(date, formatter);
      } catch (DateTimeParseException exception) {
          throw new InvalidMeterReadException("Date must be in yyyyMMdd - " + date);
      }
  }
}
