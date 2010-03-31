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
 * Base class for XSS filters
 * Extends {@link XMLFilterImpl} and provides the methods for DeXSSChangeListener.
 */
public class DeXSSFilterImpl extends XMLFilterImpl implements XMLFilter {
  protected DeXSSChangeListener xssChangeListener;

  public DeXSSFilterImpl(DeXSSChangeListener xssChangeListener) {
    super();
    this.xssChangeListener = xssChangeListener;
  }

  /*
   * Permit re-use by resetting state on setParent
   */
  public void setParent(XMLReader r) {
    super.setParent(r);
  }

  public void setDeXSSChangeListener(DeXSSChangeListener xssChangeListener) {
    this.xssChangeListener = xssChangeListener;
  }

  public DeXSSChangeListener getXSSChangeListener() {
    return xssChangeListener;
  }

  protected void logXSSChange(String message) {
    xssChangeListener.logXSSChange(message);
  }

  protected void logXSSChange(String message, String item1) {
    if (xssChangeListener != null)
      xssChangeListener.logXSSChange(message, item1);
  }

  protected void logXSSChange(String message, String item1, String item2) {
    if (xssChangeListener != null)
      xssChangeListener.logXSSChange(message, item1, item2);
  }

}
 
