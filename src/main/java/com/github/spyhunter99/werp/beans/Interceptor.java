/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.spyhunter99.werp.beans;

/**
 *
 * @author AO
 */
public abstract class Interceptor {

    public Interceptor() {

    }

    public Interceptor(Bean inlineBean) {
        this.bean = inlineBean;
    }

    public Interceptor(HandlerRef beanReference) {
        this.beanRef = beanReference;
    }

    public HandlerRef getBeanRef() {
        return beanRef;
    }

    public void setBeanRef(HandlerRef beanRef) {
        this.beanRef = beanRef;
    }

    public Bean getBean() {
        return bean;
    }

    public void setBean(Bean bean) {
        this.bean = bean;
    }

    public enum Direction {
        IN, IN_FAULT, OUT, OUT_FAULT
    }

    public abstract Direction getDirection();
    protected HandlerRef beanRef;
    protected Bean bean;
}
