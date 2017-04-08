/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.spyhunter99.werp;

import com.github.spyhunter99.werp.beans.Bean;
import com.github.spyhunter99.werp.beans.Endpoint;
import com.github.spyhunter99.werp.beans.Handler;
import com.github.spyhunter99.werp.beans.HandlerRef;
import com.github.spyhunter99.werp.beans.JaxRsEndpoint;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Beans.xml isn't deterministic, therefore this can be used to programmatically
 * attach handlers to cxf beans
 *
 * several scenarios - services with or without handlers - global handlers
 * attached to the bus -
 *
 * @author AO
 */
public class BeamsXmlParser {

    private static final String NS_JAXWS = "http://cxf.apache.org/jaxws";
    private static final String NS_JAXRS = "http://cxf.apache.org/jaxrs";
    private static final String CXF_CORE = "http://cxf.apache.org/core";
    private static final String BEAN_TAG = "bean";
    private Document document = null;
    private Map<String, String> prefixNamespaceMap = new HashMap<String, String>();
    String jaxws_prefix = "jaxws";
    String jaxrs_prefix = "jaxrs";
    String cxf_prefix = "cxf";

    //TODO global cxf:bus interceptors
    private List<Bean> beans = new ArrayList<Bean>();
    private List<Endpoint> jaxWsEndpoints = new ArrayList<Endpoint>();
    private List<JaxRsEndpoint> jaxRsEndpoints = new ArrayList<JaxRsEndpoint>();

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

