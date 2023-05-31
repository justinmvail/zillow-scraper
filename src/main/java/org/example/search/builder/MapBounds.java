
package org.example.search.builder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MapBounds {

    @SerializedName("west")
    @Expose
    private Double west;
    @SerializedName("east")
    @Expose
    private Double east;
    @SerializedName("south")
    @Expose
    private Double south;
    @SerializedName("north")
    @Expose
    private Double north;

}
