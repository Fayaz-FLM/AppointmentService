package com.flmhospitals.builder;


import com.flmhospitals.dto.AppointmentRequestDTO;
import com.flmhospitals.model.Appointment;

public class AppointmentBuilder {
	
	public static Appointment buildAppointmentFromAppointmentRequestDTO(AppointmentRequestDTO appointmentRequestDTO){
		
		return Appointment.builder()
					.patientId(appointmentRequestDTO.getPatientId())
					.doctorId(appointmentRequestDTO.getDoctorId())
					.appointmentDate(appointmentRequestDTO.getAppointmentDate())
					.startTime(appointmentRequestDTO.getStartTime())
					.endTime(appointmentRequestDTO.getEndTime())
					.notes(appointmentRequestDTO.getNotes())
					.reasonForVisit(appointmentRequestDTO.getReasonForVisit())
					.build();
		
		
		
	}

}
