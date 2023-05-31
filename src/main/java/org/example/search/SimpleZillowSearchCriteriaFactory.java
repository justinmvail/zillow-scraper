package org.example.search;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.example.search.builder.*;

import java.util.ArrayList;
import java.util.Collections;

@Data
@Builder
public class SimpleZillowSearchCriteriaFactory {

    @NonNull
    private String stateAbbreviation;
    private int minimumBaths;
    private int minimumBedrooms;
    private int minimumSquareFootage;
    private int minimumPrice;
    private int maximumPrice;
    private int pageNumber;

    public ZillowSearchCriteria generateZillowSearchCriteria (){
        return ZillowSearchCriteria.builder()
                .pagination(Pagination.builder().currentPage(pageNumber).build())
                .usersSearchTerm(stateAbbreviation)
                .mapBounds(MapBounds.builder()
                        .north(null)
                        .south(null)
                        .east(null)
                        .west(null).build())
                .mapZoom(10)
                .regionSelection(new ArrayList<>(Collections.singletonList(RegionSelection.builder()
                        .regionType(null).regionType(null).build())))
                .isMapVisible(true)
                .isListVisible(true)
                .filterState(FilterState.builder()
                        .beds(Beds.builder().min(minimumBedrooms).build())
                        .baths(Baths.builder().min(minimumBaths).build())
                        .sqft(Sqft.builder().min(minimumSquareFootage).build())
                        .price(Price.builder().min(minimumPrice).max(maximumPrice).build())
                        .con(Con.builder().value(false).build()) //condo
                        .apa(Apa.builder().value(false).build()) //apartment
                        .mf(Mf.builder().value(false).build()) // multifamily
                        .land(Land.builder().value(false).build()) //land
                        .tow(Tow.builder().value(false).build()) //townhouse
                        .manu(Manu.builder().value(false).build()) // manufactured
                        .cmsn(Cmsn.builder().value(false).build())
                        .apco(Apco.builder().value(false).build())
                        .mp(Mp.builder().max(0).build())
                        .sort(Sort.builder().value("pricea").build())
                        .build())
                .build();
    }
}
