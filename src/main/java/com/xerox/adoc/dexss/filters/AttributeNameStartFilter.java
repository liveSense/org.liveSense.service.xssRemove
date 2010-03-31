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

import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.net.URL;
import java.util.Set;
import java.util.HashSet;
import java.net.URLConnection;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.ext.LexicalHandler;

import com.xerox.adoc.dexss.*;

/**
 * Attribute Start Filter; removes attributes whose name starts with the specified name
 */
public class AttributeNameStartFilter extends DeXSSFilterImpl {
  final List starts;

  public AttributeNameStartFilter(DeXSSChangeListener xssChangeListener) {
    this(xssChangeListener, new ArrayList());
  }


  public AttributeNameStartFilter(DeXSSChangeListener xssChangeListener, List starts) {
    super(xssChangeListener);
    this.starts = starts;
  }

  public AttributeNameStartFilter(DeXSSChangeListener xssChangeListener, String startsWith) {
    this(xssChangeListener, new ArrayList());
    add(startsWith);
  }

  /**
   * Adds startsWith to the list of starting strings of attribute names that this filter should remove.
   * @param startsWith String to add
   */
  public void add(String startsWith) {
    starts.add(startsWith);
  }

  public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
    int nAttrs = atts.getLength();
    for (int attNum = 0; attNum < nAttrs; attNum++) {
      String attName = atts.getLocalName(attNum);
      String attValue = atts.getValue(attNum);
      int nStarts = starts.size();
      for (int startsWithNum = 0; startsWithNum < nStarts; startsWithNum++) {
	String startsWith = (String)starts.get(startsWithNum);
	if (attName.startsWith(startsWith)) {
	  atts = Utils.removeAttribute(atts, attNum);
	  if (xssChangeListener != null)
            xssChangeListener.logXSSChange("Removing attribute", localName, attName);
	  attNum--;
	  nAttrs--;
	  break;
	}
      }
    }
    super.startElement(namespaceURI, localName, qName, atts);
  }
}
