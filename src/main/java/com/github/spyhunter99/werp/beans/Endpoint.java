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
public class Endpoint {

    private String id;
    private String implementor;
    private String address;
    private List<Handler> handlers = new ArrayList<Handler>();
    private List<InInterceptors> in = new ArrayList<InInterceptors>();
    private List<InFaultInteceptors> inf = new ArrayList<InFaultInteceptors>();
    private List<OutFaultInterceptors> out = new ArrayList<OutFaultInterceptors>();
    private List<OutInterceptors> outF = new ArrayList<OutInterceptors>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImplementor() {
        return implementor;
    }

    public void setImplementor(String implementor) {
        this.implementor = implementor;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Handler> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<Handler> handlers) {
        this.handlers = handlers;
    }
}
