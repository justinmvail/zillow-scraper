
package org.example.search.builder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Sqft {

    @SerializedName("min")
    @Expose
    public Integer min;

}
