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
 * Element Removal Filter;
 * Removes the specified element and all its content.
 */
public class ElementRemovalFilter extends DeXSSFilterImpl {
  int elementLevel = 0 ;
  final String elementName;

  /**
   * Element Removal Filter
   * Removes Elements matching elementName.
   */
  public ElementRemovalFilter(DeXSSChangeListener xssChangeListener, String elementName) {
    super(xssChangeListener);
    this.elementName = elementName;
  }

  public void startElement(String namespaceURI, String localName,
   String qName, Attributes atts) throws SAXException {
    if (localName.equalsIgnoreCase(elementName)) {
      elementLevel++;
      logXSSChange("Removing element", elementName);
    } else {
      if (elementLevel == 0)
	super.startElement(namespaceURI, localName, qName, atts);
    }
  }
  public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    if (localName.equalsIgnoreCase(elementName)) {
      elementLevel--;
    } else {
      if (elementLevel == 0)
	super.endElement(namespaceURI, localName, qName);
    }
  }
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (elementLevel == 0)
      super.characters(ch, start, length);
  }

  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    if (elementLevel == 0)
      super.ignorableWhitespace(ch, start, length);
  }
}


  
