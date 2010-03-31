//
// Copyright 2005, 2006, 2007 Xerox Corporation
// Leigh L. Klotz, Jr. <Leigh.Klotz@xerox.com>
//
// This software is licensed under Version 3.0 of the Academic Free License.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
// 

package com.xerox.adoc.dexss.filters;

import java.util.Map;
import java.util.HashMap;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.ext.LexicalHandler;

import com.xerox.adoc.dexss.*;

/**
 * Element Replacement filter; replaces one element name with another, but leaves content alone.
 * Only local name is compared; namespace is ignored.
 */
public class ElementReplacementFilter extends DeXSSFilterImpl {
  final Map replacements;

  public ElementReplacementFilter(DeXSSChangeListener xssChangeListener) {
    this(xssChangeListener, new HashMap());
  }

  public ElementReplacementFilter(DeXSSChangeListener xssChangeListener, Map replacements) {
    super(xssChangeListener);
    this.replacements = replacements;
  }

  /**
   * Adds from and to to the list of element names for elements names that this filter should rename.
   * Only local name is compared; namespace is ignored.
   * @param from old name
   * @param to new name
   */
  public void add(String from, String to) {
    replacements.put(from, to);
  }

  public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
    String replacement = (String)(replacements.get(localName));
    if (replacement != null) {
      // TODO: What about namespace and qName?
      logXSSChange("Replacing element", localName, replacement);
      super.startElement(namespaceURI, replacement, qName, atts);
    }
    else
      super.startElement(namespaceURI, localName, qName, atts);
  }
  public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    String replacement = (String)(replacements.get(localName));
    if (replacement != null)
      super.endElement(namespaceURI, replacement, qName);
    else
      super.endElement(namespaceURI, localName, qName);
  }
}


  
