/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.spyhunter99.werp;

import com.github.spyhunter99.werp.sunjaxws.Endpoints;
import java.io.File;
import javax.xml.bind.JAXB;

/**
 *
 * @author AO
 */
public class SunJaxwsXmlParser {
    private Endpoints doc=null;
    public void parse(File input) throws Exception {
        doc = JAXB.unmarshal(input, Endpoints.class);
        
        
    }

    public Endpoints getDoc() {
        return doc;
    }

    public void setDoc(Endpoints doc) {
        this.doc = doc;
    }
}
