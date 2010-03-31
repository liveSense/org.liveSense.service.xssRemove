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

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.ccil.cowan.tagsoup.Parser;

/**
 * The DeXSSParser object.
 * This class can be used as a SAX2 XML Parser that first applies TagSoup, then applies the {@link DeXSSFilterPipeline}.
 * <p>Example:
 * <pre>{
 *  DeXSSParser dexssParser = new DeXSSParser();
 *  dexssParser.setContentHandler(new XMLWriter(writer));
 *  InputSource inputSource = new InputSource();
 *  inputSource.setCharacterStream(new StringReader(inputString));
 *  dexssParser.parse(inputSource);
 *}
 * </pre>
 * </p>
 */
public class DeXSSParser extends DeXSSFilterPipeline {
  /**
   * Creates a DeXSSParser with the following feature set:
   * <ul>
   * <li>{@link DeXSSFilterPipeline#BODY_ONLY} <code>true</code></li>
   * </ul>
   * And uses as parent a {@link org.ccil.cowan.tagsoup.Parser} with the following feature set:
   * <ul>
   * <li>{@link org.ccil.cowan.tagsoup.Parser#ignoreBogonsFeature} <code>true</code></li>
   * <li>{@link org.ccil.cowan.tagsoup.Parser#defaultAttributesFeature} <code>false</code></li>
   * </ul>
   * TODO: Should be made more configurable.
   */
  public DeXSSParser() throws SAXNotRecognizedException, SAXNotSupportedException {
    super();
    setFeature(DeXSSFilterPipeline.BODY_ONLY, true);
    Parser parser = new Parser();
    parser.setFeature(Parser.ignoreBogonsFeature, true);
    parser.setFeature(Parser.defaultAttributesFeature, false);
    setParent(parser);
  }
}
