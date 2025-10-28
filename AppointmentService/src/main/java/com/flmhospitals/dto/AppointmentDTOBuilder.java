package com.flmhospitals.dto;

import com.flmhospitals.model.Appointment;

import lombok.Builder;

@Builder
public class AppointmentDTOBuilder {
	
	public static AppointmentResponseDTO toAppointmentResponseDTO(Appointment appointment, String patientName, String doctorName) {
	    if (appointment == null) {
	        return null;
	    }

	    return AppointmentResponseDTO.builder()
	            .appointmentId(appointment.getAppointmentId())
	            .patientName(patientName)
	            .doctorName(doctorName)
	            .appointmentDate(appointment.getAppointmentDate())
	            .startTime(appointment.getStartTime() != null ? appointment.getStartTime().toLocalTime() : null)
	            .endTime(appointment.getEndTime() != null ? appointment.getEndTime().toLocalTime() : null)
	            .status(appointment.getStatus())
	            .notes(appointment.getNotes())
	            .build();
	}

}
