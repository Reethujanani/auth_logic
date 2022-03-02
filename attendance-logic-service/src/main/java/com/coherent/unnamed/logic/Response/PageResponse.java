package com.coherent.unnamed.logic.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    @Builder.Default
    private String StatusCode="200";

    @Builder.Default
    private String StatusMsg="SUCCESS";

    private T Data;

    private int totalRecord;
    
    private boolean hasNext;

    private boolean hasPrevious;

}
