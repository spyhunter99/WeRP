/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.spyhunter99.werp;

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

    /**
     * Test of parse method, of class WebXmlParser.
     */
    @org.junit.Test
    public void testParse() throws Exception {
        System.out.println("parse");
        File input = new File("src/test/resources/servlet23.xml");
        WebXmlParser instance = new WebXmlParser();
        instance.parse(input);

    }

    /**
     * Test of parse method, of class WebXmlParser.
     */
    @org.junit.Test
    public void testParse2() throws Exception {
        System.out.println("parse");
        File input = new File("src/test/resources/juddi-svc-web.xml");
        WebXmlParser instance = new WebXmlParser();
        List<ServletElement> parse = instance.parse(input);
        Assert.assertEquals(3, parse.size());

    }

    /**
     * Test of parse method, of class WebXmlParser.
     */
    @org.junit.Test
    public void testParse3() throws Exception {
        System.out.println("parse");
        File input = new File("src/test/resources/juddi-ui-web.xml");
        WebXmlParser instance = new WebXmlParser();
        List<ServletElement> parse = instance.parse(input);
        Assert.assertEquals(0, parse.size());

    }

}
