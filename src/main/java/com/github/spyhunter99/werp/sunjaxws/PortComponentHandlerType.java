//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.04.07 at 04:13:10 PM EDT 
//


package com.github.spyhunter99.werp.sunjaxws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for port-component_handlerType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="port-component_handlerType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="handler-name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="handler-class" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="init-param" type="{http://java.sun.com/xml/ns/jax-ws/ri/runtime}param-valueType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="soap-role" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "port-component_handlerType", propOrder = {
    "handlerName",
    "handlerClass",
    "initParam",
    "soapRole"
})
public class PortComponentHandlerType {

    @XmlElement(name = "handler-name", required = true)
    protected String handlerName;
    @XmlElement(name = "handler-class", required = true)
    protected String handlerClass;
    @XmlElement(name = "init-param")
    protected List<ParamValueType> initParam;
    @XmlElement(name = "soap-role")
    protected List<String> soapRole;

    /**
     * Gets the value of the handlerName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHandlerName() {
        return handlerName;
    }

    /**
     * Sets the value of the handlerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHandlerName(String value) {
        this.handlerName = value;
    }

    /**
     * Gets the value of the handlerClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHandlerClass() {
        return handlerClass;
    }

    /**
     * Sets the value of the handlerClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHandlerClass(String value) {
        this.handlerClass = value;
    }

    /**
     * Gets the value of the initParam property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the initParam property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInitParam().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ParamValueType }
     * 
     * 
     */
    public List<ParamValueType> getInitParam() {
        if (initParam == null) {
            initParam = new ArrayList<ParamValueType>();
        }
        return this.initParam;
    }

    /**
     * Gets the value of the soapRole property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the soapRole property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSoapRole().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSoapRole() {
        if (soapRole == null) {
            soapRole = new ArrayList<String>();
        }
        return this.soapRole;
    }

}
