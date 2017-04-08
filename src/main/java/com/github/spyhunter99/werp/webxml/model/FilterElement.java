/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.spyhunter99.werp.webxml.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author AO
 */
public class FilterElement {

    public FilterElement(){}
    public FilterElement(String name, String clazz){
        this.filterName=name;
        this.filterClass=clazz;
    }
    private String filterName;
    private String filterClass;
    private List<InitParam> params = new ArrayList<>();

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public String getFilterClass() {
        return filterClass;
    }

    public void setFilterClass(String filterClass) {
        this.filterClass = filterClass;
    }

    public List<InitParam> getParams() {
        return params;
    }

    public void setParams(List<InitParam> params) {
        this.params = params;
    }
}
