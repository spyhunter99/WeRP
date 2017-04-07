/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.spyhunter99.werp.model;

/**
 *
 * @author AO
 */
public class FilterMapping {

    public FilterMapping(){}
    public FilterMapping(String name, String urlpattern){
        this.filterName=name;
        this.urlPattern=urlpattern;
    }
    private String filterName;
    private String urlPattern;
    private String servletName;

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public String getServletName() {
        return servletName;
    }

    public void setServletName(String servletName) {
        this.servletName = servletName;
    }
}
