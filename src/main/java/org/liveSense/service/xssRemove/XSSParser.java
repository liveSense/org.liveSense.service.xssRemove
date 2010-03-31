package org.liveSense.service.xssRemove;

import com.xerox.adoc.dexss.DeXSSChangeListener;
import com.xerox.adoc.dexss.filters.AttributeNameRemovalFilter;
import com.xerox.adoc.dexss.filters.AttributeNameStartFilter;
import com.xerox.adoc.dexss.filters.AttributeValueRegexpFilter;
import com.xerox.adoc.dexss.filters.AttributeValueStartFilter;
import com.xerox.adoc.dexss.filters.BodyContentsFilter;
import com.xerox.adoc.dexss.filters.ElementRemovalFilter;
import com.xerox.adoc.dexss.filters.ElementReplacementFilter;
import java.util.regex.Pattern;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.ccil.cowan.tagsoup.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;



public class XSSParser extends XMLFilterImpl implements DeXSSChangeListener {

    /**
     * default log
     */
    private final Logger log = LoggerFactory.getLogger(XSSParser.class);
    boolean bodyOnly = false;

    XMLReader registerBodyContentFilter(Parser inReader) {
        XMLFilter rf = new BodyContentsFilter(this);
        rf.setParent(inReader);
        return rf;
    }

    XMLReader registerElementRemovalListFilter(XMLReader inReader, String[] elementRemovalList) {
        XMLReader reader = inReader;
        for (int i = 0; i < elementRemovalList.length; i++) {
            XMLFilter rfn = new ElementRemovalFilter(this, elementRemovalList[i]);
            rfn.setParent(reader);
            reader = rfn;
        }
        return reader;
    }

    XMLReader registerAttributeNameRemovalFilter(XMLReader inReader, String[] attributeNameRemovalList) {
        AttributeNameRemovalFilter attributeNameRemovalFilter = new AttributeNameRemovalFilter(this);
        attributeNameRemovalFilter.setParent(inReader);
        XMLReader reader = attributeNameRemovalFilter;
        for (int i = 0; i < attributeNameRemovalList.length; i++) {
            attributeNameRemovalFilter.add(attributeNameRemovalList[i]);
        }
        return reader;
    }

    XMLReader registerAttributeNameStartFilter(XMLReader inReader, String[] attributeNameStartList) {
        AttributeNameStartFilter attributeNameStartFilter = new AttributeNameStartFilter(this);
        attributeNameStartFilter.setParent(inReader);
        XMLReader reader = attributeNameStartFilter;
        for (int i = 0; i < attributeNameStartList.length; i++) {
            attributeNameStartFilter.add(attributeNameStartList[i]);
        }
        return reader;
    }

    XMLReader registerAttributeSrcHrefFilter(XMLReader inReader, String[] srcHrefList) {
        AttributeValueRegexpFilter attributeSrcFilter = new AttributeValueRegexpFilter(this, "src", Pattern.CASE_INSENSITIVE);
        AttributeValueRegexpFilter attributeHrefFilter = new AttributeValueRegexpFilter(this, "href", Pattern.CASE_INSENSITIVE);
        AttributeValueRegexpFilter attributeLowSrcFilter = new AttributeValueRegexpFilter(this, "lowSrc", Pattern.CASE_INSENSITIVE);
        attributeSrcFilter.setParent(inReader);
        attributeHrefFilter.setParent(attributeSrcFilter);
        attributeLowSrcFilter.setParent(attributeHrefFilter);
        XMLReader reader = attributeLowSrcFilter;
        for (int i = 0; i < srcHrefList.length; i++) {
            attributeSrcFilter.add(srcHrefList[i]);
            attributeHrefFilter.add(srcHrefList[i]);
            attributeLowSrcFilter.add(srcHrefList[i]);
        }
        return reader;
    }

