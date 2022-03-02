package com.coherent.unnamed.logic.service.impl;

import com.coherent.unnamed.logic.Config.UserContextHolder;
import com.coherent.unnamed.logic.Constants.Constants;
import com.coherent.unnamed.logic.Exception.CustomException;
import com.coherent.unnamed.logic.Response.BaseResponse;
import com.coherent.unnamed.logic.Response.PageResponse;
import com.coherent.unnamed.logic.dto.*;
import com.coherent.unnamed.logic.model.Attendance;
import com.coherent.unnamed.logic.model.TimeLogs;
import com.coherent.unnamed.logic.model.Users;
import com.coherent.unnamed.logic.repository.AttendanceRepository;
import com.coherent.unnamed.logic.repository.TimeLogsRepository;
import com.coherent.unnamed.logic.repository.UsersRepository;
import com.coherent.unnamed.logic.service.LogicService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class LogicServiceImpl implements LogicService {

	@Autowired
	private AttendanceRepository attendanceRepository;

	@Autowired
	private TimeLogsRepository timeLogsRepository;

	@Autowired
	private UsersRepository usersRepository;

	private Logger logger = LoggerFactory.getLogger(LogicServiceImpl.class);


	@Override
	public void calculatehours(){
		ArrayList<Long> hours = new ArrayList<>();
		List<Users> users = usersRepository.findAll();
		final long[] h = {0};
		users.stream().forEachOrdered(users1 -> {
			Date date  = new Date(System.currentTimeMillis()-24*60*60*1000);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String strDate= formatter.format(date);
			List<TimeLogs> timeLogs =  timeLogsRepository.findByUserId(strDate ,users1.getId());
			ArrayList<Long> punchin = new ArrayList<>();
			ArrayList<Long> punchout = new ArrayList<>();
			final int[] g = {0};
			timeLogs.stream().forEachOrdered(timeLogs1 -> {
				g[0] = timeLogs1.getIsLogged();
			});
			if(g[0]==1){
				Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
				TimeLogs timeLogs1 = new TimeLogs();
				timeLogs1.setCreatedAt(timeStamp);
				timeLogs1.setModifiedAt(timeStamp);
				timeLogs1.setUsers(users1);
				timeLogs1.setLongitude(Constants.LONGITUDE_START);
				timeLogs1.setLatitude(Constants.LATITUDE_START);
				timeLogs1.setCreatedBy(Constants.SYSTEM);
				timeLogs1.setModifiedBy(Constants.SYSTEM);
				timeLogs1.setIsLogged(0);
				timeLogsRepository.save(timeLogs1);
			}

			long time1 = timeLogs.stream().filter(timeLogs1 -> timeLogs1.getIsLogged() == 1).mapToLong(timeLogs1 ->
			{
				punchin.add((long) timeLogs1.getCreatedAt().toLocalDateTime().toLocalTime().getHour());
				return timeLogs1.getCreatedAt().toLocalDateTime().toLocalTime().getHour();
			}).sum();
			long time2 = timeLogs.stream().filter(timeLogs1 -> timeLogs1.getIsLogged() == 0).mapToLong(timeLogs1 -> {
				if(timeLogs1.getCreatedBy().equals(Constants.SYSTEM)){
					punchout.add(0L);
					return 0;
				}else{
					punchout.add((long) timeLogs1.getCreatedAt().toLocalDateTime().toLocalTime().getHour());
					return timeLogs1.getCreatedAt().toLocalDateTime().toLocalTime().getHour();
				}
			}).sum();

			long c = 0;
			for(int k = 0; k < punchout.size(); k++) {
				if(punchout.get(k)==0L)
				{
					c = c + 0;

				}else{
					c = c + punchout.get(k) - punchin.get(k);
				}

			}
			hours.add(c);

			System.out.println(hours.get((int) h[0]));

			Attendance attendence2 = new Attendance();
			Timestamp timeStamp = new Timestamp(System.currentTimeMillis()-24*60*60*1000);
			attendence2.setCreatedAt(timeStamp);
			attendence2.setModifiedAt(timeStamp);

			attendence2.setHours(hours.get((int) h[0]));

			if(attendence2.getHours()>6){
				attendence2.setIsPresent(Constants.IS_PRESENT);
			}else{
				attendence2.setIsPresent(Constants.IS_PRESENT_NOT);
			}
			attendence2.setActive(true);
			attendence2.setDeletedFlag(false);
			attendence2.setUsers(users1);

   

			attendence2.setCreatedBy(Constants.USER);
			attendence2.setModifiedBy(Constants.USER);
			attendanceRepository.save(attendence2);
			h[0]++;
		});

	}

	@Override
	public String saveTimeLogs(TimeLogsDTO timeLogsDTO) {
		try {
			if (timeLogsDTO != null) {
				Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
				TimeLogs timeLogs = new TimeLogs();
				UserContextDTO userContextDTO = UserContextHolder.getUserDto();
				Optional<Users> users = usersRepository.findById(userContextDTO.getId());
				if (users.isPresent()) {
					timeLogs.setUsers(users.get());
				} else {
					throw new CustomException(Constants.ERROR_CODE, Constants.ERROR);
				}
				timeLogs.setLongitude(timeLogsDTO.getLongitude());
				timeLogs.setLatitude(timeLogsDTO.getLatitude());
				timeLogs.setIsLogged(timeLogsDTO.getIsLogged());
				timeLogs.setActive(true);
				timeLogs.setDeletedFlag(false);
				timeLogs.setCreatedAt(timeStamp);
				timeLogs.setCreatedBy(Constants.USER);
				timeLogs.setModifiedAt(timeStamp);
				timeLogs.setModifiedBy(Constants.USER);
				timeLogsRepository.save(timeLogs);
				return Constants.SUCCESS;
			} else {
				throw new CustomException(Constants.ERROR_CODE, Constants.ERROR);
			}
		} catch (NoSuchElementException e) {

			e.printStackTrace();
			logger.error("Error while saving Product");
		}
		return Constants.SUCCESS;
	}


	@Override
	public String verifyLocation(Double longitude, Double latitude) {
		if ((Constants.LONGITUDE_START <= longitude && longitude <= Constants.LONGITUDE_END) &&
				(Constants.LATITUDE_START <= latitude && latitude <= Constants.LATITUDE_END)) {
			return Constants.IN_RANGE;
		} else {
			throw new CustomException(Constants.ERROR_CODE, Constants.NOT_IN_RANGE);
		}
	}

	@Override
	public DetailsDTO listByDate(String date) {
		List<LoggedDetailsDTO> loggedDetailsDTOList=new ArrayList<>();
		UserContextDTO userContextDTO = UserContextHolder.getUserDto();
		Attendance attendances = attendanceRepository.findByCreatedAtDate(date, userContextDTO.getId());
		DetailsDTO detailsDTO = new DetailsDTO();
		detailsDTO.setDate(date);
		detailsDTO.setHours(attendances.getHours());
		List<TimeLogs> timeLogs = timeLogsRepository.findAllByCreatedAtDate(date, userContextDTO.getId());
		timeLogs.forEach(data -> {
			LoggedDetailsDTO loggedDetailsDTO=new LoggedDetailsDTO();
			loggedDetailsDTO.setCreatedAt(data.getCreatedAt());
			loggedDetailsDTO.setCreatedBy(data.getCreatedBy());
			loggedDetailsDTO.setIsLogged(data.getIsLogged());
			loggedDetailsDTOList.add(loggedDetailsDTO);
		});
		detailsDTO.setLogs(loggedDetailsDTOList);
		return detailsDTO;
	}


	@Override
	public List<AttendenceDTO> listByDayMonth(int year, int month) {
		List<AttendenceDTO> attendenceDTOList = new ArrayList<>();
		UserContextDTO userContextDTO = UserContextHolder.getUserDto();
		List<Attendance> attendances = attendanceRepository.findAllByCreatedAt(year, month, userContextDTO.getId());
		attendances.forEach(data -> {
			AttendenceDTO attendenceDTO = new AttendenceDTO();
			String date = data.getCreatedAt().toString();
			String[] words=date.split("\\s");
			attendenceDTO.setCreatedAt(words[0]);
			attendenceDTO.setIsPresent(data.getIsPresent());
			attendenceDTOList.add(attendenceDTO);
		});
		return attendenceDTOList;
	}

	public PageResponse listAllDetails(int pageNo ,int pageSize, String sortBy){
		List<UserDTO> userDTO = new ArrayList<>();
		PageResponse pageResponse = new PageResponse();
	try {
		ModelMapper modelMapper = new ModelMapper();
		Pageable pageable = PageRequest.of(pageNo-1, pageSize, Sort.by(Sort.Direction.DESC, sortBy));
		Page<Users> users = usersRepository.findAll(pageable);
		users.stream().forEachOrdered(user->{
			UserDTO users1= modelMapper.map(user,UserDTO.class);
			userDTO.add(users1);
		});
		pageResponse.setData(userDTO);
		pageResponse.setHasNext(users.hasNext());
		pageResponse.setHasPrevious(users.hasPrevious());
		pageResponse.setTotalRecord(users.getNumberOfElements());

		} catch (Exception exception) {
		exception.printStackTrace();
	}
	return pageResponse;
	}

	@Override
	public UserDTO listAllDetailsById(Long id) {
		ModelMapper modelMapper = new ModelMapper();
		Optional<Users> users = usersRepository.findById(Math.toIntExact(id));
		UserDTO users1= modelMapper.map(users.get(),UserDTO.class);
		return users1;
	}

	@Override
	public BaseResponse saveUser(MultipartFile file,int id) throws Exception {
		Optional<Users> users = usersRepository.findById(id);
		if(users.isPresent()) {
			users.get().setImage(file.getBytes());
			usersRepository.save(users.get());
		}
		else {
			throw new Exception("user not found");
		}
		return new BaseResponse();
	}
}






