package org.example.search;

import lombok.Builder;
import lombok.Data;
import org.example.search.builder.ZillowSearchCriteria;

@Data
@Builder
public class ZillowSearch {

    private ZillowSearchCriteria criteria;

    private ZillowSearchResult result;

}
