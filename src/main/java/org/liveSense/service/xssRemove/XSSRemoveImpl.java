/*
 *  Copyright 2010 Robert Csakany <robson@semmi.se>.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.liveSense.service.xssRemove;
/**
 *
 * @author Robert Csakany (robson@semmi.se)
 * @created Feb 13, 2010
 */

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Dictionary;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.ObservationManager;
import org.apache.sling.commons.osgi.OsgiUtil;

import org.apache.sling.jcr.api.SlingRepository;
import org.ccil.cowan.tagsoup.XMLWriter;
import org.liveSense.core.AdministrativeService;
import org.liveSense.core.Configurator;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * Observe the users content for changes, and generate
 * thumbnails when images are added.
 *
 * @scr.component immediate="true" metatype="true"
 *
 */

public class XSSRemoveImpl extends AdministrativeService implements XSSRemove {

    private static final Logger log = LoggerFactory.getLogger(XSSRemoveImpl.class);

    private ObservationManager observationManager;
    /** 
     * @scr.reference
     */
    private SlingRepository repository;

    /**
     * @scr.reference
     */
    private Configurator configurator;

    /**
     * @scr.property    label="%contentPathes.name"
     *                  description="%contentPathes.description"
     *                  valueRef="DEFAULT_CONTENT_PATHES"
     */
    public static final String PARAM_CONTENT_PATHES = "contentPathes";
    public static final String[] DEFAULT_CONTENT_PATHES = {"/sites", "/users"};
    private String[] contentPathes = DEFAULT_CONTENT_PATHES;

    /**
     * @scr.property    label="%supportedMimeTypes.name"
     *                  description="%supportedMimeTypes.description"
     *                  valueRef="DEFAULT_SUPPORTED_MIME_TYPES"
     */
    public static final String PARAM_SUPPORTED_MIME_TYPES = "supportedMimeTypes";
    public static final String[] DEFAULT_SUPPORTED_MIME_TYPES = {"text/html"};
    private String[] supportedMimeTypes = DEFAULT_SUPPORTED_MIME_TYPES;



    /**
     * @scr.property    label="%elementRemovalList.name"
     *                  description="%elementRemovalList.description"
     *                  valueRef="DEFAULT_ELEMENT_REMOVAL_LIST"
     */
    public static final String PARAM_ELEMENT_REMOVAL_LIST = "elementRemovalList";
    public static final String[] DEFAULT_ELEMENT_REMOVAL_LIST = {"script", "applet", "embed", "xml", "bgsound", "meta", "link", "style", "base"};
    private String[] elementRemovalList = DEFAULT_ELEMENT_REMOVAL_LIST;


    /**
     * @scr.property    label="%attributeNameRemovalList.name"
     *                  description="%attributeNameRemovalList.description"
     *                  valueRef="DEFAULT_ATTRIBUTE_NAME_REMOVAL_LIST"
     */
    public static final String PARAM_ATTRIBUTE_NAME_REMOVAL_LIST = "attributeNameRemovalList";
    public static final String[] DEFAULT_ATTRIBUTE_NAME_REMOVAL_LIST = {"onload", "onclick", "onchange", "onsubmit", "onmouseover", "onerror", "dynsrc", "datasrc", "datafld", "dataformatas"};
    private String[] attributeNameRemovalList = DEFAULT_ATTRIBUTE_NAME_REMOVAL_LIST;


    /**
     * @scr.property    label="%attributeNameStartList.name"
     *                  description="%attributeNameStartList.description"
     *                  valueRef="DEFAULT_ATTRIBUTE_NAME_START_LIST"
     */
    public static final String PARAM_ATTRIBUTE_NAME_START_LIST = "attributeNameStartList";
    public static final String[] DEFAULT_ATTRIBUTE_NAME_START_LIST = {"on"};
    private String[] attributeNameStartList = DEFAULT_ATTRIBUTE_NAME_START_LIST;



