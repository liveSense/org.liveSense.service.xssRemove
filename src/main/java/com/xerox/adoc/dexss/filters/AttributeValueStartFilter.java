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
 * Attribute Start Filter; removes attributes whose content starts with the specified content
 */
public class AttributeValueStartFilter extends DeXSSFilterImpl {
  final String name;
  final List starts;

  public AttributeValueStartFilter(DeXSSChangeListener xssChangeListener, String name, List starts) {
    super(xssChangeListener);
    this.starts = starts;
    this.name = name;
  }

  public AttributeValueStartFilter(DeXSSChangeListener xssChangeListener, String name) {
    super(xssChangeListener);
    this.starts = new ArrayList();
    this.name = name;
  }

  /**
   * Adds startsWith to the list of startting strings for attribute values that this filter should remove.
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
      if (attName.equals(name)) {
	int nStarts = starts.size();
	for (int startsWithNum = 0; startsWithNum < nStarts; startsWithNum++) {
	  String startsWith = (String)starts.get(startsWithNum);
	  if (attValue.startsWith(startsWith)) { // BUG: Needs to ignore leading whitespace!
	    atts = Utils.removeAttribute(atts, attNum);
            logXSSChange("Removing attribute", localName, attName);
	    attNum--;
	    nAttrs--;
	    break;
	  }
	}
      }
    }
    super.startElement(namespaceURI, localName, qName, atts);
  }
}
