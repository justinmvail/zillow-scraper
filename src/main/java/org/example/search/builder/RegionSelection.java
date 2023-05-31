
package org.example.search.builder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegionSelection {

    @SerializedName("regionId")
    @Expose
    private Integer regionId;
    @SerializedName("regionType")
    @Expose
    private Integer regionType;

}
