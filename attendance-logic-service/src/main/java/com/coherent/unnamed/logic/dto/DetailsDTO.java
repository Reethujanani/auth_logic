package com.coherent.unnamed.logic.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailsDTO {

  public String date;
  public long hours;
  private List<LoggedDetailsDTO> logs;

}
