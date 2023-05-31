package org.example.search.builder;

import junit.framework.TestCase;
import org.example.search.SimpleZillowSearchCriteriaFactory;
import org.junit.Assert;

public class ZillowSearchCriteriaCriteriaTest extends TestCase {

    public void testFirstPage() {
        String url = SimpleZillowSearchCriteriaFactory
                .builder()
                .stateAbbreviation("sc")
                .minimumPrice(100000)
                .maximumPrice(250000)
                .minimumBaths(2)
                .minimumBedrooms(3)
                .minimumSquareFootage(1000)
                .pageNumber(1)
                .build()
                .generateZillowSearchCriteria()
                .getUrl();
        System.out.println(url);
        Assert.assertNotNull(url);
    }

    public void testTenthPage() {
        String url = SimpleZillowSearchCriteriaFactory
                .builder()
                .stateAbbreviation("sc")
                .minimumPrice(100000)
                .maximumPrice(250000)
                .minimumBaths(2)
                .minimumBedrooms(3)
                .minimumSquareFootage(1000)
                .pageNumber(15)
                .build()
                .generateZillowSearchCriteria()
                .getUrl();
        System.out.println(url);
        Assert.assertNotNull(url);
    }
}