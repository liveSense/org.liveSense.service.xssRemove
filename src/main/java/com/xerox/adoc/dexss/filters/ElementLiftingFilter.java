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

import java.util.Set;
import java.util.HashSet;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.ext.LexicalHandler;

import com.xerox.adoc.dexss.*;

/**
 * Element Lifting Filter; Lifts content of matching element (and its attributes) by eliding it and replacing it with its own content.
 */
public class ElementLiftingFilter extends DeXSSFilterImpl {
  final Set tagnames;

  public ElementLiftingFilter(DeXSSChangeListener xssChangeListener) {
    this(xssChangeListener, new HashSet());
  }

  public ElementLiftingFilter(DeXSSChangeListener xssChangeListener, Set tagnames) {
    super(xssChangeListener);
    this.tagnames = tagnames;
  }

  /**
   * Adds tagname to the list of names for element names that this filter should "lift".
   * @param tagname tagname to add
   */
  public void add(String tagname) {
    tagnames.add(tagname);
  }

  public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
    if (! tagnames.contains(localName)) {
      logXSSChange("Lifting element", localName);
      super.startElement(namespaceURI, localName, qName, atts);
    }
  }
  public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    if (! tagnames.contains(localName))
      super.endElement(namespaceURI, localName, qName);
  }
}


  