    /**
     * @scr.property    label="%attributeTypeValueStartList.name"
     *                  description="%attributeTypeValueStartList.description"
     *                  valueRef="DEFAULT_ATTRIBUTE_TYPE_VALUE_START_LIST"
     */
    public static final String PARAM_ATTRIBUTE_TYPE_VALUE_START_LIST = "attributeTypeValueStartList";
    public static final String[] DEFAULT_ATTRIBUTE_TYPE_VALUE_START_LIST =  {"text/javascript"};
    private String[] attributeTypeValueStartList = DEFAULT_ATTRIBUTE_TYPE_VALUE_START_LIST;


    /**
     * @scr.property    label="%attributeSrcHrefList.name"
     *                  description="%attributeSrcHrefList.description"
     *                  valueRef="DEFAULT_ATTRIBUTE_SRC_HREF_LIST"
     */
    public static final String PARAM_ATTRIBUTE_SRC_HREF_LIST = "attributeSrcHrefList";
    public static final String[] DEFAULT_ATTRIBUTE_SRC_HREF_LIST = {"javascript:", // javascript
            "^\\s*j\\s*a\\s*v\\s*a\\s*s\\s*c\\s*r\\s*i\\s*p\\s*t\\s*:", // javascript
            "^\\s*v\\s*i\\s*e\\s*w\\s*-s\\s*o\\s*u\\s*r\\s*c\\s*e\\s*:", // view-source
            "^\\s*d\\s*a\\s*t\\s*a\\s*:", // data
            "^\\s*v\\s*b\\s*s\\s*s\\s*r\\s*i\\s*p\\s*t\\s*:", // vbscript
            "^\\s*a\\s*b\\s*o\\s*u\\s*t\\s*:", // about
            "^\\s*s\\s*h\\s*e\\s*e\\s*l\\s*:" // shell
    };
    private String[] attributeSrcHrefList = DEFAULT_ATTRIBUTE_SRC_HREF_LIST;


    /**
     * @scr.property    label="%elementReplacementList.name"
     *                  description="%elementReplacementList.description"
     *                  valueRef="DEFAULT_ELEMENT_REPLACEMENT_LIST"
     */
    public static final String PARAM_ELEMENT_REPLACEMENT_LIST = "elementReplacementList";
    public static final String[] DEFAULT_ELEMENT_REPLACEMENT_LIST = {"html/div", "head/div", "body/div", "iframe/div", "frame/div", "frameset/div", "layer/div", "ilayer/div", "blink/span", "object/div"};
    private String[] elementReplacementList = DEFAULT_ELEMENT_REPLACEMENT_LIST;


    /**
     * @scr.property    label="%styleContentList.name"
     *                  description="%styleContentList.description"
     *                  valueRef="DEFAULT_STYLE_CONTENT_LIST"
     */
    public static final String PARAM_STYLE_CONTENT_LIST = "styleContentList";
    public static final String[] DEFAULT_STYLE_CONTENT_LIST = {"javascript", "expression"};
    private String[] styleContentList = DEFAULT_STYLE_CONTENT_LIST;


    Session session;

    private ArrayList<XSSRemoveEventListener> eventListeners = new ArrayList<XSSRemoveEventListener>();
    private XSSParser parser;

