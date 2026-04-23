package com.flmhospitals.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequestDTO {
	
	private String patientId;
	
	private String doctorId;
	
	private LocalDate appointmentDate;
	
	private LocalTime startTime;
	
	private LocalTime endTime;
	
	private String notes;
	
	private String reasonForVisit;
	


}
