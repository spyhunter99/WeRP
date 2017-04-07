/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.spyhunter99.werp;

import com.github.spyhunter99.werp.model.FilterElement;
import com.github.spyhunter99.werp.model.FilterMapping;
import com.github.spyhunter99.werp.model.InitParam;
import com.github.spyhunter99.werp.model.ServletElement;
import com.github.spyhunter99.werp.model.ServletMapping;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 *
 * @author AO
 */
public class WebXmlParser {

    private static final String SERVLET_MAPPING_TAG = "servlet-mapping";
    private static final String PARAM_VALUE_TAG = "param-value";
    private static final String PARAM_NAME_TAG = "param-name";
    private static final String SERVLET_CLASS_TAG = "servlet-class";

    private static final String FILTER_TAG = "filter";
    private static final String SERVLET_TAG = "servlet";
    private static final String SERVLET_NAME_TAG = "servlet-name";
    private static final String DISPLAY_NAME_TAG = "display-name";
    private static final String FILTER_NAME_TAG = "filter-name";
    private static final String FILTER_CLASS_TAG = "filter-class";
    private static final String FILTER_MAPPING_TAG = "filter-mapping";
    private static final String URL_PATTERN_TAG = "url-pattern";
    private static final String INIT_PARAM_TAG = "init-param";
    private List<ServletElement> servlets = new ArrayList<>();
    private List<FilterElement> filters = new ArrayList<>();
    private List<FilterMapping> filterMapping = new ArrayList<>();
    private List<ServletMapping> servletMapping = new ArrayList<>();
    private Document document = null;

    public List<ServletMapping> getServletMapping() {
        return servletMapping;
    }

