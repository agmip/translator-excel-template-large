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
package org.agmip.translators.ebd.model.handler;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import org.agmip.translators.ebd.model.AgMIPSheet;
import org.agmip.translators.ebd.model.AgMIPSheets;

/**
 *
 * @author Christopher Villalobos
 */
public class AgMIPSheetsHandler extends DefaultHandler {
  private final SharedStringsTable sst;
  private final XSSFReader reader;
  private final AgMIPSheets sheets;
  private final XMLReader parser;

  public AgMIPSheetsHandler(SharedStringsTable sst, AgMIPSheets sheets, XSSFReader reader) throws SAXException {
    this.sst = sst;
    this.sheets = sheets;
    this.reader = reader;
    this.parser = XMLReaderFactory.createXMLReader();
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException {
    if (qName.equals("sheet")) {
      String id = attr.getValue("r:id");
      String name = attr.getValue("name");
      if (name.startsWith("DOC_")) return;
      AgMIPSheet sheet = new AgMIPSheet(id, name);

      VariableHandler handler = new VariableHandler(this.sst, sheet);
      parser.setContentHandler(handler);
      boolean parsed = false;
      try (InputStream stream = reader.getSheet(id)) {
        InputSource source = new InputSource(stream);
        parser.parse(source);
        parsed = true;
      } catch( IOException|InvalidFormatException ex) {
        System.err.println("There was an issue");
      }
      if (parsed) {
        this.sheets.add(sheet);
      }
    }  
  }
}