    XMLReader registerElementReplaceFilter(XMLReader inReader, String[] elementReplacementList) {
        ElementReplacementFilter elementReplacementFilter = new ElementReplacementFilter(this);
        elementReplacementFilter.setParent(inReader);
        XMLReader reader = elementReplacementFilter;
        for (int i = 0; i < elementReplacementList.length; i++) {
            String[] fromTo = elementReplacementList[i].split("/");
            elementReplacementFilter.add(fromTo[0], fromTo[1]);
        }
        return reader;
    }

    XMLReader registerAttributeTypeValueStartFilter(XMLReader inReader, String[] attributeTypeValueStartList) {
        AttributeValueStartFilter attributeNameStartFilter = new AttributeValueStartFilter(this, "type");
        attributeNameStartFilter.setParent(inReader);
        XMLReader reader = attributeNameStartFilter;
        for (int i = 0; i < attributeTypeValueStartList.length; i++) {
            attributeNameStartFilter.add(attributeTypeValueStartList[i]);
        }
        return reader;
    }

    XMLReader registerStyleContentFilter(XMLReader inReader, String[] styleContentFilterList) {
        AttributeValueRegexpFilter attributeValueRegexpFilter = new AttributeValueRegexpFilter(this, "style", Pattern.CASE_INSENSITIVE);
        attributeValueRegexpFilter.setParent(inReader);
        XMLReader reader = attributeValueRegexpFilter;
        for (int i = 0; i < styleContentFilterList.length; i++) {
            attributeValueRegexpFilter.add(styleContentFilterList[i]);
        }
        return reader;
    }

    public XSSParser(String[] elementRemovalList,
        String[] attributeNameRemovalList,
        String[] attributeNameStartList,
        String[] attributeTypeValueStartList,
        String[] attributeSrcHrefList,
        String[] elementReplacementList,
        String[] styleContentList) throws SAXNotRecognizedException, SAXNotSupportedException {
        super();

        Parser parser = new Parser();
        //parser.setFeature(Parser.ignoreBogonsFeature, true);
        parser.setFeature(Parser.defaultAttributesFeature, false);
        //parser.setFeature(Parser.namespacesFeature, false);
        //parser.setFeature(Parser.namespacePrefixesFeature, false);
        parser.setFeature(Parser.useAttributes2Feature, true);

        //parser.setFeature(Par, bodyOnly)
        //parser.setFeature(Parser.namespacesFeature, false);
        //parser.setFeature(Parser.xmlnsURIsFeature, true);
        //parser.setFeature(Parser.bogonsEmptyFeature, true);

        XMLReader reader = registerBodyContentFilter(parser);


        // Element Removal
        reader = registerElementRemovalListFilter(reader, elementRemovalList);
        // Attribute Removal
        reader = registerAttributeNameRemovalFilter(reader, attributeNameRemovalList);
        // Attribute Removal Name start with
        reader = registerAttributeNameStartFilter(reader, attributeNameStartList);
        // Attribute Type Removal Vaue start with
        reader = registerAttributeTypeValueStartFilter(reader, attributeTypeValueStartList);
        // Attribute href/src/lowsrc regexp remove
        reader = registerAttributeSrcHrefFilter(reader, attributeSrcHrefList);
        // Element repleace
        reader = registerElementReplaceFilter(reader, elementReplacementList);
        // Style content remove
        reader = registerStyleContentFilter(reader, styleContentList);

        super.setParent(reader);
    }

    private String cssContentsRegexp(String literal) {
        final String cruft = "(\\s|(/\\*.*\\*/)+)*";
        StringBuffer buf = new StringBuffer(literal.length() * (cruft.length() + 1));
        for (int i = 0; i < literal.length(); i++) {
            buf.append(literal.charAt(i));
            if (i < literal.length() - 1) {
                buf.append(cruft);       // whitespace or CSS /* */ comments between letters
            }
        }
        return buf.toString();
    }

    public void logXSSChange(String message) {
        log.info("XSS Change: {}", message);
    }

    public void logXSSChange(String message, String item1) {
        log.info("XSS Change: {} {}", message, item1);
    }

    public void logXSSChange(String message, String item1, String item2) {
        log.info("XSS Change: {} {}" + message, item1, item2);
    }
}
