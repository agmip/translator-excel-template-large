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

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.agmip.translators.ebd.model.AgMIPSheet;

/**
 *
 * @author Christopher Villalobos
 */
public class VariableHandler extends DefaultHandler {

  private final SharedStringsTable sst;
  private boolean foundRow = false;
  private boolean finished = false;
  private boolean nextIsString;
  private boolean inlineString;
  private String contents;
  private AgMIPSheet sheet;

  public VariableHandler(SharedStringsTable sst, AgMIPSheet sheet) {
    this.sst = sst;
    this.sheet = sheet;
  }

  @Override
  public void startDocument() throws SAXException {
    foundRow = false;
    finished = false;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    if (finished) {
      return;
    }

    switch (qName) {
      case "row":
        foundRow = true;
        break;
      case "c":
        if (foundRow) {
          String cellType = attributes.getValue("t");
          nextIsString = cellType != null && cellType.equals("s");
          inlineString = cellType != null && cellType.equals("inlineStr");
        }
        break;
      default:
        break;
    }
    contents = "";
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (!finished) {
      contents += new String(ch, start, length);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (finished) {
      return;
    }
    if (nextIsString) {
      int idx = Integer.parseInt(contents);
      contents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
      nextIsString = false;
    }

    contents = contents.toUpperCase().replaceAll("\\s","");
    switch (qName) {
      case "row":
        finished = true;
        break;
      case "v":
        if (contents.startsWith("!")) {
          return;
        } else if (contents.startsWith("#LINK:")) {
          sheet.addLink(contents);
        } else {
          sheet.addVariable(contents);
        }
        break;
      default:
        break;
    }
  }
}

