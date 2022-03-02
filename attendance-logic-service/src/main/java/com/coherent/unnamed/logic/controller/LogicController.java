package com.coherent.unnamed.logic.controller;

import com.coherent.unnamed.logic.Constants.PATH;
import com.coherent.unnamed.logic.Response.BaseResponse;
import com.coherent.unnamed.logic.Response.PageResponse;
import com.coherent.unnamed.logic.dto.AttendenceDTO;
import com.coherent.unnamed.logic.dto.DetailsDTO;
import com.coherent.unnamed.logic.dto.TimeLogsDTO;
import com.coherent.unnamed.logic.dto.UserDTO;
import com.coherent.unnamed.logic.service.LogicService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/*@EnableScheduling*/
@RestController
@RequestMapping(value = PATH.UNNAMED_LOGIC_SERVICE)
public class LogicController {

	@Autowired
	private LogicService logicService;


	@PostMapping(value =PATH.REGISTER_ATTENDANCE)
	@ApiOperation(value = "Attendance punch", authorizations = {
			@Authorization(value = "Bearer")})
	public BaseResponse saveTimeLogs(@RequestBody TimeLogsDTO timeLogsDTO){
		BaseResponse baseResponse = new BaseResponse();
		baseResponse.setData(logicService.saveTimeLogs(timeLogsDTO));
		return baseResponse;
	}

	@PostMapping(value = PATH.VERIFY_LOCATION)
	@ApiOperation(value = "location verify", authorizations = {
			@Authorization(value = "Bearer")})
	public BaseResponse verifyLocation(@RequestParam Double longitude, @RequestParam Double latitude){
		String response=logicService.verifyLocation(longitude,latitude);
		BaseResponse baseResponse = new BaseResponse();
		baseResponse.setData(response);
		return baseResponse;
	}


	@GetMapping(value = PATH.LIST_BY_DAYS_AND_MONTH)
	@ApiOperation(value = "Get by Month and day", authorizations = {
			@Authorization(value = "Bearer")})
	public BaseResponse<List<AttendenceDTO>> listByDayMonth(@RequestParam int year,@RequestParam int month) {
		BaseResponse<List<AttendenceDTO>> baseResponse = null;
		baseResponse = BaseResponse.<List<AttendenceDTO>>builder().Data(logicService.listByDayMonth(year, month)).build();
		return baseResponse;
	}

	@GetMapping(value = PATH.LIST_BY_DATE)
	@ApiOperation(value = "Get by date", authorizations = {
			@Authorization(value = "Bearer")})
	public BaseResponse<DetailsDTO> listByDate(@RequestParam String date) {
		BaseResponse<DetailsDTO> baseResponse = null;
		baseResponse = BaseResponse.<DetailsDTO>builder().Data(logicService.listByDate(date)).build();
		return baseResponse;
	}

	@Scheduled(fixedDelay = 10000)
	public void calculatehours(){
		logicService.calculatehours();
	}

	@GetMapping("/getbyid")
	@ApiOperation(value = "Attendance punch", authorizations = {
			@Authorization(value = "Bearer")})
	public UserDTO getAllDetailsById(@RequestParam Long id){
		return logicService.listAllDetailsById(id);
	}

	@GetMapping("/get")
	@ApiOperation(value = "Attendance punch", authorizations = {
			@Authorization(value = "Bearer")})
	public PageResponse getAllDetails(@RequestParam(value = "pageNo",defaultValue = "0") int pageNo,
									  @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
									  @RequestParam(value = "sortBy",defaultValue = "id") String sortBy) {
		return logicService.listAllDetails(pageNo, pageSize, sortBy);

	}
	@PostMapping(value = "/profileupload")
	@ApiOperation(value = "upload image", authorizations = {
			@Authorization(value = "Bearer")
	})
	public BaseResponse upload(@RequestParam("file") MultipartFile file, @RequestParam("id") int id) throws Exception {
		return logicService.saveUser(file,id);
	}
}

