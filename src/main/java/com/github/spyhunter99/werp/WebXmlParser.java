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
import org.w3c.dom.Element;

/**
 *
 * @author AO
 */
public class WebXmlParser {

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

    }

    public void write(File output) throws Exception {
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
    }

    private void processWebAppNode(Node node) {

        System.out.println(node.getNodeName());
        System.out.println(node.getNodeValue());

        if ("servlet".equalsIgnoreCase(node.getNodeName())) {

            ServletElement e = new ServletElement();
            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node cNode = childNodes.item(j);
                if ("servlet-name".equalsIgnoreCase(cNode.getNodeName())) {
                    e.setServletName(cNode.getTextContent().trim());
                } else if ("display-name".equalsIgnoreCase(cNode.getNodeName())) {
                    e.setDiplayName(cNode.getTextContent().trim());
                } else if ("servlet-class".equalsIgnoreCase(cNode.getNodeName())) {
                    e.setServletClass(cNode.getTextContent().trim());
                }
            }
            servlets.add(e);

        } else if ("filter".equalsIgnoreCase(node.getNodeName())) {
            FilterElement e = new FilterElement();
            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node cNode = childNodes.item(j);
                if ("filter-name".equalsIgnoreCase(cNode.getNodeName())) {
                    e.setFilterName(cNode.getTextContent().trim());
                } else if ("init-param".equalsIgnoreCase(cNode.getNodeName())) {
                    NodeList childNodes2 = cNode.getChildNodes();
                    InitParam param = new InitParam();
                    for (int k = 0; k < childNodes2.getLength(); k++) {
                        Node cNode2 = childNodes2.item(j);
                        if ("param-name".equalsIgnoreCase(cNode.getNodeName())) {
                            param.setParamName(cNode2.getTextContent().trim());
                        } else if ("param-value".equalsIgnoreCase(cNode.getNodeName())) {
                            param.setParamValue(cNode2.getTextContent().trim());
                        }
                    }
                    e.getParams().add(param);
                } else if ("filter-class".equalsIgnoreCase(cNode.getNodeName())) {
                    e.setFilterName(cNode.getTextContent());
                }
            }
            filters.add(e);

        } else if ("filter-mapping".equalsIgnoreCase(node.getNodeName())) {

            FilterMapping e = new FilterMapping();
            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node cNode = childNodes.item(j);
                if ("filter-name".equalsIgnoreCase(cNode.getNodeName())) {
                    e.setFilterName(cNode.getTextContent().trim());
                } else if ("url-pattern".equalsIgnoreCase(cNode.getNodeName())) {
                    e.setUrlPattern(cNode.getTextContent().trim());
                }
            }
            filterMapping.add(e);
        } else if ("servlet-mapping".equalsIgnoreCase(node.getNodeName())) {

            ServletMapping e = new ServletMapping();
            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node cNode = childNodes.item(j);
                if ("servlet-name".equalsIgnoreCase(cNode.getNodeName())) {
                    e.setServletName(cNode.getTextContent().trim());
                } else if ("url-pattern".equalsIgnoreCase(cNode.getNodeName())) {
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

    }

    private void doFilters() {

        List<FilterElement> currentList = new ArrayList<FilterElement>();
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            //non-destructive merge
            if ("filter".equalsIgnoreCase(node.getNodeName())) {

                FilterElement e = new FilterElement();
                NodeList childNodes = node.getChildNodes();
                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node cNode = childNodes.item(j);
                    if ("filter-name".equalsIgnoreCase(cNode.getNodeName())) {
                        e.setFilterName(cNode.getTextContent().trim());
                    } else if ("init-param".equalsIgnoreCase(cNode.getNodeName())) {
                        NodeList childNodes2 = cNode.getChildNodes();
                        InitParam param = new InitParam();
                        for (int k = 0; k < childNodes2.getLength(); k++) {
                            Node cNode2 = childNodes2.item(j);
                            if ("param-name".equalsIgnoreCase(cNode.getNodeName())) {
                                param.setParamName(cNode2.getTextContent().trim());
                            } else if ("param-value".equalsIgnoreCase(cNode.getNodeName())) {
                                param.setParamValue(cNode2.getTextContent().trim());
                            }
                        }
                        e.getParams().add(param);
                    } else if ("filter-class".equalsIgnoreCase(cNode.getNodeName())) {
                        e.setFilterName(cNode.getTextContent());
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
                    if ("filter".equalsIgnoreCase(node.getNodeName())) {

                        FilterElement e = new FilterElement();
                        NodeList childNodes = node.getChildNodes();
                        for (int j = 0; j < childNodes.getLength(); j++) {
                            Node cNode = childNodes.item(j);
                            if ("filter-name".equalsIgnoreCase(cNode.getNodeName())) {
                                e.setFilterName(cNode.getTextContent().trim());
                            } else if ("init-param".equalsIgnoreCase(cNode.getNodeName())) {
                                NodeList childNodes2 = cNode.getChildNodes();
                                InitParam param = new InitParam();
                                for (int kk = 0; kk < childNodes2.getLength(); kk++) {
                                    Node cNode2 = childNodes2.item(j);
                                    if ("param-name".equalsIgnoreCase(cNode.getNodeName())) {
                                        param.setParamName(cNode2.getTextContent().trim());
                                    } else if ("param-value".equalsIgnoreCase(cNode.getNodeName())) {
                                        param.setParamValue(cNode2.getTextContent().trim());
                                    }
                                }
                                e.getParams().add(param);
                            } else if ("filter-class".equalsIgnoreCase(cNode.getNodeName())) {
                                e.setFilterName(cNode.getTextContent());
                            }
                        }

                        if (e.getFilterClass() != null && e.getFilterClass().equalsIgnoreCase(currentList.get(k).getFilterClass())) {
                            document.removeChild(node);
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
                Node newfilter = document.createElement("filter");

                //popupate the new filter element
                Node newfilterClass = document.createElement("filter-class");
                Node newfilterName = document.createElement("filter-name");
                newfilterName.setTextContent(filters.get(j).getFilterName());
                newfilterClass.setTextContent(filters.get(j).getFilterClass());
                for (int k = 0; k < filters.get(j).getParams().size(); k++) {
                    Node ip = document.createElement("init-param");
                    Node pn = document.createElement("param-name");
                    pn.setTextContent(filters.get(j).getParams().get(k).getParamName());
                    Node pv = document.createElement("param-value");
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
                    if ("filter".equalsIgnoreCase(node.getNodeName())) {
                        document.getDocumentElement().insertBefore(newfilter, node);
                        added = true;
                        break;
                    } else if ("filter-mapping".equalsIgnoreCase(node.getNodeName())) {
                        document.getDocumentElement().insertBefore(newfilter, node);
                        added = true;
                        break;
                    } else if ("listener".equalsIgnoreCase(node.getNodeName())) {
                        document.getDocumentElement().insertBefore(newfilter, node);
                        added = true;
                        break;
                    } else if ("servlet".equalsIgnoreCase(node.getNodeName())) {
                        document.getDocumentElement().insertBefore(newfilter, node);
                        added = true;
                        break;
                    } else if ("servlet-mapping".equalsIgnoreCase(node.getNodeName())) {
                        document.getDocumentElement().insertBefore(newfilter, node);
                        added = true;
                        break;
                    } else if ("servlet".equalsIgnoreCase(node.getNodeName())) {
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

    public List<ServletElement> getServlets() {
        return servlets;
    }

    public List<FilterElement> getFilters() {
        return filters;
    }

    public List<FilterMapping> getFilterMapping() {
        return filterMapping;
    }

}
