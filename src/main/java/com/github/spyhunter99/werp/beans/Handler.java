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
public class Handler {

    private HandlerRef references;
    private Bean bean;

    public HandlerRef getReferences() {
        return references;
    }

    public void setReferences(HandlerRef references) {
        this.references = references;
    }

    public Bean getBean() {
        return bean;
    }

    public void setBean(Bean bean) {
        this.bean = bean;
    }
}
