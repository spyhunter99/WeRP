/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.spyhunter99.werp;

import java.io.File;
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
public class SunJaxwsXmlParserTest {
    
    public SunJaxwsXmlParserTest() {
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
     * Test of parse method, of class SunJaxwsXmlParser.
     */
    @Test
    public void testParse() throws Exception {
        System.out.println("parse");
        File input = new File("src/test/resources/fgsms/sun-jaxws.xml");
        SunJaxwsXmlParser instance = new SunJaxwsXmlParser();
        instance.parse(input);
        Assert.assertEquals(5, instance.getDoc().getEndpoint().size());
        //register handlers this way instance.getDoc().getEndpoint().get(0).getHandlerChain().getHandler().get(0).getHandlerClass()
       
    }

    
}
