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
import java.util.Set;
import java.util.HashSet;
import java.net.URLConnection;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.ext.LexicalHandler;

import com.xerox.adoc.dexss.*;

/**
 * Attribute Removal Filter;
 * Removes attributes matching names added with {@link #add(String)}.
 */
public class AttributeNameRemovalFilter extends DeXSSFilterImpl {
  final Set attributeLocalNames;

  public AttributeNameRemovalFilter(DeXSSChangeListener xssChangeListener) {
    this(xssChangeListener, new HashSet());
  }

  /**
   * Adds name to the list names regexps for attribute names that this filter should remove.
   * @param name name to add
   */
  public void add(String name) {
    attributeLocalNames.add(name);
  }

  public AttributeNameRemovalFilter(DeXSSChangeListener xssChangeListener, Set attributeLocalNames) {
    super(xssChangeListener);
    this.attributeLocalNames = attributeLocalNames;
  }

  public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
    int nAttrs = atts.getLength();
    for (int attNum = 0; attNum < nAttrs; attNum++) {
      String attName = atts.getLocalName(attNum);
      if (attributeLocalNames.contains(attName)) {
	if (atts instanceof org.ccil.cowan.tagsoup.AttributesImpl) {
	  atts = Utils.removeAttribute(atts, attNum);
	  if (xssChangeListener != null)
            xssChangeListener.logXSSChange("Removing attribute", localName, attName);
	  nAttrs--;
	  attNum--;
	}
      }
    }
    super.startElement(namespaceURI, localName, qName, atts);
  }
}
