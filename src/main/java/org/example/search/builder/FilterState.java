
package org.example.search.builder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilterState {

    @SerializedName("baths")
    @Expose
    private Baths baths;
    @SerializedName("price")
    @Expose
    private Price price;
    @SerializedName("sqft")
    @Expose
    private Sqft sqft;
    @SerializedName("con")
    @Expose
    private Con con;
    @SerializedName("apa")
    @Expose
    private Apa apa;
    @SerializedName("mf")
    @Expose
    private Mf mf;
    @SerializedName("mp")
    @Expose
    private Mp mp;
    @SerializedName("sort")
    @Expose
    private Sort sort;
    @SerializedName("land")
    @Expose
    private Land land;
    @SerializedName("tow")
    @Expose
    private Tow tow;
    @SerializedName("manu")
    @Expose
    private Manu manu;
    @SerializedName("cmsn")
    @Expose
    private Cmsn cmsn;
    @SerializedName("apco")
    @Expose
    private Apco apco;
    @SerializedName("beds")
    @Expose
    private Beds beds;

}
