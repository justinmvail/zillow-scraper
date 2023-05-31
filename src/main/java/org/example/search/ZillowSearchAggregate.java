package org.example.search;

import lombok.Builder;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Builder
public class ZillowSearchAggregate {

    List<ZillowSearch> searches;

    public int getExpectedNumberOfListings(){
        AtomicInteger count = new AtomicInteger();
        searches.forEach(x-> count.addAndGet(x.getResult().getExpectedListingCount()));
        return count.get();
    }

    public int getActualNumberOfListings(){
        AtomicInteger count = new AtomicInteger();
        searches.forEach(x-> count.addAndGet(x.getResult().getListingLinks().size()));
        return count.get();
    }

    public int getActualNumberOfNonDuplicatedListings(){
        return getAllListingLinks().size();
    }

    public Set<String> getAllListingLinks(){
        Set<String> allListings = new HashSet<>();
        searches.forEach(x-> allListings.addAll(x.getResult().getListingLinks()));
        return allListings;
    }
}
