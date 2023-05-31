package org.example.search;

import lombok.Builder;
import lombok.Data;
import org.example.search.builder.Pagination;
import org.example.search.builder.ZillowSearchCriteria;
import org.example.selenium.DriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
public class ZillowListingSearcher {
    final int LISTINGS_PER_PAGE;
    final int MAX_PAGES;
    final int SCROLL_AMOUNT_PIXELS;
    final int ELEMENT_TIMEOUT;

    public ZillowSearchAggregate getZillowSearchAggregate(ZillowSearchCriteria searchCriteria) {
        Set<ZillowSearch> zillowSearch = new HashSet<>();
        List<ZillowSearch> searches = verifyAndCorrectSearchSplit(
                Collections.singletonList(
                    ZillowSearch
                            .builder()
                            .criteria(searchCriteria)
                            .build()));
        searches.forEach(x -> zillowSearch.add(search(x)));
        ZillowSearchAggregate aggregate =  ZillowSearchAggregate
                .builder()
                .searches(
                        new ArrayList<>(zillowSearch))
                .build();

        if(!isCountValid(aggregate)){
            ZillowSearchCriteria criteria = findMissingResults(aggregate);
            if(criteria != null){
                aggregate.getSearches().addAll(getZillowSearchAggregate(searchCriteria).getSearches());
            }
        }
        return aggregate;
    }

    private ZillowSearchCriteria findMissingResults(ZillowSearchAggregate aggregate){
        for (ZillowSearch zillowSearch : aggregate.getSearches()){
            ZillowSearchResult result = zillowSearch.getResult();
            if (result.getListingLinks().size() < result.getExpectedListingCount()) {
                return zillowSearch.getCriteria();
            }
        }
        return ZillowSearchCriteria.builder().build();
    }

    private boolean isCountValid(ZillowSearchAggregate aggregate){
        if(aggregate.getExpectedNumberOfListings()>aggregate.getActualNumberOfListings()) {
            return false;
        }
        return true;
    }

