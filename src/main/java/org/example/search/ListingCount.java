package org.example.search;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListingCount {

    private int count;

    @Builder.Default
    private Status status = Status.NOT_ATTEMPTED;

}
