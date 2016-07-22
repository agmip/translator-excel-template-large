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

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

public class AgMIPSheets extends ArrayList<AgMIPSheet> {
  private static final long serialVersionUID = 1L;

  public AgMIPSheets(int initialCapacity) {
    super(initialCapacity);
  }

  public AgMIPSheets() {
    super();
  }

  public AgMIPSheets(Collection<? extends AgMIPSheet> c) {
    super(c);
  }

  public boolean add(AgMIPSheet sheet) throws CircularDepencencyException {
    int dIndex = preDependencyIndex(sheet);
    if (dIndex == -1) {
      return super.add(sheet);
    } else {
      super.add(dIndex, sheet);
      return true;
    }
  }

  private int preDependencyIndex(AgMIPSheet sheet) throws CircularDepencencyException {
    int dIndex = -1;
    for(int i=0; i < size(); i++) {
      if (dIndex == -1) {
        for(String s : get(i).links()) {
          if (dIndex == -1) {
            if (sheet.variables().contains(s)) {
              dIndex = i;
            }
          }
        }
      }
    }
    if (dIndex != -1) {
      for(int i=0; i < size(); i++) {
        for(String s : get(i).variables()) {
          if (sheet.links().contains(s)) {
            if (i == dIndex) {
              throw new CircularDepencencyException(sheet.name(), get(i).name());
            }
            if (i > dIndex) {
              AgMIPSheet move = remove(i);
              add(dIndex,  move);
              dIndex++;
            }
            return dIndex;
          }
        }
      }
    }
    return dIndex;
  }

  private void trace() {
    for(AgMIPSheet s : this) {
      System.out.println("[TRACE] " + s.name());
    }
  }

  public static class CircularDepencencyException extends RuntimeException {
    CircularDepencencyException(String a, String b) {
      super("Sheets " + a + " and " + b + " have a circular dependency");
    }
    private static final long serialVersionUID = 1L;
  }
}
