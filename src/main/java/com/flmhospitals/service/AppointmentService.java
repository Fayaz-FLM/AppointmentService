package com.flmhospitals.service;

import java.time.LocalDate;
import java.util.List;
import com.flmhospitals.dto.AppointmentRequestDTO;
import com.flmhospitals.dto.AppointmentResponseDTO;
import com.flmhospitals.dto.DiagnosisRequestDTO;
import com.flmhospitals.dto.RescheduleAppointmentDTO;
import com.flmhospitals.model.Appointment;

public interface AppointmentService {

	List<String> getPatientsByDoctor(String staffId,LocalDate startDate, LocalDate endDate);

	AppointmentResponseDTO bookAppointment(AppointmentRequestDTO appointmentRequestDto);

	List<AppointmentResponseDTO> getAllAppointmentsForAllDoctors(LocalDate date);
	
	List<AppointmentResponseDTO> getAllAppointmentsOfDoctor(String doctorId,LocalDate date);
	
	List<AppointmentResponseDTO> getAllFutureAppointmentsOfDoctor(String doctorId);

	AppointmentResponseDTO reScheduleAppointment(String appointmentId,RescheduleAppointmentDTO rescheduleAppointmentDTO);

	boolean cancelAppointment(String appointmentId);
	
	AppointmentResponseDTO getAppointmentDetails(String appointmentId);

	AppointmentResponseDTO submitDiagnosis(String appointmentId, DiagnosisRequestDTO diagnosisRequest);
	
	AppointmentResponseDTO generateDietPlan(String appointmentId);
}
