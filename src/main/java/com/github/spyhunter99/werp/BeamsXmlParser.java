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
import com.github.spyhunter99.werp.beans.InFaultInteceptors;
import com.github.spyhunter99.werp.beans.InInterceptors;
import com.github.spyhunter99.werp.beans.Interceptor;
import com.github.spyhunter99.werp.beans.JaxRsEndpoint;
import com.github.spyhunter99.werp.beans.OutFaultInterceptors;
import com.github.spyhunter99.werp.beans.OutInterceptors;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Beans.xml isn't deterministic, therefore this can be used to programmatically
 * attach handlers to cxf beans
 *
 * several scenarios - services with or without handlers - global handlers
 * attached to the bus - handlers attached to each endpoint - jaxrs and jaxws
 *
 * min, return a list of endpoints that could have a handler attached to it. add
 * a handler to a specific endpoint add a global handler remove a global handler
 * remove a handler from a specific endpoint
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
    private String jaxws_prefix = "jaxws";
    private String jaxrs_prefix = "jaxrs";
    private String cxf_prefix = "cxf";
    private List<Interceptor> globalInterceptors = new ArrayList<>();

    //TODO global cxf:bus interceptors
    private List<Bean> beans = new ArrayList<Bean>();
    private List<Endpoint> jaxWsEndpoints = new ArrayList<Endpoint>();
    private List<JaxRsEndpoint> jaxRsEndpoints = new ArrayList<JaxRsEndpoint>();

    public void addBean(Bean bean) {
        //TODO check for duplicates?
        beans.add(bean);
    }

    public void removeBean(Bean bean) {
        for (int i = 0; i < beans.size(); i++) {
            if (beans.get(i).getClazz().equals(bean.getClazz())
                    && beans.get(i).getId().equals(bean.getId())) {
                beans.remove(i);
                break;
            }
        }
    }

    public void removeBean(String classname) {
        for (int i = 0; i < beans.size(); i++) {
            if (beans.get(i).getClazz().equals(classname)) {
                beans.remove(i);
                break;
            }
        }
    }

    public void addGlobalHandler(Interceptor handler) {
        if (!globalInterceptors.contains(handler)) {
            globalInterceptors.add(handler);
        }

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

    public void write(File output) throws Exception {
        long start = System.currentTimeMillis();
        applyChanges();

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
        } else if ((cxf_prefix + ":bus").equals(nodeName) || ("bus".equals(nodeName) && isNamespace(node, CXF_CORE))) {
            scanGlobalCxf(node);
        }
        //cxf:bus
    }

    private void addBean(Node node) {
        Bean b = new Bean();
        for (int i = 0; i < node.getAttributes().getLength(); i++) {
            Node item = node.getAttributes().item(i);
            if (ID_TAG.equals(item.getNodeName())) {
                b.setId(item.getTextContent().trim());
            }
            if (CLASS_TAG.equals(item.getNodeName())) {
                b.setClazz(item.getTextContent().trim());
            }
        }
        beans.add(b);
    }

    private void scanEndpoints(Node node) {
        Endpoint ep = new Endpoint();
        for (int i = 0; i < node.getAttributes().getLength(); i++) {
            Node item = node.getAttributes().item(i);
            if (ID_TAG.equals(item.getNodeName())) {
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
            if (REF.equals(node.getNodeName()) && isNamespace(node, NS_JAXWS)) {
                Handler h = new Handler();

                HandlerRef ref = new HandlerRef();
                ref.setBean(node.getAttributes().getNamedItem(BEAN_TAG).getTextContent().trim());
                h.setReferences(ref);
                ep.getHandlers().add(h);
            } else if (BEAN_TAG.equals(node.getNodeName()) && isNamespace(node, NS_JAXWS)) {
                Handler h = new Handler();
                h.setBean(new Bean());
                Node attr;
                attr = node.getAttributes().getNamedItem(ID_TAG);
                if (attr != null) {
                    h.getBean().setId(attr.getTextContent());
                }
                attr = node.getAttributes().getNamedItem(CLASS_TAG);
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

    private void scanJaxRsServer(Node node) {
        JaxRsEndpoint ep = new JaxRsEndpoint();

        String address = "UNKNOWN";
        Node addr = node.getAttributes().getNamedItem("address");
        if (addr != null) {
            address = addr.getTextContent().trim();
        }
        ep.setAddress(address);
        String id = "UNKNOWN";
        Node idn = node.getAttributes().getNamedItem(ID_TAG);
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
            if (node.getNodeName().endsWith(REF)) {

                Node bean = node.getAttributes().getNamedItem(BEAN_TAG);
                if (bean != null) {
                    String beanRef = bean.getTextContent().trim();
                    HandlerRef ref = new HandlerRef();
                    ref.setBean(beanRef);
                    baseAddress.getServiceBeanReference().add(ref);
                }
            } else if (node.getNodeName().endsWith("component-id")) {

            } else if (node.getNodeName().endsWith(BEAN_TAG)) {
                Node bean = node.getAttributes().getNamedItem(CLASS_TAG);
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

    private void scanGlobalCxf(Node n) {
        for (int i = 0; i < n.getChildNodes().getLength(); i++) {
            Node node = n.getChildNodes().item(i);
            String nodeName = node.getNodeName();
            
            if ((cxf_prefix + ":inInterceptors").equals(nodeName) || ("inInterceptors".equals(nodeName) && isNamespace(node, CXF_CORE))) {
                globalInterceptors.addAll(addGlobalInterceptor(Interceptor.Direction.IN, node));

            } else if ((cxf_prefix + ":outInterceptors").equals(nodeName) || ("outInterceptors".equals(nodeName) && isNamespace(node, CXF_CORE))) {
                globalInterceptors.addAll(addGlobalInterceptor(Interceptor.Direction.OUT, node));

            } else if ((cxf_prefix + ":outFaultInterceptors").equals(nodeName) || ("outFaultInterceptors".equals(nodeName) && isNamespace(node, CXF_CORE))) {
                globalInterceptors.addAll (addGlobalInterceptor(Interceptor.Direction.OUT_FAULT, node));

            } else if ((cxf_prefix + ":inFaultInterceptors").equals(nodeName) || ("inFaultInterceptors".equals(nodeName) && isNamespace(node, CXF_CORE))) {
                globalInterceptors.addAll (addGlobalInterceptor(Interceptor.Direction.IN_FAULT, node));
            }
            
            //could be 'ref'
            //'component-id'
            //'bean'
            //'serviceFactory'
        }
    }

    private List<Interceptor> addGlobalInterceptor(Interceptor.Direction direction, Node n) {
        List<Interceptor> r = new ArrayList<>();
        Interceptor input = null;
        switch (direction) {
            case IN:
                input = new InInterceptors();
                break;
            case IN_FAULT:
                input = new InFaultInteceptors();
                break;
            case OUT:
                input = new OutInterceptors();
                break;
            case OUT_FAULT:
                input = new OutFaultInterceptors();
                break;
        }
        for (int i = 0; i < n.getChildNodes().getLength(); i++) {
            Node node = n.getChildNodes().item(i);
            if (node.getNodeName().endsWith(REF)) {

                Node bean = node.getAttributes().getNamedItem(BEAN_TAG);
                if (bean != null) {
                    String beanRef = bean.getTextContent().trim();
                    HandlerRef ref = new HandlerRef();
                    ref.setBean(beanRef);
                    input.setBeanRef(ref);
                    r.add (input);
                }
            } else if (node.getNodeName().endsWith(BEAN_TAG)) {
                Node bean = node.getAttributes().getNamedItem(CLASS_TAG);
                if (bean != null) {
                    Bean b = new Bean();
                    b.setClazz(bean.getTextContent().trim());

                    input.setBean(b);
                    r.add(input);
                }

            }
        }
        return r;
    }
    private static final String REF = "ref";

    public List<Interceptor> getGlobalInterceptors() {
        return globalInterceptors;
    }

    private void applyChanges() {

        doBeans();
        doGlobalHandlers();
    }

    private void doBeans() {

        List<Bean> currentList = new ArrayList<Bean>();
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            //non-destructive merge
            if (BEAN_TAG.equalsIgnoreCase(node.getNodeName())) {
                Bean b = new Bean();
                for (int k = 0; k < node.getAttributes().getLength(); k++) {
                    Node item = node.getAttributes().item(k);
                    if (ID_TAG.equals(item.getNodeName())) {
                        b.setId(item.getTextContent().trim());
                    }
                    if (CLASS_TAG.equals(item.getNodeName())) {
                        b.setClazz(item.getTextContent().trim());
                    }
                }
                currentList.add(b);
            }
        }

        //ok we have everything in the original document
        for (int k = 0; k < currentList.size(); k++) {
            //is it still in the caller's desired collection
            boolean keep = false;
            for (int j = 0; j < beans.size(); j++) {
                if (beans.get(j).getClazz() != null && beans.get(j).getClazz().equalsIgnoreCase(currentList.get(k).getClazz())) {
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
                    if (BEAN_TAG.equalsIgnoreCase(node.getNodeName())) {

                        Bean b = new Bean();
                        for (int jj = 0; jj < node.getAttributes().getLength(); jj++) {
                            Node item = node.getAttributes().item(jj);
                            if (ID_TAG.equals(item.getNodeName())) {
                                b.setId(item.getTextContent().trim());
                            }
                            if (CLASS_TAG.equals(item.getNodeName())) {
                                b.setClazz(item.getTextContent().trim());
                            }
                        }

                        if (b.getClazz() != null && b.getClazz().equalsIgnoreCase(currentList.get(k).getClazz())) {
                            document.getDocumentElement().removeChild(node);
                            break;
                        }

                    }
                }
            }

        }

        for (int j = 0; j < beans.size(); j++) {
            boolean add = true;
            for (int k = 0; k < currentList.size(); k++) {
                if (beans.get(j).getClazz() == null) {
                    add = false;
                    break;
                }
                if (beans.get(j).getClazz().equalsIgnoreCase(currentList.get(k).getClazz())) {
                    add = false;
                    break;
                }
            }

            if (add) {
                //ok the caller has added something new that wasn't there before, add it
                Node newBean = document.createElement(BEAN_TAG);

                ((Element) newBean).setAttribute(CLASS_TAG, beans.get(j).getClazz());
                ((Element) newBean).setAttribute(ID_TAG, beans.get(j).getId());

                //filter, insert before the first filter 
                boolean added = false;
                nodeList = document.getDocumentElement().getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);

                    if (BEAN_TAG.equalsIgnoreCase(node.getNodeName())) {
                        document.getDocumentElement().insertBefore(newBean, node);
                        added = true;
                        break;
                    } else if ((jaxws_prefix + ":endpoint").equalsIgnoreCase(node.getNodeName())) {
                        document.getDocumentElement().insertBefore(newBean, node);
                        added = true;
                        break;
                    } else if ((jaxrs_prefix + ":server").equalsIgnoreCase(node.getNodeName())) {
                        document.getDocumentElement().insertBefore(newBean, node);
                        added = true;
                        break;
                    } else if ((cxf_prefix + ":bus").equalsIgnoreCase(node.getNodeName())) {
                        document.getDocumentElement().insertBefore(newBean, node);
                        added = true;
                        break;
                    }
                }

                if (!added) {
                    document.getDocumentElement().appendChild(newBean);
                }

            }

        }
    }
    private static final String ID_TAG = "id";
    private static final String CLASS_TAG = "class";

    private void doGlobalHandlers() {
        //TODO

        List<Interceptor> currentList = new ArrayList<Interceptor>();
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        Node bus = null;
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            String nodeName = node.getNodeName();
            if (nodeName.equals(cxf_prefix + ":bus") || ("bus".equals(nodeName) && isNamespace(node, CXF_CORE))) {
                bus = node;
                for (int k = 0; k < node.getChildNodes().getLength(); k++) {
                    String nodeName2 = node.getChildNodes().item(k).getNodeName();
                    
                    if ((cxf_prefix + ":inInterceptors").equals(nodeName2) || ("inInterceptors".equals(nodeName2) && isNamespace(node, CXF_CORE))) {
                        currentList.addAll(addGlobalInterceptor(Interceptor.Direction.IN, node.getChildNodes().item(k)));

                    } else if ((cxf_prefix + ":outInterceptors").equals(nodeName2) || ("outInterceptors".equals(nodeName2) && isNamespace(node, CXF_CORE))) {
                        currentList.addAll (addGlobalInterceptor(Interceptor.Direction.OUT, node.getChildNodes().item(k)));

                    } else if ((cxf_prefix + ":outFaultInterceptors").equals(nodeName2) || ("outFaultInterceptors".equals(nodeName2) && isNamespace(node, CXF_CORE))) {
                        currentList.addAll (addGlobalInterceptor(Interceptor.Direction.OUT_FAULT, node.getChildNodes().item(k)));

                    } else if ((cxf_prefix + ":inFaultInterceptors").equals(nodeName2) || ("inFaultInterceptors".equals(nodeName2) && isNamespace(node, CXF_CORE))) {
                        currentList.addAll(addGlobalInterceptor(Interceptor.Direction.IN_FAULT, node.getChildNodes().item(k)));
                    }
                    
                }
                break;  //assuming only one bus definition
            }

        }

        //ok we have everything in the original document
        for (int k = 0; k < currentList.size(); k++) {
            //is it still in the caller's desired collection
            boolean keep = false;
            for (int j = 0; j < globalInterceptors.size(); j++) {
                if (globalInterceptors.get(j).getBean() != null && currentList.get(k).getBean() != null) {
                    if (globalInterceptors.get(j).getBean().getClazz().equals(currentList.get(k).getBean().getClazz())) {
                        keep = true;
                        break;
                    }
                } else if (globalInterceptors.get(j).getBeanRef() != null && currentList.get(k).getBeanRef() != null) {
                    if (globalInterceptors.get(j).getBeanRef().getBean().equals(currentList.get(k).getBeanRef().getBean())) {
                        keep = true;
                        break;
                    }
                }

            }
            if (!keep && bus != null) {
                //remove the item from the xml doc

                String expectedTag = "";
                switch (currentList.get(k).getDirection()) {
                    case IN:
                        expectedTag = cxf_prefix + ":inInterceptors";
                        break;
                    case IN_FAULT:
                        expectedTag = cxf_prefix + ":inFaultInterceptors";
                        break;
                    case OUT:
                        expectedTag = cxf_prefix + ":outFaultInterceptors";
                        break;
                    case OUT_FAULT:
                        expectedTag = cxf_prefix + ":outFaultInterceptors";
                        break;
                }
                nodeList = bus.getChildNodes();
                for (int mm = 0; mm < nodeList.getLength(); mm++) {
                    Node node = nodeList.item(mm);

                    if (expectedTag.equalsIgnoreCase(node.getNodeName())) {

                        Interceptor addGlobalInterceptor = addGlobalInterceptor(currentList.get(k).getDirection(), node).get(0);

                        if (addGlobalInterceptor.getBean() != null && currentList.get(k).getBean() != null) {
                            if (addGlobalInterceptor.getBean().getClazz().equals(currentList.get(k).getBean().getClazz())) {
                                bus.removeChild(node);
                                break;
                            }
                        } else if (addGlobalInterceptor.getBeanRef() != null && currentList.get(k).getBeanRef() != null) {
                            if (addGlobalInterceptor.getBeanRef().getBean().equals(currentList.get(k).getBeanRef().getBean())) {
                                bus.removeChild(node);
                                break;
                            }
                        }

                    }
                }
            }

        }

        if (bus == null) {
            bus = document.createElementNS(CXF_CORE, "bus");
            bus.setPrefix(cxf_prefix);
            document.getDocumentElement().appendChild(bus);

            Element e = document.createElementNS(CXF_CORE, "inInterceptors");
            e.setPrefix(cxf_prefix);
            bus.appendChild(e);
            e = document.createElementNS(CXF_CORE, "outInterceptors");
            e.setPrefix(cxf_prefix);
            bus.appendChild(e);
            e = document.createElementNS(CXF_CORE, "outFaultInterceptors");
            e.setPrefix(cxf_prefix);
            bus.appendChild(e);
            e = document.createElementNS(CXF_CORE, "inFaultInterceptors");
            e.setPrefix(cxf_prefix);
            bus.appendChild(e);
        }

        //find all the new stuff to add
        for (int j = 0; j < globalInterceptors.size(); j++) {
            boolean add = true;
            nodeList = bus.getChildNodes();

            for (int m = 0; m < nodeList.getLength(); m++) {
                Node node = nodeList.item(m);
                String nodeName = nodeList.item(m).getNodeName();
                if ((cxf_prefix + ":inInterceptors").equals(nodeName) || ("inInterceptors".equals(nodeName) && isNamespace(node, CXF_CORE))) {
                    List<Interceptor> listaddGlobalInterceptor = addGlobalInterceptor(Interceptor.Direction.IN, node);
                    Interceptor addGlobalInterceptor = listaddGlobalInterceptor.isEmpty() ? null : listaddGlobalInterceptor.get(0);
                    if (addGlobalInterceptor != null) {
                        if (addGlobalInterceptor.getBean() != null && globalInterceptors.get(j).getBean() != null) {
                            if (addGlobalInterceptor.getBean().getClazz().equals(globalInterceptors.get(j).getBean().getClazz())) {
                                add = false;
                                break;
                            }
                        } else if (addGlobalInterceptor.getBeanRef() != null && globalInterceptors.get(j).getBeanRef() != null) {
                            if (addGlobalInterceptor.getBeanRef().getBean().equals(globalInterceptors.get(j).getBeanRef().getBean())) {
                                add = false;
                                break;
                            }
                        }
                    }

                } else if ((cxf_prefix + ":outFaultInterceptors").equals(nodeName) || ("outFaultInterceptors".equals(nodeName) && isNamespace(node, CXF_CORE))) {
                    List<Interceptor> listaddGlobalInterceptor = addGlobalInterceptor(Interceptor.Direction.OUT_FAULT, node);
                    Interceptor addGlobalInterceptor = listaddGlobalInterceptor.isEmpty() ? null : listaddGlobalInterceptor.get(0);
                    if (addGlobalInterceptor != null) {
                        if (addGlobalInterceptor.getBean() != null && globalInterceptors.get(j).getBean() != null) {
                            if (addGlobalInterceptor.getBean().getClazz().equals(globalInterceptors.get(j).getBean().getClazz())) {
                                add = false;
                                break;
                            }
                        } else if (addGlobalInterceptor.getBeanRef() != null && globalInterceptors.get(j).getBeanRef() != null) {
                            if (addGlobalInterceptor.getBeanRef().getBean().equals(globalInterceptors.get(j).getBeanRef().getBean())) {
                                add = false;
                                break;
                            }
                        }
                    }
                } else if ((cxf_prefix + ":outInterceptors").equals(nodeName) || ("outInterceptors".equals(nodeName) && isNamespace(node, CXF_CORE))) {
                    List<Interceptor> listaddGlobalInterceptor = addGlobalInterceptor(Interceptor.Direction.OUT, node);
                    Interceptor addGlobalInterceptor = listaddGlobalInterceptor.isEmpty() ? null : listaddGlobalInterceptor.get(0);
                    if (addGlobalInterceptor != null) {
                        if (addGlobalInterceptor.getBean() != null && globalInterceptors.get(j).getBean() != null) {
                            if (addGlobalInterceptor.getBean().getClazz().equals(globalInterceptors.get(j).getBean().getClazz())) {
                                add = false;
                                break;
                            }
                        } else if (addGlobalInterceptor.getBeanRef() != null && globalInterceptors.get(j).getBeanRef() != null) {
                            if (addGlobalInterceptor.getBeanRef().getBean().equals(globalInterceptors.get(j).getBeanRef().getBean())) {
                                add = false;
                                break;
                            }
                        }
                    }

                } else if ((cxf_prefix + ":inFaultInterceptors").equals(nodeName) || ("inFaultInterceptors".equals(nodeName) && isNamespace(node, CXF_CORE))) {
                    List<Interceptor> listaddGlobalInterceptor = addGlobalInterceptor(Interceptor.Direction.IN_FAULT, node);
                    Interceptor addGlobalInterceptor = listaddGlobalInterceptor.isEmpty() ? null : listaddGlobalInterceptor.get(0);
                    if (addGlobalInterceptor != null) {
                        if (addGlobalInterceptor.getBean() != null && globalInterceptors.get(j).getBean() != null) {
                            if (addGlobalInterceptor.getBean().getClazz().equals(globalInterceptors.get(j).getBean().getClazz())) {
                                add = false;
                                break;
                            }
                        } else if (addGlobalInterceptor.getBeanRef() != null && globalInterceptors.get(j).getBeanRef() != null) {
                            if (addGlobalInterceptor.getBeanRef().getBean().equals(globalInterceptors.get(j).getBeanRef().getBean())) {
                                add = false;
                                break;
                            }
                        }
                    }
                }

            }
            if (add) {
                //ok the caller has added something new that wasn't there before, add it
                
                Element bean=null;

                if (globalInterceptors.get(j).getBean() != null) {
                    //inline bean
                    bean = document.createElement("bean");
                    bean.setAttribute("class", globalInterceptors.get(j).getBean().getClazz());
                   
                }
                if (globalInterceptors.get(j).getBeanRef() != null) {
                    //reference to another bean
                    bean = document.createElement("ref");
                    bean.setAttribute("bean", globalInterceptors.get(j).getBeanRef().getBean());
                  
                }

                //filter, insert before the first filter 
                boolean added = false;
                nodeList = bus.getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);

                    String nodeName = node.getNodeName();

                    if (globalInterceptors.get(j).getDirection() == Interceptor.Direction.IN && (cxf_prefix + ":inInterceptors").equals(nodeName) || ("inInterceptors".equals(nodeName) && isNamespace(node, CXF_CORE))) {
                        node.appendChild(bean);
                        added = true;
                        break;

                    } else if (globalInterceptors.get(j).getDirection() == Interceptor.Direction.OUT && (cxf_prefix + ":outInterceptors").equals(nodeName) || ("outInterceptors".equals(nodeName) && isNamespace(node, CXF_CORE))) {
                        node.appendChild(bean);
                        added = true;
                        break;

                    } else if (globalInterceptors.get(j).getDirection() == Interceptor.Direction.OUT_FAULT && (cxf_prefix + ":outFaultInterceptors").equals(nodeName) || ("outFaultInterceptors".equals(nodeName) && isNamespace(node, CXF_CORE))) {
                        node.appendChild(bean);
                        added = true;
                        break;

                    } else if (globalInterceptors.get(j).getDirection() == Interceptor.Direction.IN_FAULT && (cxf_prefix + ":inFaultInterceptors").equals(nodeName) || ("inFaultInterceptors".equals(nodeName) && isNamespace(node, CXF_CORE))) {
                        node.appendChild(bean);
                        added = true;
                        break;
                    }
                }

            }

        }
    }
}
