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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AgMIPSheet {
  private String name;
  private String id;
  private List<String> variables;
  private List<String> links;
  private boolean isRoot = false;
  private Optional<String> rootField;
  private final Set<String> ROOT_FIELDS = Collections.unmodifiableSet(
      new HashSet<>(Arrays.asList(new String[] {"EXNAME", "EXPER_ID", "SOIL_ID", "WST_ID"})));

  public AgMIPSheet(String id, String name) {
    this.id = id;
    this.name = name;
    this.variables = new ArrayList<>(50);
    this.links = new ArrayList<>();
    this.rootField = Optional.empty();
  }

  public AgMIPSheet(String name) {
    this(null, name);
  }

  public AgMIPSheet() {
    this(null, null);
  }

  public void id(String id) {
    this.id = id;
  }

  public String id() {
    return this.id;
  }

  public void name(String name) {
    this.name = name;
  }

  public String name() {
    return this.name;
  }

  public boolean isRoot() {
    return rootField.isPresent();
  }

  public Optional<String> root() {
    return rootField;
  }

  public void addLink(String link) {
    this.links.add(link.toUpperCase().replaceAll("\\s", "").substring(6));
  }

  public void addVariable(String var) {
    String fixed = var.toUpperCase().replaceAll("\\s", "");
    if (ROOT_FIELDS.contains(fixed)) {
      rootField = Optional.of(fixed);
    }
    this.variables.add(fixed);
  }

  public List<String> links() {
    return this.links;
  }

  public List<String> variables() {
    return this.variables;
  }

  public boolean has(String needle) {
    return this.variables.contains(needle.toUpperCase()) || this.links.contains(needle.toUpperCase());
  }
}

