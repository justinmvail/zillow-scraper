package org.example.search;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class ZillowSearchResult {

    private int expectedListingCount;

    private Set<String> ListingLinks;

}
