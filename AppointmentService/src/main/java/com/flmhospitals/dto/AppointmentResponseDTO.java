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
public class AppointmentResponseDTO {
	
    private String appointmentId;
    
    private String patientName;
    
    private String doctorName;
    
    private LocalDate appointmentDate;
    
    private LocalTime startTime;
    
    private LocalTime endTime;
    
    private String status;
    
    private String notes;
}

