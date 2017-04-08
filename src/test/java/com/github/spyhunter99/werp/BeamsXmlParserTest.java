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
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Dad
 */
public class BeamsXmlParserTest {

    public BeamsXmlParserTest() {
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

    private void runTest(File input, int expectedJaxWsEndpoints, int beans) throws Exception {
        runTest(input, expectedJaxWsEndpoints, beans, 0);
        
    }
    
    private void runTest(File input, int expectedJaxWsEndpoints, int beans, int jaxrsEndpoints) throws Exception {
        BeamsXmlParser instance = new BeamsXmlParser();
        instance.parse(input);
        Assert.assertEquals(beans, instance.getBeans().size());
        Assert.assertEquals(expectedJaxWsEndpoints, instance.getJaxWsEndpoints().size());
        Assert.assertEquals(jaxrsEndpoints, instance.getJaxRsEndpoints().size());
        
    }

    /**
     * Test of parse method, of class BeamsXmlParser.
     */
    @Test
    public void testParse() throws Exception {
        System.out.println("parse");
        File input = new File("src/test/resources/fgsms/beans.xml");
        runTest(input, 5, 6,1);

    }

    @Test
    public void testParse2() throws Exception {
        System.out.println("parse2");
        File input = new File("src/test/resources/cxfbeans/nonrefbean.xml");
        runTest(input, 1, 0);

    }

    @Test
    public void testParse3() throws Exception {
        System.out.println("parse3");
        File input = new File("src/test/resources/cxfbeans/cxf-test.xml");
        runTest(input, 6, 12,7);

    }
    
     @Test
    public void testParse4() throws Exception {
        System.out.println("parse3");
        File input = new File("src/test/resources/cxfbeans/jaxrs1.xml");
        runTest(input, 0, 1,1);

    }
    @Ignore
     @Test
    public void testParse5() throws Exception {
        System.out.println("parse3");
        File input = new File("src/test/resources/cxfbeans/jaxrs2.xml");
        runTest(input, 6, 12);

    }
    @Ignore
     @Test
    public void testParse6() throws Exception {
        System.out.println("parse3");
        File input = new File("src/test/resources/cxfbeans/jaxwshandlers.xml");
        runTest(input, 6, 12);

    }

    
     @Test
    public void testParse7() throws Exception {
        System.out.println("parse3");
        File input = new File("src/test/resources/cxfbeans/juddi-beans.xml");
        //11 jaxws
        //1 jaxrs
        runTest(input, 11 , 3, 1);

    }
    
     @Test
    public void testParse8() throws Exception {
        System.out.println("parse3");
        File input = new File("src/test/resources/cxfbeans/logging.xml");
        runTest(input, 0, 3);

    }
    @Test
    public void testParse9() throws Exception {
        System.out.println("parse3");
        File input = new File("src/test/resources/cxfbeans/logging2.xml");
        runTest(input, 0, 2);

    }
}
