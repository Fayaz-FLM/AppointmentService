package com.flmhospitals.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentDetailsRequestDTO {

	private Long patientId;
	
	private LocalDate appointmentDate;
	
	private LocalTime startTime;
	
	private LocalTime endTime;

}
