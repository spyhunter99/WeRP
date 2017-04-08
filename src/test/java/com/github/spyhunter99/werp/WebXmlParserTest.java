/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.spyhunter99.werp;

import com.github.spyhunter99.werp.webxml.model.FilterElement;
import com.github.spyhunter99.werp.webxml.model.FilterMapping;
import com.github.spyhunter99.werp.webxml.model.ServletElement;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Dad
 */
public class WebXmlParserTest {

    public WebXmlParserTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    private void runTest(File input) throws Exception {

        WebXmlParser instance = new WebXmlParser();
        instance.parse(input);
        int beforeS = instance.getServlets().size();
        int beforeFM = instance.getFilterMapping().size();
        int beforeF = instance.getFilters().size();

        instance.getFilters().add(new FilterElement("MyCoolFilter", "com.github.spyhunter99.AwesomeFilter"));
        instance.getFilterMapping().add(new FilterMapping("MyCoolFilter", "/*"));
        File output = new File("target/" + input.getName() + "-modified.xml");

        instance.write(output);
        instance = new WebXmlParser();
        instance.parse(output);
        Assert.assertEquals(beforeS, instance.getServlets().size());
        Assert.assertEquals(beforeFM + 1, instance.getFilterMapping().size());
        Assert.assertEquals(beforeF + 1, instance.getFilters().size());

        //ok now let's remove our additions, resave it, then compare with the original
        Iterator<FilterMapping> iterator = instance.getFilterMapping().iterator();
        while (iterator.hasNext()) {
            FilterMapping next = iterator.next();
            if (next.getFilterName().equals("MyCoolFilter")) {
                instance.getFilterMapping().remove(next);
                break;
            }
        }
        Iterator<FilterElement> iterator1 = instance.getFilters().iterator();
        while (iterator1.hasNext()) {
            FilterElement next = iterator1.next();
            if (next.getFilterClass().equals("com.github.spyhunter99.AwesomeFilter")) {
                instance.getFilters().remove(next);
                break;
            }
        }

        output = new File("target/" + input.getName() + "-undo.xml");
        instance.write(output);
        instance = new WebXmlParser();
        
        instance.parse(output);
        Assert.assertEquals(beforeS, instance.getServlets().size());
        Assert.assertEquals(beforeFM, instance.getFilterMapping().size());
        Assert.assertEquals(beforeF, instance.getFilters().size());

    }

    /**
     * Test of parse method, of class WebXmlParser.
     */
    @org.junit.Test
    public void testParseF() throws Exception {
        System.out.println("parseF");
        File input = new File("src/test/resources/fgsms/web.xml");
        runTest(input);

    }
    
     /**
     * Test of parse method, of class WebXmlParser.
     */
    @org.junit.Test
    public void testParseC() throws Exception {
        System.out.println("parseC");
        File input = new File("src/test/resources/complexWebXml.xml");
        runTest(input);

    }
    
      /**
     * Test of parse method, of class WebXmlParser.
     */
    @org.junit.Test
    public void testParse() throws Exception {
        System.out.println("parse1");
        File input = new File("src/test/resources/servlet23.xml");
        runTest(input);

    }   

    /**
     * Test of parse method, of class WebXmlParser.
     */
    @org.junit.Test
    public void testParse2() throws Exception {
        System.out.println("parse2");
        File input = new File("src/test/resources/juddi-svc-web.xml");
        runTest(input);
    }

    /**
     * Test of parse method, of class WebXmlParser.
     */
    @org.junit.Test
    public void testParse3() throws Exception {
        System.out.println("parse3");
        File input = new File("src/test/resources/juddi-ui-web.xml");
        runTest(input);
    }

    /**
     * Test of parse method, of class WebXmlParser.
     */
    @org.junit.Test
    public void testParse4() throws Exception {
        System.out.println("parse4");
        File input = new File("src/test/resources/withFilters.xml");
        runTest(input);

    }

    /**
     * Test of parse method, of class WebXmlParser.
     */
    @org.junit.Test
    public void testParse5() throws Exception {
        System.out.println("parse5");
        File input = new File("src/test/resources/withFilters2.xml");
        runTest(input);

    }

}
