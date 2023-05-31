
package org.example.search.builder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ZillowSearchCriteria {

    @SerializedName("pagination")
    @Expose
    public Pagination pagination;
    @SerializedName("usersSearchTerm")
    @Expose
    public String usersSearchTerm;
    @SerializedName("mapBounds")
    @Expose
    public MapBounds mapBounds;
    @SerializedName("mapZoom")
    @Expose
    public Integer mapZoom;
    @SerializedName("regionSelection")
    @Expose
    public List<RegionSelection> regionSelection;
    @SerializedName("isMapVisible")
    @Expose
    public Boolean isMapVisible;
    @SerializedName("filterState")
    @Expose
    public FilterState filterState;
    @SerializedName("isListVisible")
    @Expose
    public Boolean isListVisible;

    public int expectedListingCount;

    public String getUrl() {
        String baseUrl = String.format("https://www.zillow.com/%s/?searchQueryState=",
                this.getUsersSearchTerm());
        Gson gson = new Gson();
        String urlParams = URLEncoder.encode(gson.toJson(this), StandardCharsets.UTF_8);
        return baseUrl+urlParams;
    }

    public ZillowSearchCriteria getCloneWithPriceChange(int min, int max){
        Gson gson = new Gson();
        String flattenedObject = gson.toJson(this);
        ZillowSearchCriteria newCriteria = gson.fromJson(flattenedObject, ZillowSearchCriteria.class);
        FilterState originalFilterState = this.getFilterState();
        newCriteria.setFilterState(FilterState.builder()
                .beds(Beds.builder().min(originalFilterState.getBeds().getMin()).build())
                .baths(Baths.builder().min(originalFilterState.getBaths().getMin()).build())
                .sqft(Sqft.builder().min(originalFilterState.getSqft().getMin()).build())
                .price(Price.builder().min(min).max(max).build())
                .con(Con.builder().value(originalFilterState.getCon().getValue()).build())
                .apa(Apa.builder().value(originalFilterState.getApa().getValue()).build())
                .mf(Mf.builder().value(originalFilterState.getMf().getValue()).build())
                .land(Land.builder().value(originalFilterState.getLand().getValue()).build())
                .tow(Tow.builder().value(originalFilterState.getTow().getValue()).build())
                .manu(Manu.builder().value(originalFilterState.getManu().getValue()).build())
                .cmsn(Cmsn.builder().value(originalFilterState.getCmsn().getValue()).build())
                .apco(Apco.builder().value(originalFilterState.getApco().getValue()).build())
                .mp(Mp.builder().max(originalFilterState.getMp().getMax()).build())
                .sort(Sort.builder().value(originalFilterState.getSort().getValue()).build())
                .build());
        return newCriteria;
    }
}
