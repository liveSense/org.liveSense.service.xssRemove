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
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.URLConnection;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.ext.LexicalHandler;

import com.xerox.adoc.dexss.*;

/**
 * AttributeValue Regexp Filter; removes attributes whose content matches the specified regexp
 */
public class AttributeValueRegexpFilter extends DeXSSFilterImpl {
  final String name;
  final List regexps;
  final int flags;

  public AttributeValueRegexpFilter(DeXSSChangeListener xssChangeListener, List regexps) {
    super(xssChangeListener);
    this.regexps = regexps;
    this.name = null;
    this.flags=0;

  }

  public AttributeValueRegexpFilter(DeXSSChangeListener xssChangeListener, String name) {
    super(xssChangeListener);
    this.regexps = new ArrayList();
    this.name = name;
    this.flags=0;

  }

  public AttributeValueRegexpFilter(DeXSSChangeListener xssChangeListener, String name, int flags) {
    super(xssChangeListener);
    this.regexps = new ArrayList();
    this.name = name;
    this.flags = flags;
  }


  /**
   * Adds regexp to the list of regexps for attribute values that this filter should remove.
   * @param regexp regexp to add
   */
  public void add(String regexp) {
    regexps.add(Pattern.compile(regexp, flags));
  }

  public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
    int nAttrs = atts.getLength();
    for (int attNum = 0; attNum < nAttrs; attNum++) {
      String attName = atts.getLocalName(attNum);
      String attValue = atts.getValue(attNum);
      if (attName.equals(name)) {
	int nRegexps = regexps.size();
	for (int regexpNum = 0; regexpNum < nRegexps; regexpNum++) {
	  Pattern pattern = (Pattern)regexps.get(regexpNum);
	  if (pattern.matcher(attValue).find()) { // TODO: Is there a way to re-use matcher in a single thread?
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