    //private XMLWriter xmlWriter;
    /**
     * Activates this component.
     *
     * @param componentContext The OSGi <code>ComponentContext</code> of this
     *            component.
     */
    protected void activate(ComponentContext componentContext) throws RepositoryException {
        Dictionary<?, ?> props = componentContext.getProperties();
        
        // Setting up contentPathes
        String[] contentPathesNew = OsgiUtil.toStringArray(componentContext.getProperties().get(PARAM_CONTENT_PATHES), DEFAULT_CONTENT_PATHES);
        boolean contentPathesChanged = false;
        if (contentPathesNew.length != this.contentPathes.length) {
            contentPathesChanged = true;
        } else {
            for (int i = 0; i < contentPathesNew.length; i++) {
                if (!contentPathesNew[i].equals(this.contentPathes[i])) {
                    contentPathesChanged = true;
                }
            }
            if (contentPathesChanged) {
                StringBuffer contentPathesValueList = new StringBuffer();
                StringBuffer contentPathesNewValueList = new StringBuffer();

                for (int i = 0; i < contentPathesNew.length; i++) {
                    if (i != 0) {
                        contentPathesNewValueList.append(", ");
                    }
                    contentPathesNewValueList.append(contentPathesNew[i].toString());
                }

                for (int i = 0; i < contentPathes.length; i++) {
                    if (i != 0) {
                        contentPathesValueList.append(", ");
                    }
                    contentPathesValueList.append(contentPathes[i].toString());
                }
                log.info("Setting new contentPathes: {}) (was: {})", contentPathesNewValueList.toString(), contentPathesValueList.toString());
                this.contentPathes = contentPathesNew;
            }
        }

       
        // Setting up elementRemovalList
        String[] elementRemovalListNew = OsgiUtil.toStringArray(componentContext.getProperties().get(PARAM_ELEMENT_REMOVAL_LIST), DEFAULT_ELEMENT_REMOVAL_LIST);
        boolean elementRemovalListChanged = false;
        if (elementRemovalListNew.length != this.elementRemovalList.length) {
            elementRemovalListChanged = true;
        } else {
            for (int i = 0; i < elementRemovalListNew.length; i++) {
                if (!elementRemovalListNew[i].equals(this.elementRemovalList[i])) {
                    elementRemovalListChanged = true;
                }
            }
            if (elementRemovalListChanged) {
                StringBuffer elementRemovalListValueList = new StringBuffer();
                StringBuffer elementRemovalListNewValueList = new StringBuffer();

                for (int i = 0; i < elementRemovalListNew.length; i++) {
                    if (i != 0) {
                        elementRemovalListNewValueList.append(", ");
                    }
                    elementRemovalListNewValueList.append(elementRemovalListNew[i].toString());
                }

                for (int i = 0; i < elementRemovalList.length; i++) {
                    if (i != 0) {
                        elementRemovalListValueList.append(", ");
                    }
                    elementRemovalListValueList.append(elementRemovalList[i].toString());
                }
                log.info("Setting new elementRemovalList: {}) (was: {})", elementRemovalListNewValueList.toString(), elementRemovalListValueList.toString());
                this.elementRemovalList = elementRemovalListNew;
            }
        }

        // Setting up attributeNameRemovalList
        String[] attributeNameRemovalListNew = OsgiUtil.toStringArray(componentContext.getProperties().get(PARAM_ATTRIBUTE_NAME_REMOVAL_LIST), DEFAULT_ATTRIBUTE_NAME_REMOVAL_LIST);
        boolean attributeNameRemovalListChanged = false;
        if (attributeNameRemovalListNew.length != this.attributeNameRemovalList.length) {
            attributeNameRemovalListChanged = true;
        } else {
            for (int i = 0; i < attributeNameRemovalListNew.length; i++) {
                if (!attributeNameRemovalListNew[i].equals(this.attributeNameRemovalList[i])) {
                    attributeNameRemovalListChanged = true;
                }
            }
            if (attributeNameRemovalListChanged) {
                StringBuffer attributeNameRemovalListValueList = new StringBuffer();
                StringBuffer attributeNameRemovalListNewValueList = new StringBuffer();

                for (int i = 0; i < attributeNameRemovalListNew.length; i++) {
                    if (i != 0) {
                        attributeNameRemovalListNewValueList.append(", ");
                    }
                    attributeNameRemovalListNewValueList.append(attributeNameRemovalListNew[i].toString());
                }

                for (int i = 0; i < attributeNameRemovalList.length; i++) {
                    if (i != 0) {
                        attributeNameRemovalListValueList.append(", ");
                    }
                    attributeNameRemovalListValueList.append(attributeNameRemovalList[i].toString());
                }
                log.info("Setting new attributeNameRemovalList: {}) (was: {})", attributeNameRemovalListNewValueList.toString(), attributeNameRemovalListValueList.toString());
                this.attributeNameRemovalList = attributeNameRemovalListNew;
            }
        }

        // Setting up attributeNameStartList
        String[] attributeNameStartListNew = OsgiUtil.toStringArray(componentContext.getProperties().get(PARAM_ATTRIBUTE_NAME_START_LIST), DEFAULT_ATTRIBUTE_NAME_START_LIST);
        boolean attributeNameStartListChanged = false;
        if (attributeNameStartListNew.length != this.attributeNameStartList.length) {
            attributeNameStartListChanged = true;
        } else {
            for (int i = 0; i < attributeNameStartListNew.length; i++) {
                if (!attributeNameStartListNew[i].equals(this.attributeNameStartList[i])) {
                    attributeNameStartListChanged = true;
                }
            }
            if (attributeNameStartListChanged) {
                StringBuffer attributeNameStartListValueList = new StringBuffer();
                StringBuffer attributeNameStartListNewValueList = new StringBuffer();

                for (int i = 0; i < attributeNameStartListNew.length; i++) {
                    if (i != 0) {
                        attributeNameStartListNewValueList.append(", ");
                    }
                    attributeNameStartListNewValueList.append(attributeNameStartListNew[i].toString());
                }

                for (int i = 0; i < attributeNameStartList.length; i++) {
                    if (i != 0) {
                        attributeNameStartListValueList.append(", ");
                    }
                    attributeNameStartListValueList.append(attributeNameStartList[i].toString());
                }
                log.info("Setting new attributeNameStartList: {}) (was: {})", attributeNameStartListNewValueList.toString(), attributeNameStartListValueList.toString());
                this.attributeNameStartList = attributeNameStartListNew;
            }
        }


        // Setting up attributeTypeValueStartList
        String[] attributeTypeValueStartListNew = OsgiUtil.toStringArray(componentContext.getProperties().get(PARAM_ATTRIBUTE_TYPE_VALUE_START_LIST), DEFAULT_ATTRIBUTE_TYPE_VALUE_START_LIST);
        boolean attributeTypeValueStartListChanged = false;
        if (attributeTypeValueStartListNew.length != this.attributeTypeValueStartList.length) {
            attributeTypeValueStartListChanged = true;
        } else {
            for (int i = 0; i < attributeTypeValueStartListNew.length; i++) {
                if (!attributeTypeValueStartListNew[i].equals(this.attributeTypeValueStartList[i])) {
                    attributeTypeValueStartListChanged = true;
                }
            }
            if (attributeTypeValueStartListChanged) {
                StringBuffer attributeTypeValueStartListValueList = new StringBuffer();
                StringBuffer attributeTypeValueStartListNewValueList = new StringBuffer();

                for (int i = 0; i < attributeTypeValueStartListNew.length; i++) {
                    if (i != 0) {
                        attributeTypeValueStartListNewValueList.append(", ");
                    }
                    attributeTypeValueStartListNewValueList.append(attributeTypeValueStartListNew[i].toString());
                }

                for (int i = 0; i < attributeTypeValueStartList.length; i++) {
                    if (i != 0) {
                        attributeTypeValueStartListValueList.append(", ");
                    }
                    attributeTypeValueStartListValueList.append(attributeTypeValueStartList[i].toString());
                }
                log.info("Setting new attributeTypeValueStartList: {}) (was: {})", attributeTypeValueStartListNewValueList.toString(), attributeTypeValueStartListValueList.toString());
                this.attributeTypeValueStartList = attributeTypeValueStartListNew;
            }
        }


        // Setting up attributeSrcHrefList
        String[] attributeSrcHrefListNew = OsgiUtil.toStringArray(componentContext.getProperties().get(PARAM_ATTRIBUTE_SRC_HREF_LIST), DEFAULT_ATTRIBUTE_SRC_HREF_LIST);
        boolean attributeSrcHrefListChanged = false;
        if (attributeSrcHrefListNew.length != this.attributeSrcHrefList.length) {
            attributeSrcHrefListChanged = true;
        } else {
            for (int i = 0; i < attributeSrcHrefListNew.length; i++) {
                if (!attributeSrcHrefListNew[i].equals(this.attributeSrcHrefList[i])) {
                    attributeSrcHrefListChanged = true;
                }
            }
            if (attributeSrcHrefListChanged) {
                StringBuffer attributeSrcHrefListValueList = new StringBuffer();
                StringBuffer attributeSrcHrefListNewValueList = new StringBuffer();

                for (int i = 0; i < attributeSrcHrefListNew.length; i++) {
                    if (i != 0) {
                        attributeSrcHrefListNewValueList.append(", ");
                    }
                    attributeSrcHrefListNewValueList.append(attributeSrcHrefListNew[i].toString());
                }

                for (int i = 0; i < attributeSrcHrefList.length; i++) {
                    if (i != 0) {
                        attributeSrcHrefListValueList.append(", ");
                    }
                    attributeSrcHrefListValueList.append(attributeSrcHrefList[i].toString());
                }
                log.info("Setting new attributeSrcHrefList: {}) (was: {})", attributeSrcHrefListNewValueList.toString(), attributeSrcHrefListValueList.toString());
                this.attributeSrcHrefList = attributeSrcHrefListNew;
            }
        }


        // Setting up elementReplacementList
        String[] elementReplacementListNew = OsgiUtil.toStringArray(componentContext.getProperties().get(PARAM_ELEMENT_REPLACEMENT_LIST), DEFAULT_ELEMENT_REPLACEMENT_LIST);
        boolean elementReplacementListChanged = false;
        if (elementReplacementListNew.length != this.elementReplacementList.length) {
            elementReplacementListChanged = true;
        } else {
            for (int i = 0; i < elementReplacementListNew.length; i++) {
                if (!elementReplacementListNew[i].equals(this.elementReplacementList[i])) {
                    elementReplacementListChanged = true;
                }
            }
            if (elementReplacementListChanged) {
                StringBuffer elementReplacementListValueList = new StringBuffer();
                StringBuffer elementReplacementListNewValueList = new StringBuffer();

                for (int i = 0; i < elementReplacementListNew.length; i++) {
                    if (i != 0) {
                        elementReplacementListNewValueList.append(", ");
                    }
                    elementReplacementListNewValueList.append(elementReplacementListNew[i].toString());
                }

                for (int i = 0; i < elementReplacementList.length; i++) {
                    if (i != 0) {
                        elementReplacementListValueList.append(", ");
                    }
                    elementReplacementListValueList.append(elementReplacementList[i].toString());
                }
                log.info("Setting new elementReplacementList: {}) (was: {})", elementReplacementListNewValueList.toString(), elementReplacementListValueList.toString());
                this.elementReplacementList = elementReplacementListNew;
            }
        }



        // Setting up styleContentList
        String[] styleContentListNew = OsgiUtil.toStringArray(componentContext.getProperties().get(PARAM_STYLE_CONTENT_LIST), DEFAULT_STYLE_CONTENT_LIST);
        boolean styleContentListChanged = false;
        if (styleContentListNew.length != this.styleContentList.length) {
            styleContentListChanged = true;
        } else {
            for (int i = 0; i < styleContentListNew.length; i++) {
                if (!styleContentListNew[i].equals(this.styleContentList[i])) {
                    styleContentListChanged = true;
                }
            }
            if (styleContentListChanged) {
                StringBuffer styleContentListValueList = new StringBuffer();
                StringBuffer styleContentListNewValueList = new StringBuffer();

                for (int i = 0; i < styleContentListNew.length; i++) {
                    if (i != 0) {
                        styleContentListNewValueList.append(", ");
                    }
                    styleContentListNewValueList.append(styleContentListNew[i].toString());
                }

                for (int i = 0; i < styleContentList.length; i++) {
                    if (i != 0) {
                        styleContentListValueList.append(", ");
                    }
                    styleContentListValueList.append(styleContentList[i].toString());
                }
                log.info("Setting new styleContentList: {}) (was: {})", styleContentListNewValueList.toString(), styleContentListValueList.toString());
                this.styleContentList = styleContentListNew;
            }
        }

        try {
            parser = new XSSParser(elementRemovalList, attributeNameRemovalList, attributeNameStartList, attributeTypeValueStartList, attributeSrcHrefList, elementReplacementList, styleContentList);
        } catch (SAXNotRecognizedException ex) {
            log.error("Parser init error: ",ex);
        } catch (SAXNotSupportedException ex) {
            log.error("Parser init error: ",ex);
        }


   //     xmlWriter = new XMLWriter(w);
    //    xmlWriter.setOutputProperty(XMLWriter.OMIT_XML_DECLARATION, "yes");
    //    parser = new XSSParser();

        session = getAdministrativeSession(repository);
        if (repository.getDescriptor(Repository.OPTION_OBSERVATION_SUPPORTED).equals("true")) {
            observationManager = session.getWorkspace().getObservationManager();
            //String[] types = {"nt:resource"};
            for (int i = 0; i < contentPathes.length; i++) {
                String[] propType = {"nt:resource"};
                XSSRemoveEventListener listener = new XSSRemoveEventListener(this);
                observationManager.addEventListener(listener, Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED | Event.PROPERTY_REMOVED, contentPathes[i], true, null, propType, true);
                eventListeners.add(listener);

                String[] fileType = {"nt:file"};
                listener = new XSSRemoveEventListener(this);
                observationManager.addEventListener(listener, Event.NODE_REMOVED, contentPathes[i], true, null, fileType, true);
                eventListeners.add(listener);
            }
        }
    }
    

