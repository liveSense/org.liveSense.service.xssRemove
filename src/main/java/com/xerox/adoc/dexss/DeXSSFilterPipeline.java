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

package com.xerox.adoc.dexss;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.ext.LexicalHandler;

import com.xerox.adoc.dexss.filters.*;


/**
 * DeXSSFilterPipeline sets up a pipeline of other filters.
 */
public class DeXSSFilterPipeline extends XMLFilterImpl implements DeXSSChangeListener {
  /**
   * Feature.  If set to true, only the body is returned, and the &lt;html&gt; and &lt;head&gt; elements
   * are discarded.
   */
  public static final String BODY_ONLY = "http://www.adoc.xerox.com/2007/dsbu/dexss/body-only";

  /**
   * Property.  Value should be an object satisfying interface {@link DeXSSChangeListener}.
   */
  public static final String DEXSS_CHANGE_LISTENER = "http://www.adoc.xerox.com/2007/dsbu/dexss/dexss-change-listener";

  private DeXSSChangeListener xssChangeListener;
  boolean bodyOnly = false;

  public DeXSSFilterPipeline() {
    super();
  }

  /**
   * Processes feature {@link #BODY_ONLY} directly; other features are referred to the superclass.
   * @param name feature name
   * @param state feature state
   * @throws SAXNotRecognizedException, SAXNotSupportedException
   */
  public void setFeature(String name, boolean state) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (name.equals(BODY_ONLY))
      bodyOnly = state;
    else
      super.setFeature(name, state);
  }

  /**
   * Processes feature {@link #BODY_ONLY} directly; other features are referred to the superclass.
   * @param name feature name
   * @return feature value
   * @throws SAXNotRecognizedException, SAXNotSupportedException
   */
  public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (name.equals(BODY_ONLY))
      return bodyOnly;
    else
      return super.getFeature(name);
  }

  /**
   * Processes property {@link #DEXSS_CHANGE_LISTENER} directly; other features are referred to the superclass.
   * @param name property name
   * @throws SAXNotRecognizedException, SAXNotSupportedException
   */
  public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (name.equals(DEXSS_CHANGE_LISTENER))
      xssChangeListener = ((DeXSSChangeListener)value);
    else
      super.setProperty(name, value);
  }

  /**
   * Processes property {@link #DEXSS_CHANGE_LISTENER} directly; other properties are referred to the superclass.
   * @param name property name
   * @return property value
   * @throws SAXNotRecognizedException, SAXNotSupportedException
   */
  public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (name.equals(DEXSS_CHANGE_LISTENER))
      return xssChangeListener;
    else
      return super.getProperty(name);
  }

  /**
   * Equivalent to <code>{@link #setProperty(String,Object) setProperty}({@link #DEXSS_CHANGE_LISTENER}, xssChangeListener)</code>
   * @see #getDeXSSChangeListener()
   */
  public void setDeXSSChangeListener(DeXSSChangeListener xssChangeListener) {
    this.xssChangeListener = xssChangeListener;
  }

  /**
   * Equivalent to <code>{@link #getProperty(String) getProperty}({@link #DeXSS_CHANGE_LISTENER})</code>
   * @return {@link DeXSSChangeListener} or null if none set.
   * @see #setDeXSSChangeListener(DeXSSChangeListener)
   */
  public DeXSSChangeListener getDeXSSChangeListener() {
    return xssChangeListener;
  }

  private XMLReader addBodyContentsFilter(XMLReader r) {
    XMLFilter rf = new BodyContentsFilter(this);
    rf.setParent(r);
    return rf;
  }

  /**
   * Specializes {@link XMLFilterImpl#setParent(XMLReader)} and constructs the DeXSS filter pipeline first.
   * <p>TODO: Pipeline configuration is controlled by inline boolean constants.  
   * Configuration should be made better, perhaps with a {@link java.util.Properties}
   * parameter to the constructor.</p>
   */
  public void setParent(XMLReader rr) {
    // TODO: Permit re-use by resetting state on setParent
    // TODO: basefont, etc.
    XMLReader r = rr;
    if (true) {
      r = addBodyContentsFilter(r);
    }

    if (true) {
      r = addElementRemovalFilter(r, "script");
      r = addElementRemovalFilter(r, "applet");
      r = addElementRemovalFilter(r, "embed");
      r = addElementRemovalFilter(r, "xml");
      r = addElementRemovalFilter(r, "bgsound");
      r = addElementRemovalFilter(r, "meta");
      r = addElementRemovalFilter(r, "link");
      r = addElementRemovalFilter(r, "style");
      r = addElementRemovalFilter(r, "base");
    }
    if (true) {
      r = addAttributeNameRemovalFilter(r, "onload");
      r = addAttributeNameRemovalFilter(r, "onclick");
      r = addAttributeNameRemovalFilter(r, "onchange");
      r = addAttributeNameRemovalFilter(r, "onsubmit");
      r = addAttributeNameRemovalFilter(r, "onmouseover");
      r = addAttributeNameRemovalFilter(r, "onerror");
    }
    if (true) {
      r = addAttributeNameStartFilter(r, "on");
    }
    if (true) {
      r = addAttributeNameRemovalFilter(r, "dynsrc");
      r = addAttributeNameRemovalFilter(r, "datasrc");
      r = addAttributeNameRemovalFilter(r, "datafld");
      r = addAttributeNameRemovalFilter(r, "dataformatas");
      /* TODO: Add more AttributesRemovalFilters */
    }
    if (true) {
      r = addElementReplacementFilter(r, "html", "div");
      r = addElementReplacementFilter(r, "head", "div");
      r = addElementReplacementFilter(r, "body", "div");
      r = addElementReplacementFilter(r, "iframe", "div");
      r = addElementReplacementFilter(r, "frame", "div");
      r = addElementReplacementFilter(r, "frameset", "div");
      r = addElementReplacementFilter(r, "layer", "div");
      r = addElementReplacementFilter(r, "ilayer", "div");
      r = addElementReplacementFilter(r, "blink", "span");
      r = addElementReplacementFilter(r, "object", "div");
    }
    if (true) {
      r = addSrcHrefFilter(r, "javascript:"); // javascript
      r = addSrcHrefFilter(r, "^\\s*j\\s*a\\s*v\\s*a\\s*s\\s*c\\s*r\\s*i\\s*p\\s*t\\s*:"); // javascript
      r = addSrcHrefFilter(r, "^\\s*v\\s*i\\s*e\\s*w\\s*-s\\s*o\\s*u\\s*r\\s*c\\s*e\\s*:"); // view-source
      r = addSrcHrefFilter(r, "^\\s*d\\s*a\\s*t\\s*a\\s*:");	// data
      r = addSrcHrefFilter(r, "^\\s*v\\s*b\\s*s\\s*s\\s*r\\s*i\\s*p\\s*t\\s*:"); // vbscript
      r = addSrcHrefFilter(r, "^\\s*a\\s*b\\s*o\\s*u\\s*t\\s*:"); // about
      r = addSrcHrefFilter(r, "^\\s*s\\s*h\\s*e\\s*e\\s*l\\s*:"); // shell
    }
    if (true) {
      // change to method if we get more than one of these "type" filters
      AttributeValueStartFilter acf = new AttributeValueStartFilter(this, "type");
      acf.add("text/javascript");
      acf.setParent(r);
      r = acf;
    }
    if (true) {
      r = addStyleContentFilter(r, cssContentsRegexp("javacsript"));
      r = addStyleContentFilter(r, cssContentsRegexp("expression"));
      // this area could possibly be improved
    }
    super.setParent(r);
  }

  private String cssContentsRegexp(String literal) {
    final String cruft = "(\\s|(/\\*.*\\*/)+)*";
    StringBuffer buf = new StringBuffer(literal.length() * (cruft.length() + 1));
    for (int i = 0; i < literal.length(); i++) {
      buf.append(literal.charAt(i));
      if (i < literal.length()-1)
	buf.append(cruft);       // whitespace or CSS /* */ comments between letters
    }
    return buf.toString();
  }

  private XMLReader addElementRemovalFilter(XMLReader r, String name) {
    XMLFilter rf = new ElementRemovalFilter(this, name);
    rf.setParent(r);
    return rf;
  }


  // TODO: Permit re-use by clearing this
  AttributeNameRemovalFilter attributeNameRemovalFilter = null;
  private XMLReader addAttributeNameRemovalFilter(XMLReader r, String name) {
    if (attributeNameRemovalFilter == null) {
      attributeNameRemovalFilter = new AttributeNameRemovalFilter(this);
      attributeNameRemovalFilter.setParent(r);
      attributeNameRemovalFilter.add(name);
      return attributeNameRemovalFilter;
    } else {
      attributeNameRemovalFilter.add(name);
      return r;
    }
  }

  // TODO: Permit re-use by clearing this
  AttributeNameStartFilter attributeNameStartFilter = null;
  private XMLReader addAttributeNameStartFilter(XMLReader r, String name) {
    if (attributeNameStartFilter == null) {
      attributeNameStartFilter = new AttributeNameStartFilter(this);
      attributeNameStartFilter.setParent(r);
      attributeNameStartFilter.add(name);
      return attributeNameStartFilter;
    } else {
      attributeNameStartFilter.add(name);
      return r;
    }
  }

  AttributeValueRegexpFilter hrefRegexpFilter = null;
  AttributeValueRegexpFilter srcRegexpFilter = null;
  AttributeValueRegexpFilter lowSrcRegexpFilter = null;
  private XMLReader addSrcHrefFilter(XMLReader r, String startsWith) {
    if (srcRegexpFilter == null) {
      {
	srcRegexpFilter = new AttributeValueRegexpFilter(this, "src", Pattern.CASE_INSENSITIVE);
	srcRegexpFilter.add(startsWith);
	srcRegexpFilter.setParent(r);
      }
      {
	lowSrcRegexpFilter = new AttributeValueRegexpFilter(this, "lowsrc", Pattern.CASE_INSENSITIVE);
	lowSrcRegexpFilter.add(startsWith);
	lowSrcRegexpFilter.setParent(srcRegexpFilter);
      }
      {
	hrefRegexpFilter = new AttributeValueRegexpFilter(this, "href", Pattern.CASE_INSENSITIVE);
	hrefRegexpFilter.setParent(lowSrcRegexpFilter);
	hrefRegexpFilter.add(startsWith);
      }
      return hrefRegexpFilter;
    } else {
      srcRegexpFilter.add(startsWith);
      lowSrcRegexpFilter.add(startsWith);
      hrefRegexpFilter.add(startsWith);
      return r;
    }
  }

  // TODO: Permit re-use by clearing this
  ElementReplacementFilter elementReplacementFilter = null;
  private XMLReader addElementReplacementFilter(XMLReader r, String from, String to) {
    if (elementReplacementFilter == null) {
      elementReplacementFilter = new ElementReplacementFilter(this);
      elementReplacementFilter.setParent(r);
      elementReplacementFilter.add(from, to);
      return elementReplacementFilter;
    } else {
      elementReplacementFilter.add(from, to);
      return r;
    }
  }

  AttributeValueRegexpFilter styleAttributeValueRegexpFilter = null;
  private XMLReader addStyleContentFilter(XMLReader r, String content) {
    if (styleAttributeValueRegexpFilter == null) {
      styleAttributeValueRegexpFilter = new AttributeValueRegexpFilter(this, "style", Pattern.CASE_INSENSITIVE);
      styleAttributeValueRegexpFilter.add(content);
      styleAttributeValueRegexpFilter.setParent(r); 
      return styleAttributeValueRegexpFilter;
    } else {
      styleAttributeValueRegexpFilter.add(content);
      return r;
    }
  }

  /**
   * Called when a change happens and there are no informational items.
   * If this DeXSSFilterPipeline has an xssChangeListener, invokes the similar method on it.
   * @param message Main message
   */
  public void logXSSChange(String message) {
    if (xssChangeListener != null)
      xssChangeListener.logXSSChange(message);
  }

  /**
   * Called when a change happens and there is one informational item.
   * If this DeXSSFilterPipeline has an xssChangeListener, invokes the similar method on it.
   * @param message Main message
   * @param item1 Information item
   */
  public void logXSSChange(String message, String item1) {
    if (xssChangeListener != null)
      xssChangeListener.logXSSChange(message, item1);
  }

  /**
   * Called when a change happens and there are two informational items.
   * If this DeXSSFilterPipeline has an xssChangeListener, invokes the similar method on it.
   * @param message Main message
   * @param item1 Information item 1
   * @param item2 Information item 2
   */
  public void logXSSChange(String message, String item1, String item2) {
    if (xssChangeListener != null)
      xssChangeListener.logXSSChange(message, item1, item2);
  }

}
