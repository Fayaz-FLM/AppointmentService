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
    
    private String patientId;
    
    private String patientName;
    
    private String doctorId;
    
    private String doctorName;
    
    private LocalDate appointmentDate;
    
    private LocalTime startTime;
    
    private LocalTime endTime;
    
    private String status;
    
    private String notes;
    
    private String reasonForVisit;
    
    private String diagnosisSummary;
    
    private String prescription;
    
    private String medicines;
    
    private String notesForReceptionist;
    
    private String followUpSuggestion;
    
    private String dietPlan;
}