    public void parse(File input) throws Exception {
        //Get the DOM Builder Factory
        long start = System.currentTimeMillis();
        DocumentBuilderFactory factory
                = DocumentBuilderFactory.newInstance();

        //Get the DOM Builder
        DocumentBuilder builder = factory.newDocumentBuilder();

        //Load and Parse the XML document
        //document contains the complete XML as a Tree.
        document = builder.parse(input);

        if ("web-app".equalsIgnoreCase(document.getDocumentElement().getNodeName())) {
            NodeList nodeList = document.getDocumentElement().getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                processWebAppNode(node);
            }
        } else {
            throw new Exception("not supported");
        }
        System.out.println("Parsed in " + (System.currentTimeMillis() - start) + "ms");

    }

    public void write(File output) throws Exception {
        long start = System.currentTimeMillis();
        //loop through the current document, remove any element no longer in our collection

        processWebAppNodeWrite();

        //add new elements that have been added
        // Use a Transformer for output
        TransformerFactory tFactory
                = TransformerFactory.newInstance();
        Transformer transformer
                = tFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(document);
        FileOutputStream fos = new FileOutputStream(output);
        StreamResult result = new StreamResult(fos);
        transformer.transform(source, result);
        fos.flush();
        fos.close();
        System.out.println("Written in " + (System.currentTimeMillis() - start) + "ms");
    }

    private void processWebAppNode(Node node) {

        if (SERVLET_TAG.equalsIgnoreCase(node.getNodeName())) {

            ServletElement e = new ServletElement();
            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node cNode = childNodes.item(j);
                if (SERVLET_NAME_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                    e.setServletName(cNode.getTextContent().trim());
                } else if (DISPLAY_NAME_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                    e.setDiplayName(cNode.getTextContent().trim());
                } else if (SERVLET_CLASS_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                    e.setServletClass(cNode.getTextContent().trim());
                }
            }
            servlets.add(e);

        } else if (FILTER_TAG.equalsIgnoreCase(node.getNodeName())) {
            FilterElement e = new FilterElement();
            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node cNode = childNodes.item(j);
                if (FILTER_NAME_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                    e.setFilterName(cNode.getTextContent().trim());
                } else if (INIT_PARAM_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                    NodeList childNodes2 = cNode.getChildNodes();
                    InitParam param = new InitParam();
                    for (int k = 0; k < childNodes2.getLength(); k++) {
                        Node cNode2 = childNodes2.item(j);
                        if (PARAM_NAME_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                            param.setParamName(cNode2.getTextContent().trim());
                        } else if (PARAM_VALUE_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                            param.setParamValue(cNode2.getTextContent().trim());
                        }
                    }
                    e.getParams().add(param);
                } else if (FILTER_CLASS_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                    e.setFilterClass(cNode.getTextContent());
                }
            }
            filters.add(e);

        } else if (FILTER_MAPPING_TAG.equalsIgnoreCase(node.getNodeName())) {

            FilterMapping e = new FilterMapping();
            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node cNode = childNodes.item(j);
                if (FILTER_NAME_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                    e.setFilterName(cNode.getTextContent().trim());
                } else if (URL_PATTERN_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                    e.setUrlPattern(cNode.getTextContent().trim());
                } else if (SERVLET_NAME_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                    e.setServletName(cNode.getTextContent().trim());
                }
            }
            filterMapping.add(e);
        } else if (SERVLET_MAPPING_TAG.equalsIgnoreCase(node.getNodeName())) {

            ServletMapping e = new ServletMapping();
            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node cNode = childNodes.item(j);
                if (SERVLET_NAME_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                    e.setServletName(cNode.getTextContent().trim());
                } else if (URL_PATTERN_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                    e.setUrlPattern(cNode.getTextContent().trim());
                }
            }
            servletMapping.add(e);
        } else {
            if (node.getAttributes() != null) {
                for (int i = 0; i < node.getAttributes().getLength(); i++) {
                    processWebAppNode(node.getAttributes().item(i));
                }
            }

            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node cNode = childNodes.item(j);
                processWebAppNode(cNode);

            }
        }

    }

    private void processWebAppNodeWrite() {

        doFilters();
        doFiltersMappings();

    }

    private void doFilters() {

        List<FilterElement> currentList = new ArrayList<FilterElement>();
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            //non-destructive merge
            if (FILTER_TAG.equalsIgnoreCase(node.getNodeName())) {

                FilterElement e = new FilterElement();
                NodeList childNodes = node.getChildNodes();
                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node cNode = childNodes.item(j);
                    if (FILTER_NAME_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                        e.setFilterName(cNode.getTextContent().trim());
                    } else if (INIT_PARAM_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                        NodeList childNodes2 = cNode.getChildNodes();
                        InitParam param = new InitParam();
                        for (int k = 0; k < childNodes2.getLength(); k++) {
                            Node cNode2 = childNodes2.item(j);
                            if (PARAM_NAME_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                                param.setParamName(cNode2.getTextContent().trim());
                            } else if (PARAM_VALUE_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                                param.setParamValue(cNode2.getTextContent().trim());
                            }
                        }
                        e.getParams().add(param);
                    } else if (FILTER_CLASS_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                        e.setFilterClass(cNode.getTextContent());
                    }
                }
                currentList.add(e);

            }
        }

        //ok we have everything in the original document
        for (int k = 0; k < currentList.size(); k++) {
            //is it still in the caller's desired collection
            boolean keep = false;
            for (int j = 0; j < filters.size(); j++) {
                if (filters.get(j).getFilterClass() != null && filters.get(j).getFilterClass().equalsIgnoreCase(currentList.get(k).getFilterClass())) {
                    keep = true;
                    break;
                }
            }
            if (!keep) {
                //remove the item from the xml doc

                nodeList = document.getDocumentElement().getChildNodes();
                for (int mm = 0; mm < nodeList.getLength(); mm++) {
                    Node node = nodeList.item(mm);

                    //non-destructive merge
                    if (FILTER_TAG.equalsIgnoreCase(node.getNodeName())) {

                        FilterElement e = new FilterElement();
                        NodeList childNodes = node.getChildNodes();
                        for (int j = 0; j < childNodes.getLength(); j++) {
                            Node cNode = childNodes.item(j);
                            if (FILTER_NAME_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                                e.setFilterName(cNode.getTextContent().trim());
                            } else if (INIT_PARAM_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                                NodeList childNodes2 = cNode.getChildNodes();
                                InitParam param = new InitParam();
                                for (int kk = 0; kk < childNodes2.getLength(); kk++) {
                                    Node cNode2 = childNodes2.item(j);
                                    if (PARAM_NAME_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                                        param.setParamName(cNode2.getTextContent().trim());
                                    } else if (PARAM_VALUE_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                                        param.setParamValue(cNode2.getTextContent().trim());
                                    }
                                }
                                e.getParams().add(param);
                            } else if (FILTER_CLASS_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                                e.setFilterClass(cNode.getTextContent());
                            }
                        }

                        if (e.getFilterClass() != null && e.getFilterClass().equalsIgnoreCase(currentList.get(k).getFilterClass())) {
                            document.getDocumentElement().removeChild(node);
                            break;
                        }

                    }
                }
            }

        }

        for (int j = 0; j < filters.size(); j++) {
            boolean add = true;
            for (int k = 0; k < currentList.size(); k++) {
                if (filters.get(j).getFilterClass() == null) {
                    add = false;
                    break;
                }
                if (filters.get(j).getFilterClass().equalsIgnoreCase(currentList.get(k).getFilterClass())) {
                    add = false;
                    break;
                }
            }

            if (add) {
                //ok the caller has added something new that wasn't there before, add it
                Node newfilter = document.createElement(FILTER_TAG);

                //popupate the new filter element
                Node newfilterClass = document.createElement(FILTER_CLASS_TAG);
                Node newfilterName = document.createElement(FILTER_NAME_TAG);
                newfilterName.setTextContent(filters.get(j).getFilterName());
                newfilterClass.setTextContent(filters.get(j).getFilterClass());
                for (int k = 0; k < filters.get(j).getParams().size(); k++) {
                    Node ip = document.createElement(URL_PATTERN_TAG);
                    Node pn = document.createElement(PARAM_NAME_TAG);
                    pn.setTextContent(filters.get(j).getParams().get(k).getParamName());
                    Node pv = document.createElement(PARAM_VALUE_TAG);
                    pv.setTextContent(filters.get(j).getParams().get(k).getParamValue());
                    ip.appendChild(pv);
                    ip.appendChild(pn);
                    newfilter.appendChild(ip);
                }

                newfilter.appendChild(newfilterName);
                newfilter.appendChild(newfilterClass);

                //filter, insert before the first filter 
                boolean added = false;
                nodeList = document.getDocumentElement().getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);

                    //non-destructive merge
                    if (FILTER_TAG.equalsIgnoreCase(node.getNodeName())) {
                        document.getDocumentElement().insertBefore(newfilter, node);
                        added = true;
                        break;
                    } else if (FILTER_MAPPING_TAG.equalsIgnoreCase(node.getNodeName())) {
                        document.getDocumentElement().insertBefore(newfilter, node);
                        added = true;
                        break;
                    } else if (LISTENER_TAG.equalsIgnoreCase(node.getNodeName())) {
                        document.getDocumentElement().insertBefore(newfilter, node);
                        added = true;
                        break;
                    } else if (FILTER_TAG.equalsIgnoreCase(node.getNodeName())) {
                        document.getDocumentElement().insertBefore(newfilter, node);
                        added = true;
                        break;
                    } else if (SERVLET_MAPPING_TAG.equalsIgnoreCase(node.getNodeName())) {
                        document.getDocumentElement().insertBefore(newfilter, node);
                        added = true;
                        break;
                    } else if (FILTER_TAG.equalsIgnoreCase(node.getNodeName())) {
                        document.getDocumentElement().insertBefore(newfilter, node);
                        added = true;
                        break;
                    }
                }

                if (!added) {
                    document.getDocumentElement().appendChild(newfilter);
                }
            }

        }
    }
    private static final String LISTENER_TAG = "listener";

    public List<ServletElement> getServlets() {
        return servlets;
    }

    public List<FilterElement> getFilters() {
        return filters;
    }

    public List<FilterMapping> getFilterMapping() {
        return filterMapping;
    }

    private void doFiltersMappings() {
        List<FilterMapping> currentList = new ArrayList<FilterMapping>();
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            //non-destructive merge
            if (FILTER_MAPPING_TAG.equalsIgnoreCase(node.getNodeName())) {

                FilterMapping e = new FilterMapping();
                NodeList childNodes = node.getChildNodes();
                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node cNode = childNodes.item(j);
                    if (FILTER_NAME_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                        e.setFilterName(cNode.getTextContent().trim());
                    } else if (URL_PATTERN_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                        e.setUrlPattern(cNode.getTextContent().trim());
                    } else if (SERVLET_NAME_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                        e.setServletName(cNode.getTextContent().trim());
                    }
                }
                currentList.add(e);

            }
        }

        //ok we have everything in the original document
        for (int k = 0; k < currentList.size(); k++) {
            //is it still in the caller's desired collection
            boolean keep = false;
            for (int j = 0; j < filterMapping.size(); j++) {
                if (filterMapping.get(j).getFilterName() != null && filterMapping.get(j).getFilterName().equalsIgnoreCase(currentList.get(k).getFilterName())) {
                    keep = true;
                    break;
                }
            }
            if (!keep) {
                //remove the item from the xml doc

                nodeList = document.getDocumentElement().getChildNodes();
                for (int mm = 0; mm < nodeList.getLength(); mm++) {
                    Node node = nodeList.item(mm);

                    //non-destructive merge
                    if (FILTER_MAPPING_TAG.equalsIgnoreCase(node.getNodeName())) {

                        FilterMapping e = new FilterMapping();
                        NodeList childNodes = node.getChildNodes();
                        for (int j = 0; j < childNodes.getLength(); j++) {
                            Node cNode = childNodes.item(j);
                            if (FILTER_NAME_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                                e.setFilterName(cNode.getTextContent().trim());
                            } else if (URL_PATTERN_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                                e.setUrlPattern(cNode.getTextContent().trim());
                            } else if (SERVLET_NAME_TAG.equalsIgnoreCase(cNode.getNodeName())) {
                                e.setServletName(cNode.getTextContent().trim());
                            }
                        }

                        if (e.getFilterName() != null && e.getFilterName().equalsIgnoreCase(currentList.get(k).getFilterName())) {
                            document.getDocumentElement().removeChild(node);
                            break;
                        }

                    }
                }
            }

        }

        for (int j = 0; j < filterMapping.size(); j++) {
            boolean add = true;
            for (int k = 0; k < currentList.size(); k++) {
                if (filterMapping.get(j).getFilterName() == null) {
                    add = false;
                    break;
                }
                if (filterMapping.get(j).getFilterName().equalsIgnoreCase(currentList.get(k).getFilterName())) {
                    add = false;
                    break;
                }
            }

            if (add) {
                //ok the caller has added something new that wasn't there before, add it
                Node newfilter = document.createElement(FILTER_MAPPING_TAG);

                //popupate the new filter element
                Node newfilterName = document.createElement(FILTER_NAME_TAG);
                newfilterName.setTextContent(filterMapping.get(j).getFilterName());
                newfilter.appendChild(newfilterName);
                if (filterMapping.get(j).getUrlPattern() != null) {
                    Node urlpattern = document.createElement(URL_PATTERN_TAG);
                    urlpattern.setTextContent(filterMapping.get(j).getUrlPattern());
                    newfilter.appendChild(urlpattern);
                }
                if (filterMapping.get(j).getServletName() != null) {
                    Node urlpattern = document.createElement(SERVLET_NAME_TAG);
                    urlpattern.setTextContent(filterMapping.get(j).getServletName());
                    newfilter.appendChild(urlpattern);
                }

                //filter, insert before the first filter 
                boolean added = false;
                nodeList = document.getDocumentElement().getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);

                    if (FILTER_MAPPING_TAG.equalsIgnoreCase(node.getNodeName())) {
                        document.getDocumentElement().insertBefore(newfilter, node);
                        added = true;
                        break;
                    } else if (LISTENER_TAG.equalsIgnoreCase(node.getNodeName())) {
                        document.getDocumentElement().insertBefore(newfilter, node);
                        added = true;
                        break;
                    } else if (FILTER_TAG.equalsIgnoreCase(node.getNodeName())) {
                        document.getDocumentElement().insertBefore(newfilter, node);
                        added = true;
                        break;
                    } else if (SERVLET_MAPPING_TAG.equalsIgnoreCase(node.getNodeName())) {
                        document.getDocumentElement().insertBefore(newfilter, node);
                        added = true;
                        break;
                    } else if (FILTER_TAG.equalsIgnoreCase(node.getNodeName())) {
                        document.getDocumentElement().insertBefore(newfilter, node);
                        added = true;
                        break;
                    }
                }

                if (!added) {
                    document.getDocumentElement().appendChild(newfilter);
                }
            }

        }
    }

}
