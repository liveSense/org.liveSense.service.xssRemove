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

import java.io.*;
import java.util.Properties;
import org.xml.sax.XMLReader;
import org.xml.sax.XMLFilter;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.ccil.cowan.tagsoup.XMLWriter;

/**
 * Call createInstance to specify an xssChangeWriter and a Writer.
 * Call process to parse inputString using TagSoup.
 * DeXSS notes any changes to xssChangeWriter, and serializes the resulting document to the Writer. 
 */
public class DeXSS {

  private DeXSSParser dexssParser;
  private InputSource inputSource;

  /**
   * Create a DeXSS object using createInstance.  Modifications will be noted to xssChangeListener, and the
   * resulting document serialized to w.
   * @param xssChangeListener the DeXSSChangeListener which is informed of changes
   * @param w the Writer to which the parsed document is serialized
   * @return a DeXSS object
   */
  public static DeXSS createInstance(DeXSSChangeListener xssChangeListener, Writer w) throws IOException, SAXException {
    XMLWriter x = new XMLWriter(w);
    x.setOutputProperty(XMLWriter.OMIT_XML_DECLARATION, "yes");
    DeXSS dexss = new DeXSS(xssChangeListener, x);
    return dexss;
  }

  private DeXSS(DeXSSChangeListener xssChangeListener, ContentHandler h) throws IOException, SAXException {
    dexssParser = new DeXSSParser();
    dexssParser.setDeXSSChangeListener(xssChangeListener);
    dexssParser.setContentHandler(h);
    //dexssParser.setProperty(Parser.lexicalHandlerProperty, h);
    inputSource = new InputSource();
  }

  /**
   * Call createInstance to specify an xssChangeWriter and a Writer.
   * Call process to parse inputString using TagSoup.
   * DeXSS notes any changes to xssChangeWriter, and serializes the resulting document to the Writer. 
   * @param inputString the String to parse
   */
  public void process(String inputString) throws IOException, SAXException {
    // http://java.sun.com/j2se/1.4.2/docs/api/index.html
    // "Once a parse is complete, an application may reuse the same XMLReader object, possibly with a different input source."
    // Might need to set the reuse flag.
    // XMLWriter.startDocument resets the XMLWriter, but doesn't clear its namespaces.
    inputSource.setCharacterStream(new StringReader(inputString));
    dexssParser.parse(inputSource);
  }

  public void process(Reader inputString) throws IOException, SAXException {
	    // http://java.sun.com/j2se/1.4.2/docs/api/index.html
	    // "Once a parse is complete, an application may reuse the same XMLReader object, possibly with a different input source."
	    // Might need to set the reuse flag.
	    // XMLWriter.startDocument resets the XMLWriter, but doesn't clear its namespaces.
	    inputSource.setCharacterStream(inputString);
	    dexssParser.parse(inputSource);
	  }


}
