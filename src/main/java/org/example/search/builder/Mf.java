
package org.example.search.builder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Mf {

    @SerializedName("value")
    @Expose
    private Boolean value;

}
