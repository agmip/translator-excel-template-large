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
package org.agmip.translators.ebd.model;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import org.agmip.translators.ebd.model.handler.FirstRowHandler;
import org.agmip.translators.ebd.model.handler.DebugHandler;
import org.agmip.translators.ebd.model.handler.AgMIPSheetsHandler;


/**
 *
 * @author frostbytten
 */
public class XlsxBigCropUserModel {

  private static final Logger LOG = LoggerFactory.getLogger(XlsxBigCropUserModel.class);
  private final OPCPackage pkg;
  private final XSSFReader reader;
  private final SharedStringsTable sst;
  private final XMLReader parser;
  private final AgMIPSheets agmipSheets;

  public XlsxBigCropUserModel(Path file) throws Exception {
    pkg = OPCPackage.open(file.toFile(), PackageAccess.READ);
    reader = new XSSFReader(pkg);
    sst = reader.getSharedStringsTable();
    parser = XMLReaderFactory.createXMLReader();
    agmipSheets = new AgMIPSheets();
  }

  public void init() throws Exception {
    try (InputStream wkb = reader.getWorkbookData()) {
      InputSource source = new InputSource(wkb);
      AgMIPSheetsHandler handler = new AgMIPSheetsHandler(sst, agmipSheets, reader);
      setContentHandler(handler);
      parser.parse(source);
    }
  }

  public void getWorkbookDebug() throws Exception {
    InputStream wkb = reader.getWorkbookData();
    InputSource source = new InputSource(wkb);
    setContentHandler(new DebugHandler(sst));
    parser.parse(source);
  }

  public void getSheetsDebug() throws Exception {
    XSSFReader.SheetIterator sheets = getSheetIterator();
    setContentHandler(new DebugHandler(sst));
    while (sheets.hasNext()) {
      try (InputStream sheet = sheets.next()) {
        InputSource source = new InputSource(sheet);
        parser.parse(source);
      }
    }
  }

  public void getSheetDebug(String id) throws Exception {
    setContentHandler(new DebugHandler(sst));
    try (InputStream sheet = reader.getSheet(id)) {
      InputSource source = new InputSource(sheet);
      parser.parse(source);
    }
  }

  public void setContentHandler(ContentHandler handler) throws Exception {
    parser.setContentHandler(handler);
  }

  public XMLReader getParser() {
    return parser;
  }

  public XSSFReader.SheetIterator getSheetIterator() throws Exception {
    return (XSSFReader.SheetIterator) reader.getSheetsData();
  }

  public List<String> getSheetNames() throws Exception {
    XSSFReader.SheetIterator sheets = getSheetIterator();
    List<String> sheetNames = new ArrayList<>();
    while (sheets.hasNext()) {
      InputStream sheet = sheets.next();
      sheetNames.add(sheets.getSheetName());
    }
    return sheetNames;
  }

  public AgMIPSheets agmipSheets() {
    return this.agmipSheets;
  }
}
