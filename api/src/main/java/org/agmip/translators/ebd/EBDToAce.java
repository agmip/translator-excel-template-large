/*
 * Copyright (c) 2012-2016, AgMIP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the AgMIP nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.agmip.translators.ebd;

import java.io.IOException;
import java.nio.file.Path;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author frostbytten
 */
public class EBDToAce implements IToAce {
  private static final Logger LOG = LoggerFactory.getLogger(EBDToAce.class);
  //AceDataset ace = new AceDataset();
  public static void read(Path ebdPath) {
    try (Workbook wb = WorkbookFactory.create(ebdPath.toFile())) {
      readSheets(wb);
    } catch (IOException | InvalidFormatException ex) {
      LOG.error("Error reading workbook: " + ex.getMessage());
    }
  }
  
  public static void readSheets(Workbook wb) {
    for(Sheet s : (Iterable<Sheet>) () -> wb.sheetIterator()) {
    }
  }
  
  public static void readFirstRow(Sheet s) {
    Row r = s.getRow(0);
    if (r != null) {
      System.out.println( "This row has " + r.getPhysicalNumberOfCells() + " cells.");
      int idx = 0;
      for(Cell c : r) {
        String cellString = c.getStringCellValue();
        if (! cellString.startsWith("!")) {
          if (cellString.toLowerCase().equals("dataset notes")) {
            System.out.println("Found dataset notes... stopping on this sheet.");
            return;
          } else {
            System.out.println("Saving this column id: " + idx + " [" + cellString + "]");
          }
        }
        idx++;
      }
    }
  }
}