    private ZillowSearch search(ZillowSearch search) {
        int totalListings = getListingCount(search.getCriteria());
        int pageCount = totalListings / LISTINGS_PER_PAGE;
        int remainder = totalListings % LISTINGS_PER_PAGE;
        if (remainder > 0) {
            pageCount++;
        }
        Set<String> filteredLinks = new HashSet<>();
        for (int i = 1; i <= pageCount; i++) {
            FirefoxDriver loopDriver = DriverManager.getDriver();
            search.getCriteria().setPagination(Pagination.builder().currentPage(i).build());
            String url = search.getCriteria().getUrl();
            WebElement resultsDiv = null;
            try {
                loopDriver.get(url);
                WebDriverWait wait = new WebDriverWait(loopDriver, Duration.ofSeconds(ELEMENT_TIMEOUT));
                resultsDiv = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(By.id("search-page-list-container")));
            } catch (TimeoutException e) {
                System.out.println("there was a problem loading the page within the specified timeout.");
            }
            if (i != pageCount) {
                filteredLinks.addAll(getListings(loopDriver, resultsDiv, LISTINGS_PER_PAGE));
            }else{
                filteredLinks.addAll(getListings(loopDriver, resultsDiv, remainder));
            }
            DriverManager.closeDriver(loopDriver);
        }
        search.getResult().setListingLinks(filteredLinks);
        return search;
    }

    private List<ZillowSearch> verifyAndCorrectSearchSplit(List<ZillowSearch> zillowSearch){
        List<ZillowSearch> verified = new ArrayList<>();
        zillowSearch.forEach(x->{
            int listingCount = getListingCount(x.getCriteria());
            x.setResult(ZillowSearchResult.builder().expectedListingCount(listingCount).build());
            if(listingCount<= MAX_PAGES * LISTINGS_PER_PAGE){
                x.setResult(ZillowSearchResult.builder().expectedListingCount(listingCount).build());
                verified.add(x);
            }else{
                verified.addAll(verifyAndCorrectSearchSplit(splitCriteria(x)));
            }
        });
        return verified;
    }

    private List<ZillowSearch> splitCriteria(ZillowSearch search) {
        List<ZillowSearch> zillowSearches = new ArrayList<>();
        if(search.getResult().getExpectedListingCount() <= MAX_PAGES * LISTINGS_PER_PAGE){
            List<ZillowSearch> list = new ArrayList<>();
            list.add(search);
            return list;
        }else {
            int numberOfSearches = search.getResult().getExpectedListingCount()/(MAX_PAGES * LISTINGS_PER_PAGE);
            if(numberOfSearches == 1 && search.getResult().getExpectedListingCount() > MAX_PAGES * LISTINGS_PER_PAGE){
                numberOfSearches ++;
            }
            List<ZillowSearchCriteria> zillowSearchCriteria = splitCriteria(search.getCriteria(), numberOfSearches);
            zillowSearchCriteria.forEach(x -> zillowSearches.add(ZillowSearch.builder().criteria(x).build()));
            return zillowSearches;
        }
    }

    private List<ZillowSearchCriteria> splitCriteria(ZillowSearchCriteria searchCriteria, int numberOfSearches){
        List<ZillowSearchCriteria> searches = new ArrayList<>();
        if (numberOfSearches <= 1){
            return List.of(searchCriteria);
        } else {
            int minPrice = searchCriteria.getFilterState().getPrice().getMin();
            int maxPrice = searchCriteria.getFilterState().getPrice().getMax();
            int priceDifference = maxPrice - minPrice;
            int priceIncrement = priceDifference/numberOfSearches;
            searches.add(searchCriteria.getCloneWithPriceChange(minPrice, minPrice+priceIncrement));
            for (int i = 1; i < numberOfSearches; i++){
                ZillowSearchCriteria previousCriteria = searches.get(searches.size()-1);
                searches.add(
                        previousCriteria.getCloneWithPriceChange(
                                previousCriteria.getFilterState().getPrice().getMin()+priceIncrement,
                                previousCriteria.getFilterState().getPrice().getMax()+priceIncrement));
            }
        }
        return searches;
    }

    private int getListingCount(ZillowSearchCriteria searchCriteria){
        String urlForCount = searchCriteria.getUrl();
        FirefoxDriver getCountDriver = DriverManager.getDriver();
        getCountDriver.get(urlForCount);

        WebElement totalListingCountElement = null;
        int totalListings = 0;
        WebDriverWait wait = new WebDriverWait(getCountDriver, Duration.ofSeconds(ELEMENT_TIMEOUT));
        try {
            totalListingCountElement = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".result-count")));
        } catch (TimeoutException e) {
            System.out.println(".result-count could not be found within the specified timeout.");
        }
        if (totalListingCountElement != null){
            totalListings = (extractLeadingNumber(totalListingCountElement.getText().trim()));
            DriverManager.closeDriver(getCountDriver);
            return totalListings;
        }
        DriverManager.closeDriver(getCountDriver);
        return totalListings;
    }

    private int extractLeadingNumber(String input) {
        String numberString = input.replaceAll("[^0-9]", "");
        return Integer.parseInt(numberString);
    }

    private Set<String> getListings(FirefoxDriver driver, WebElement gridResultsDiv, int listingCount) {
        long startNano = System.nanoTime();
        if (listingCount == 0) return new HashSet<>();
        int totalScrolled = 0;
        while (true) {
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollTop += "+ SCROLL_AMOUNT_PIXELS, gridResultsDiv);
            Document doc = Jsoup.parse(driver.getPageSource());
            Elements links = doc.select("a");
            Set<String> filteredLinks = links.stream()
                    .map(link -> link.absUrl("href"))  // Convert relative URLs to absolute URLs
                    .filter(link -> link.contains("zpid"))
                    .collect(Collectors.toSet());
            System.out.println(filteredLinks.size());
            if (filteredLinks.size() == listingCount) {
                return filteredLinks;
            }
            totalScrolled += SCROLL_AMOUNT_PIXELS;
            if (totalScrolled > 10000){
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollTop = 0", gridResultsDiv);
                totalScrolled = 0;
                if ((System.nanoTime()-startNano)/1_000_000_000 > ELEMENT_TIMEOUT){
                    return new HashSet<>();
                }
            }
        }
    }
}

