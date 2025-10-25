package com.flmhospitals.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentDetailsResponseDTO {
	
    private String appointmentId;
    
    private Long patientId;
    
    private Long doctorId;
    
    private LocalDate appointmentDate;
    
    private LocalTime startTime;
    
    private LocalTime endTime;
    
    private String status;
    
    private String notes;

}
