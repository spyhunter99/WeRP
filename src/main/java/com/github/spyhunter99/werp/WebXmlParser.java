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
import com.sun.javaee3.FilterMapping;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author AO
 */
public class WebXmlParser {

    private List<ServletElement> servlets = new ArrayList<>();
    private List<FilterElement> filters = new ArrayList<>();
    private List<FilterMapping> filterMapping = new ArrayList<>();

    public List<ServletElement> parse(File input) throws Exception {
        //Get the DOM Builder Factory
        DocumentBuilderFactory factory
                = DocumentBuilderFactory.newInstance();

        //Get the DOM Builder
        DocumentBuilder builder = factory.newDocumentBuilder();

        //Load and Parse the XML document
        //document contains the complete XML as a Tree.
        Document document = builder.parse(input);

        if ("web-app".equalsIgnoreCase(document.getDocumentElement().getNodeName())) {
            //Iterating through the nodes and extracting the data.
            NodeList nodeList = document.getDocumentElement().getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {

                //We have encountered an <employee> tag.
                Node node = nodeList.item(i);

                processWebAppNode(node);

            }
        }
        return servlets;

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
