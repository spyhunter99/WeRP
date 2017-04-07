/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.spyhunter99.werp;

import com.github.spyhunter99.werp.model.FilterElement;
import com.github.spyhunter99.werp.model.FilterMapping;
import com.github.spyhunter99.werp.model.ServletElement;
import java.io.File;
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
