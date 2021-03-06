/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.spyhunter99.werp;

import com.github.spyhunter99.werp.beans.Bean;
import com.github.spyhunter99.werp.beans.HandlerRef;
import com.github.spyhunter99.werp.beans.InInterceptors;
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
 * @author AO
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
        runTest(input, expectedJaxWsEndpoints, beans, 0,0);
        
    }
    
    private void runTest(File input, int expectedJaxWsEndpoints, int beans, int jaxrsEndpoints, int globalHandlers) throws Exception {
        BeamsXmlParser instance = new BeamsXmlParser();
        instance.parse(input);
        Assert.assertEquals(beans, instance.getBeans().size());
        Assert.assertEquals(expectedJaxWsEndpoints, instance.getJaxWsEndpoints().size());
        Assert.assertEquals(jaxrsEndpoints, instance.getJaxRsEndpoints().size());
        Assert.assertEquals(globalHandlers, instance.getGlobalInterceptors().size());
        
        
        //add remove bean
        File temp = new File("target/" + input.getName() + "-modified.xml");
        instance.write(temp);
        instance = new BeamsXmlParser();
        instance.parse(temp);
        Assert.assertEquals(beans, instance.getBeans().size());
        Assert.assertEquals(expectedJaxWsEndpoints, instance.getJaxWsEndpoints().size());
        Assert.assertEquals(jaxrsEndpoints, instance.getJaxRsEndpoints().size());
        Assert.assertEquals(globalHandlers, instance.getGlobalInterceptors().size());
        
        Bean newBean = new Bean();
        newBean.setClazz("com.github.spyhunter99.CustomBean");
        newBean.setId("awesomeBean");
        instance.addBean(newBean);
        instance.write(temp);

        instance = new BeamsXmlParser();
        instance.parse(temp);
        Assert.assertEquals(beans+1, instance.getBeans().size());
        Assert.assertEquals(expectedJaxWsEndpoints, instance.getJaxWsEndpoints().size());
        Assert.assertEquals(jaxrsEndpoints, instance.getJaxRsEndpoints().size());
        Assert.assertEquals(globalHandlers, instance.getGlobalInterceptors().size());
        instance.removeBean(newBean);
        temp = new File("target/" + input.getName() + "-undo.xml");
        instance.write(temp);
        
        instance = new BeamsXmlParser();
        instance.parse(temp);
        Assert.assertEquals(beans, instance.getBeans().size());
        Assert.assertEquals(expectedJaxWsEndpoints, instance.getJaxWsEndpoints().size());
        Assert.assertEquals(jaxrsEndpoints, instance.getJaxRsEndpoints().size());
        Assert.assertEquals(globalHandlers, instance.getGlobalInterceptors().size());
        instance.addBean(newBean);
        
        HandlerRef ref = new HandlerRef();
        ref.setBean(newBean.getId());
        
        //add remove global handler with bean reference
        instance.addGlobalHandler(new InInterceptors(ref));
        temp = new File("target/" + input.getName() + "-modified2.xml");
        instance.write(temp);
        instance = new BeamsXmlParser();
        instance.parse(temp);
        Assert.assertEquals(beans+1, instance.getBeans().size());
        Assert.assertEquals(expectedJaxWsEndpoints, instance.getJaxWsEndpoints().size());
        Assert.assertEquals(jaxrsEndpoints, instance.getJaxRsEndpoints().size());
        Assert.assertEquals(globalHandlers+1, instance.getGlobalInterceptors().size());
        
    }

    /**
     * Test of parse method, of class BeamsXmlParser.
     */
    @Test
    public void testParse() throws Exception {
        System.out.println("parse");
        File input = new File("src/test/resources/fgsms/beans.xml");
        runTest(input, 5, 6,1,0);

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
        runTest(input, 6, 12,7,0);

    }
    
     @Test
    public void testParse4() throws Exception {
        System.out.println("parse3");
        File input = new File("src/test/resources/cxfbeans/jaxrs1.xml");
        runTest(input, 0, 1,1,0);

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
        runTest(input, 11 , 3, 1,0);

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
        runTest(input, 0, 2,0,4);

    }
    
     @Test
    public void testParse10() throws Exception {
        System.out.println("parse3");
        File input = new File("src/test/resources/cxfbeans/empty.xml");
        runTest(input, 0, 0,0,0);

    }
    
}
