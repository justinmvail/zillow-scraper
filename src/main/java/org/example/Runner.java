package org.example;

import org.example.search.*;
import org.example.search.builder.ZillowSearchCriteria;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Runner {
    public static void main( String[] args ) {
        long startTime = System.nanoTime();

        ZillowSearchCriteria searchCriteria = SimpleZillowSearchCriteriaFactory.builder()
                .stateAbbreviation("sc")
                .minimumPrice(1)
                .maximumPrice(25_000_000)
                .minimumBaths(1)
                .minimumBedrooms(1)
                .minimumSquareFootage(100)
                .pageNumber(1)
                .build()
                .generateZillowSearchCriteria();

        ZillowSearchAggregate zillowSearchAggregate = ZillowListingSearcher.builder()
                .LISTINGS_PER_PAGE(40)
                .MAX_PAGES(12)
                .SCROLL_AMOUNT_PIXELS(1000)
                .ELEMENT_TIMEOUT(10)
                .build()
                .getZillowSearchAggregate(searchCriteria);

        printLinksAndAddresses(zillowSearchAggregate);
        verifyResults(zillowSearchAggregate);

        long endTime = System.nanoTime();
        System.out.println();
        long totalSeconds = (endTime-startTime)/1_000_000_000;
        System.out.println("Run Duration: " + totalSeconds/60 + " minutes " + totalSeconds%60 + " seconds");

    }

    public static void verifyResults(ZillowSearchAggregate results){
        System.out.println();
        System.out.println("Expected Number of Links: "+results.getExpectedNumberOfListings());
        System.out.println("Actual Number of Links: "+results.getActualNumberOfListings());
        System.out.println("Actual Number of non duplicated Links : "+results.getActualNumberOfNonDuplicatedListings());
        results.getExpectedNumberOfListings();
        results.getActualNumberOfListings();
    }

    public static void printLinksAndAddresses(ZillowSearchAggregate aggregate){
        Set<String> listingLinks = aggregate.getAllListingLinks();
        listingLinks.forEach(System.out::println);
        List<String> addresses = listingLinks.stream()
                .map(Runner::extractAddressFromURL)
                .filter(Objects::nonNull)
                .toList();
        addresses.forEach(System.out::println);
    }

    public static String extractAddressFromURL(String urlString) {
        String pattern = "/homedetails/([^/]+)/";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(urlString);
        if (matcher.find()) {
            String address = matcher.group(1);
            return address.replace("-", " ");
        }
        return null;
    }


}