    @Override
    public void deactivate(ComponentContext componentContext) throws RepositoryException {
        super.deactivate(componentContext);
        if (observationManager != null) {
            for (XSSRemoveEventListener listener : eventListeners) {
                observationManager.removeEventListener(listener);
            }
        }
    }

    boolean isValidMimeType(Node node) throws RepositoryException {
        if (node.getNode("jcr:content").hasProperty("jcr:mimeType")) {
            final String mimeType = node.getNode("jcr:content").getProperty("jcr:mimeType").getString();

            for (int i = 0; i < supportedMimeTypes.length; i++) {
                String actMimeType = supportedMimeTypes[i];
                if (mimeType.equals(actMimeType)) {
                    return true;
                }
            }
        } else {
            return false;
        }
        return false;
    }

    public void removeXSSsecurityVulnerability(String parentFolder, String fileName) throws RepositoryException, Exception {
        if (!session.isLive()) session = getAdministrativeSession(repository);
        Node node = session.getRootNode().getNode(parentFolder+"/"+fileName);
        if (isValidMimeType(node)) {
            log.info("Removing XSS Vulnerability codes for node {}", node.getPath());

            StringWriter sw = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(sw);
            xmlWriter.setOutputProperty(XMLWriter.OMIT_XML_DECLARATION, "yes");
            xmlWriter.setOutputProperty(XMLWriter.ENCODING, configurator.getEncoding());
            parser.setContentHandler(xmlWriter);         
            parser.parse(new InputSource(new InputStreamReader(node.getNode("jcr:content").getProperty("jcr:data").getStream(), configurator.getEncoding())));
            node.getNode("jcr:content").setProperty("jcr:data", new ByteArrayInputStream(sw.toString().getBytes(configurator.getEncoding())));

        } else {
            log.info("No XSS  Vulnerability remove, not a HTML: {} - {}", node.getPath(), node.getNode("jcr:content").getProperty("jcr:mimeType"));
        }
        session.save();
    }

}

