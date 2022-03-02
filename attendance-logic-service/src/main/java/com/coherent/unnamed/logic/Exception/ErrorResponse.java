package com.coherent.unnamed.logic.Exception;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Data
@ToString
public class ErrorResponse {
    private int statusCode;

    private String statusMessage;
}
