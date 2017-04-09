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
      private List<Interceptor> interceptors = new ArrayList<Interceptor>();
    
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

    public List<Interceptor> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<Interceptor> interceptors) {
        this.interceptors = interceptors;
    }
}
