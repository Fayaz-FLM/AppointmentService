package com.flmhospitals.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.flmhospitals.dto.PatientResponseDto;

@FeignClient(name = "PatientManagement")
public interface PatientClient {

	@GetMapping("/patients/getPatientName/{patientId}")
	String getPatientName(@PathVariable(name="patientId") String patientId);
	
	@GetMapping("/patients/{id}")
	public ResponseEntity<PatientResponseDto> getPatientById(@PathVariable(name="id") String patientId);

}
