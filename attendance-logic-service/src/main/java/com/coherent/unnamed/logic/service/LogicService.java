package com.coherent.unnamed.logic.service;

import com.coherent.unnamed.logic.Response.BaseResponse;
import com.coherent.unnamed.logic.Response.PageResponse;
import com.coherent.unnamed.logic.dto.AttendenceDTO;
import com.coherent.unnamed.logic.dto.DetailsDTO;
import com.coherent.unnamed.logic.dto.TimeLogsDTO;
import com.coherent.unnamed.logic.dto.UserDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LogicService {

	String saveTimeLogs(TimeLogsDTO timeLogsDTO);

	List<AttendenceDTO> listByDayMonth(int year,int month);

	String verifyLocation(Double longitude, Double latitude);

	DetailsDTO listByDate(String date);

	void calculatehours();

	PageResponse listAllDetails(int pageNo, int pageSize, String sortBy);

	UserDTO listAllDetailsById(Long id);

    BaseResponse saveUser(MultipartFile file, int id) throws Exception;
}
