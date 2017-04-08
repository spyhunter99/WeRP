/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.spyhunter99.werp.beans;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author AO
 */
public class JaxRsEndpoint {

    private String address;
    private String id;
      private List<InInterceptors> in = new ArrayList<InInterceptors>();
    private List<InFaultInteceptors> inf = new ArrayList<InFaultInteceptors>();
    private List<OutFaultInterceptors> out = new ArrayList<OutFaultInterceptors>();
    private List<OutInterceptors> outF = new ArrayList<OutInterceptors>();
    private List<Bean> serviceBeans = new ArrayList<>();
    private List<HandlerRef> serviceBeanReference = new ArrayList<HandlerRef>();

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<InInterceptors> getIn() {
        return in;
    }

    public void setIn(List<InInterceptors> in) {
        this.in = in;
    }

    public List<InFaultInteceptors> getInf() {
        return inf;
    }

    public void setInf(List<InFaultInteceptors> inf) {
        this.inf = inf;
    }

    public List<OutFaultInterceptors> getOut() {
        return out;
    }

    public void setOut(List<OutFaultInterceptors> out) {
        this.out = out;
    }

    public List<OutInterceptors> getOutF() {
        return outF;
    }

    public void setOutF(List<OutInterceptors> outF) {
        this.outF = outF;
    }

    public List<Bean> getServiceBeans() {
        return serviceBeans;
    }

    public void setServiceBeans(List<Bean> serviceBeans) {
        this.serviceBeans = serviceBeans;
    }

    public List<HandlerRef> getServiceBeanReference() {
        return serviceBeanReference;
    }

    public void setServiceBeanReference(List<HandlerRef> serviceBeanReference) {
        this.serviceBeanReference = serviceBeanReference;
    }
}
