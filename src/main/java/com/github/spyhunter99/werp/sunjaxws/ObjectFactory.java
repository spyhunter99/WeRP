//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.04.07 at 04:13:10 PM EDT 
//


package com.github.spyhunter99.werp.sunjaxws;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.github.spyhunter99.werp.sunjaxws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.github.spyhunter99.werp.sunjaxws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Endpoints }
     * 
     */
    public Endpoints createEndpoints() {
        return new Endpoints();
    }

    /**
     * Create an instance of {@link EndpointType }
     * 
     */
    public EndpointType createEndpointType() {
        return new EndpointType();
    }

    /**
     * Create an instance of {@link PortComponentHandlerType }
     * 
     */
    public PortComponentHandlerType createPortComponentHandlerType() {
        return new PortComponentHandlerType();
    }

    /**
     * Create an instance of {@link HandlerChainType }
     * 
     */
    public HandlerChainType createHandlerChainType() {
        return new HandlerChainType();
    }

    /**
     * Create an instance of {@link ParamValueType }
     * 
     */
    public ParamValueType createParamValueType() {
        return new ParamValueType();
    }

}
