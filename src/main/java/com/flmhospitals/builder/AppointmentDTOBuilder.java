package com.flmhospitals.builder;

import com.flmhospitals.dto.AppointmentResponseDTO;
import com.flmhospitals.model.Appointment;
import com.flmhospitals.model.Diagnosis;

public class AppointmentDTOBuilder {

	 public static AppointmentResponseDTO buildAppointmentResponseDTO(Appointment appointment, Diagnosis diagnosis) {
	        return AppointmentResponseDTO.builder()
	                .appointmentId(appointment.getAppointmentId())
	                .patientId(appointment.getPatientId())
	                .doctorId(appointment.getDoctorId())
	                .appointmentDate(appointment.getAppointmentDate())
	                .startTime(appointment.getStartTime())
	                .endTime(appointment.getEndTime())
	                .status(appointment.getStatus())
	                .notes(appointment.getNotes())
	                .reasonForVisit(appointment.getReasonForVisit())
	                .diagnosisSummary(diagnosis != null ? diagnosis.getDiagnosisSummary() : null)
	                .prescription(diagnosis != null ? diagnosis.getPrescription() : null)
	                .medicines(diagnosis != null ? diagnosis.getMedicines() : null)
	                .notesForReceptionist(diagnosis != null ? diagnosis.getNotesForReceptionist() : null)
	                .followUpSuggestion(diagnosis != null ? diagnosis.getFollowUpSuggestion() : null)
	                .dietPlan(diagnosis != null ? diagnosis.getDietPlan() : null)
	                .build();
	    }
	 
	 public static AppointmentResponseDTO buildAppointmentResponseDTO(Appointment appointment) {
		 return buildAppointmentResponseDTO(appointment, null);
	 }
}
