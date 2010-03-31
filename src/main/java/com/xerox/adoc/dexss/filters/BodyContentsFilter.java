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
 * Body Contents Filter; drops everything that's not in &lt;body&gt;
 * <p>When used with TagSoup, will drop elements such as &lt;style&gt; that should be in &lt;head&gt; but are written in &lt;body&gt;.
 */
public class BodyContentsFilter extends DeXSSFilterImpl {
  int inHead = 0;
  int inBody = 0;

  public BodyContentsFilter(DeXSSChangeListener xssChangeListener) {
    super(xssChangeListener);
  }

  public void startElement(String namespaceURI, String localName,
   String qName, Attributes atts) throws SAXException {
    if (localName.equalsIgnoreCase("body")) {
      inBody++;
    } else {
      if (localName.equalsIgnoreCase("head")) {
	inHead++;
      } else {
	if (inBody > 0 && inHead == 0)
	  super.startElement(namespaceURI, localName, qName, atts);
      }
    }
  }

  public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    if (localName.equalsIgnoreCase("body")) {
      inBody--;
    } else {
      if (localName.equalsIgnoreCase("head")) {
	inHead--;
      } else {
	if (inBody > 0 && inHead == 0)
	  super.endElement(namespaceURI, localName, qName);
      }
    }
  }

  public void characters(char[] ch, int start, int length) throws SAXException {
    if (inBody > 0 && inHead == 0)
      super.characters(ch, start, length);
  }

  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    if (inBody > 0 && inHead == 0)
      super.ignorableWhitespace(ch, start, length);
  }
}
 
