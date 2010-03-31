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
 * Attribute Value Filter; removes attributes whose value contains the specified content
 */
public class AttributeValueFilter extends DeXSSFilterImpl {
  final String name;
  final List contents;

  public AttributeValueFilter(DeXSSChangeListener xssChangeListener, List contents) {
    super(xssChangeListener);
    this.contents = contents;
    this.name = null;
  }

  public AttributeValueFilter(DeXSSChangeListener xssChangeListener, String name) {
    super(xssChangeListener);
    this.contents = new ArrayList();
    this.name = name;
  }

  /**
   * Adds contains to the list strings for attribute values that this filter should remove.
   * @param contains String to add
   */
  public void add(String contains) {
    contents.add(contains);
  }

  public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
    int nAttrs = atts.getLength();
    for (int attNum = 0; attNum < nAttrs; attNum++) {
      String attName = atts.getLocalName(attNum);
      String attValue = atts.getValue(attNum);
      if (attName.equals(name)) {
	int nContains = contents.size();
	for (int containsNum = 0; containsNum < nContains; containsNum++) {
	  String contains = (String)contents.get(containsNum);
	  if (attValue.indexOf(contains) != -1) {
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
