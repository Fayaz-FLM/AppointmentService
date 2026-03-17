package com.flmhospitals.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "StaffService")
public interface DoctorClient {

	@GetMapping("/doctorSchedule/isDoctorAvailable")
	Boolean isDoctorAvailable(@RequestParam("staffId") String staffId,
			@RequestParam("date") String date);

	@GetMapping("/staff/getDoctorName/{doctorId}")
	String getDoctorName(@PathVariable(name="doctorId") String doctorId);
	
	@GetMapping("/staff/getSpecialization/{doctorId}")
	String getSpecialization(@PathVariable(name="doctorId") String doctorId);
	 
}