        if ("beans".equalsIgnoreCase(document.getDocumentElement().getNodeName())) {
            for (int i = 0; i < document.getDocumentElement().getAttributes().getLength(); i++) {
                Node item = document.getDocumentElement().getAttributes().item(i);

                if (item.getNodeName().startsWith("xmlns:")) {
                    prefixNamespaceMap.put(item.getNodeName().replace("xmlns:", ""), item.getTextContent().trim());
                }
                if (NS_JAXWS.equals(item.getTextContent().trim())) {
                    jaxws_prefix = item.getNodeName().replace("xmlns:", "");
                }
                if (NS_JAXRS.equals(item.getTextContent().trim())) {
                    jaxrs_prefix = item.getNodeName().replace("xmlns:", "");
                }
                if (CXF_CORE.equals(item.getTextContent().trim())) {
                    cxf_prefix = item.getNodeName().replace("xmlns:", "");
                }
                if ("xmlns".equals(item.getPrefix())) {
                    prefixNamespaceMap.put(item.getNodeName(), item.getTextContent().trim());
                }
            }

            NodeList nodeList = document.getDocumentElement().getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                processBeansNode(node);
            }
        } else {
            throw new Exception("not supported");
        }
        System.out.println("Parsed in " + (System.currentTimeMillis() - start) + "ms");
    }

    public void write(File output) {

        //get all the beans 
        //compare doc list with the active list
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            processBeansNodeWrite(node);
        }

    }

    private void processBeansNode(Node node) {
        String nodeName = node.getNodeName();
        if (BEAN_TAG.equalsIgnoreCase(nodeName)) {

            addBean(node);

        } else if ((jaxws_prefix + ":endpoint").equals(nodeName) || ("endpoint".equals(nodeName) && isNamespace(node, NS_JAXWS))) {
            //definitely what we're looking for.
            scanEndpoints(node);

        } else if ((jaxrs_prefix + ":server").equals(nodeName) || ("server".equals(nodeName) && isNamespace(node, NS_JAXRS))) {
            //jaxrs server
            scanJaxRsServer(node);
        }
        //cxf:bus
    }

    private void addBean(Node node) {
        Bean b = new Bean();
        for (int i = 0; i < node.getAttributes().getLength(); i++) {
            Node item = node.getAttributes().item(i);
            if ("id".equals(item.getNodeName())) {
                b.setId(item.getTextContent().trim());
            }
            if ("class".equals(item.getNodeName())) {
                b.setClazz(item.getTextContent().trim());
            }
        }
        beans.add(b);
    }

    private void scanEndpoints(Node node) {
        Endpoint ep = new Endpoint();
        for (int i = 0; i < node.getAttributes().getLength(); i++) {
            Node item = node.getAttributes().item(i);
            if ("id".equals(item.getNodeName())) {
                ep.setId(item.getTextContent().trim());

            }
            if ("implementor".equals(item.getNodeName())) {
                ep.setImplementor(item.getTextContent().trim());
            }
            if ("address".equals(item.getNodeName())) {
                ep.setAddress(item.getTextContent().trim());
            }

        }
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            Node item = node.getChildNodes().item(i);
            if ((jaxws_prefix + ":handlers").equals(item.getNodeName()) || ("handlers".equals(item.getNodeName()) && isNamespace(node, NS_JAXWS))) {
//TODO
                addHandlers(ep, item);
            }
        }
        //potential child elements that we care about
        //jaxws:inInterceptors
        //jaxws:outInterceptors
        //jaxws:handlers
        jaxWsEndpoints.add(ep);

    }

    private boolean isNamespace(Node node, String ns) {
        return (ns.equals(node.getNamespaceURI()) || node.getPrefix() != null && prefixNamespaceMap.containsKey(node.getPrefix()) && ns.equals(prefixNamespaceMap.get(node.getPrefix())));
    }

    private void addHandlers(Endpoint ep, Node item) {
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            Node node = item.getChildNodes().item(i);
            //expecting one of 'ref' or 'bean'
            if ("ref".equals(node.getNodeName()) && isNamespace(node, NS_JAXWS)) {
                Handler h = new Handler();

                HandlerRef ref = new HandlerRef();
                ref.setBean(node.getAttributes().getNamedItem("bean").getTextContent().trim());
                h.setReferences(ref);
                ep.getHandlers().add(h);
            } else if ("bean".equals(node.getNodeName()) && isNamespace(node, NS_JAXWS)) {
                Handler h = new Handler();
                h.setBean(new Bean());
                Node attr;
                attr = node.getAttributes().getNamedItem("id");
                if (attr != null) {
                    h.getBean().setId(attr.getTextContent());
                }
                attr = node.getAttributes().getNamedItem("class");
                if (attr != null) {
                    h.getBean().setClazz(attr.getTextContent());
                }
            }
        }
    }

    public List<Bean> getBeans() {
        return beans;
    }

    public void setBeans(List<Bean> beans) {
        this.beans = beans;
    }

    public List<Endpoint> getJaxWsEndpoints() {
        return jaxWsEndpoints;
    }

    public void setJaxWsEndpoints(List<Endpoint> jaxWsEndpoints) {
        this.jaxWsEndpoints = jaxWsEndpoints;
    }

    private void processBeansNodeWrite(Node node) {
        String nodeName = node.getNodeName();
        if (BEAN_TAG.equalsIgnoreCase(nodeName)) {

            addBean(node);

        } else if ((jaxws_prefix + ":endpoint").equals(nodeName) || ("endpoint".equals(nodeName) && isNamespace(node, NS_JAXWS))) {
            //definitely what we're looking for.
            scanEndpoints(node);

        }
    }

    private void scanJaxRsServer(Node node) {
        JaxRsEndpoint ep = new JaxRsEndpoint();

        String address = "UNKNOWN";
        Node addr = node.getAttributes().getNamedItem("address");
        if (addr != null) {
            address = addr.getTextContent().trim();
        }
        ep.setAddress(address);
        String id = "UNKNOWN";
        Node idn = node.getAttributes().getNamedItem("id");
        if (idn != null) {
            id = addr.getTextContent().trim();
        }
        ep.setId(id);
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            Node n = node.getChildNodes().item(i);
            String nodeName = n.getNodeName();
            //could be a bunch of stuff here, such as providers and service beans
            if ((jaxws_prefix + ":serviceBeans").equals(nodeName) || ("serviceBeans".equals(nodeName) && isNamespace(node, NS_JAXWS))) {
                addJaxRsServiceBeans(n, ep);
            }
        }
        jaxRsEndpoints.add(ep);
    }

    private void addJaxRsServiceBeans(Node n, JaxRsEndpoint baseAddress) {
        for (int i = 0; i < n.getChildNodes().getLength(); i++) {
            Node node = n.getChildNodes().item(i);
            if (node.getNodeName().endsWith("ref")) {

                Node bean = node.getAttributes().getNamedItem("bean");
                if (bean != null) {
                    String beanRef = bean.getTextContent().trim();
                    HandlerRef ref = new HandlerRef();
                    ref.setBean(beanRef);
                    baseAddress.getServiceBeanReference().add(ref);
                }
            } else if (node.getNodeName().endsWith("component-id")) {

            } else if (node.getNodeName().endsWith("bean")) {
                Node bean = node.getAttributes().getNamedItem("class");
                if (bean != null) {
                    Bean b = new Bean();
                    b.setClazz(bean.getTextContent().trim());
                    
                    baseAddress.getServiceBeans().add(b);
                }

            } else if (node.getNodeName().endsWith("serviceFactory")) {

            }
            //could be 'ref'
            //'component-id'
            //'bean'
            //'serviceFactory'
        }
    }

    public List<JaxRsEndpoint> getJaxRsEndpoints() {
        return jaxRsEndpoints;
    }

    public void setJaxRsEndpoints(List<JaxRsEndpoint> jaxRsEndpoints) {
        this.jaxRsEndpoints = jaxRsEndpoints;
    }
}
