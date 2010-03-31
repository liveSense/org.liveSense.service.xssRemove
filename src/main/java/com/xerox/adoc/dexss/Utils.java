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

import org.xml.sax.Attributes;

/**
 * Utility functions
 */
public abstract class Utils {
  /**
   * Given a SAX2 Attributes and an index, remove the specified attribute as best we can.
   * If the implementation is the SAX Helpers AttributesImpl, use its removeAttribute.
   * If the implementation is TagSoup's, use its removeAttribute.
   * Otherwise, convert it to a SAX Helpers implementation and use its removeAttribute.
   */
  public static Attributes removeAttribute(Attributes atts, int attNum) {
    if (atts instanceof org.ccil.cowan.tagsoup.AttributesImpl) {
      ((org.ccil.cowan.tagsoup.AttributesImpl)atts).removeAttribute(attNum);
      return atts;
    } else if (atts instanceof org.xml.sax.helpers.AttributesImpl) {
      ((org.xml.sax.helpers.AttributesImpl)atts).removeAttribute(attNum);
      return atts;
    } else {
      org.xml.sax.helpers.AttributesImpl newatts = new org.xml.sax.helpers.AttributesImpl(atts);
      newatts.removeAttribute(attNum);
      return newatts;
    }
  }

  /**
   * Given a SAX2 Attributes and an index, remove the specified attribute as best we can.
   * If the implementation is the SAX Helpers AttributesImpl, use its setAttribute
   * If the implementation is TagSoup's, use its setAttribute
   * Otherwise, convert it to a SAX Helpers implementation and use its setAttribute
   */
  public static Attributes clearAttribute(Attributes atts, int attNum) {
    if (atts instanceof org.ccil.cowan.tagsoup.AttributesImpl) {
      ((org.ccil.cowan.tagsoup.AttributesImpl)atts).setValue(attNum, "");
      return atts;
    } else if (atts instanceof org.xml.sax.helpers.AttributesImpl) {
      ((org.xml.sax.helpers.AttributesImpl)atts).setValue(attNum, "");
      return atts;
    } else {
      org.xml.sax.helpers.AttributesImpl newatts = new org.xml.sax.helpers.AttributesImpl(atts);
      newatts.setValue(attNum, "");
      return newatts;
    }
  }
}
