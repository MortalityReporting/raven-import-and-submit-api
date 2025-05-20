package edu.gatech.chai.Mapping.Util;

import java.math.BigDecimal;

import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Quantity.QuantityComparator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class MappingUtilTest extends TestCase{
    
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MappingUtilTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( MappingUtilTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testParseQuantity0()
    {
        String input = "<2.5 ng/mL";
        Quantity returnQuantity = LocalModelToFhirCMSUtil.parseQuantity(input);
        assertEquals(returnQuantity.getComparator(), QuantityComparator.LESS_THAN);
        assertEquals(returnQuantity.getValue().compareTo(new BigDecimal(2.5)), 0);
        assertTrue(returnQuantity.getUnit().equalsIgnoreCase("ng/mL"));
    }

    public void testParseQuantity1()
    {
        String input = "0.145 g/dL";
        Quantity returnQuantity = LocalModelToFhirCMSUtil.parseQuantity(input);
        assertEquals(returnQuantity.getComparator(), null);
        System.out.println(returnQuantity.getValue());
        //assertEquals(returnQuantity.getValue().compareTo(new BigDecimal(0.145)), 0);
        assertTrue(returnQuantity.getUnit().equalsIgnoreCase("g/dL"));
    }

    public void testParseQuantity2()
    {
        String input = "23 ng/mL";
        Quantity returnQuantity = LocalModelToFhirCMSUtil.parseQuantity(input);
        assertEquals(returnQuantity.getComparator(), null);
        assertEquals(returnQuantity.getValue().compareTo(new BigDecimal(23)), 0);
        assertTrue(returnQuantity.getUnit().equalsIgnoreCase("ng/mL"));
    }

    public void testParseQuantity3()
    {
        String input = "1.0 %(g/dL)";
        Quantity returnQuantity = LocalModelToFhirCMSUtil.parseQuantity(input);
        assertEquals(returnQuantity.getComparator(), null);
        assertEquals(returnQuantity.getValue().compareTo(new BigDecimal(1)), 0);
        assertTrue(returnQuantity.getUnit().equalsIgnoreCase("%(g/dL)"));
    }
}