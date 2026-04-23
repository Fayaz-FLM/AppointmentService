package com.flmhospitals.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flmhospitals.builder.AppointmentBuilder;
import com.flmhospitals.builder.AppointmentDTOBuilder;
import com.flmhospitals.clients.DoctorClient;
import com.flmhospitals.clients.NotificationClient;
import com.flmhospitals.clients.PatientClient;
import com.flmhospitals.dao.AppointmentRepository;
import com.flmhospitals.dao.DiagnosisRepository;
import com.flmhospitals.dto.AppointmentRequestDTO;
import com.flmhospitals.dto.AppointmentResponseDTO;
import com.flmhospitals.dto.DiagnosisRequestDTO;
import com.flmhospitals.dto.EmailRequestDto;
import com.flmhospitals.dto.PatientResponseDto;
import com.flmhospitals.dto.RescheduleAppointmentDTO;
import com.flmhospitals.exception.AppointmentAlreadyExistsException;
import com.flmhospitals.exception.AppointmentNotFoundException;
import com.flmhospitals.exception.DoctorUnAvailableException;
import com.flmhospitals.exception.InvalidTimeException;
import com.flmhospitals.model.Appointment;
import com.flmhospitals.model.Diagnosis;
import com.flmhospitals.service.AiDietPlanService;
import com.flmhospitals.service.AppointmentService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {

	public final AppointmentRepository appointmentRepository;
	public final DoctorClient doctorClient;
	public final PatientClient patientClient;
	public final NotificationClient notificationClient;
	public final DiagnosisRepository diagnosisRepository;
	public final AiDietPlanService aiDietPlanService;

	public AppointmentServiceImpl(AppointmentRepository appointmentRepository, DoctorClient doctorClient, 
			PatientClient patientClient, NotificationClient notificationClient, DiagnosisRepository diagnosisRepository,
			AiDietPlanService aiDietPlanService) {
		this.appointmentRepository = appointmentRepository;
		this.doctorClient = doctorClient;
		this.patientClient = patientClient;
		this.notificationClient = notificationClient;
		this.diagnosisRepository = diagnosisRepository;
		this.aiDietPlanService = aiDietPlanService;
	}

	@Override
	public List<String> getPatientsByDoctor(String staffId, LocalDate startDate, LocalDate endDate) {
		return appointmentRepository.findPatientsByStaffId(staffId, startDate, endDate);
	}

	@Override
	@Transactional
	public AppointmentResponseDTO bookAppointment(AppointmentRequestDTO appointmentRequestDto) {
		log.info("Booking Appointment for the user {}", appointmentRequestDto.getPatientId());

		if (appointmentRequestDto.getStartTime().isAfter(appointmentRequestDto.getEndTime()) || 
		    appointmentRequestDto.getStartTime().equals(appointmentRequestDto.getEndTime())) {
			throw new InvalidTimeException("Start time must be before end time");
		}

		if (appointmentRequestDto.getAppointmentDate().isBefore(LocalDate.now())) {
			log.info("Invalid Date - appointment date is in the past: {}", appointmentRequestDto.getAppointmentDate());
			throw new InvalidTimeException("Cannot book appointments for past dates. Please select a future date.");
		}

		LocalDate appointmentDate = appointmentRequestDto.getAppointmentDate();
		String date = appointmentDate.toString();

		List<Appointment> doctorAppointments = appointmentRepository.findAppointmentsByDoctorId(
				appointmentRequestDto.getDoctorId(), appointmentRequestDto.getAppointmentDate(),
				appointmentRequestDto.getStartTime(), appointmentRequestDto.getEndTime());

		if (!doctorAppointments.isEmpty()) {
			throw new AppointmentAlreadyExistsException(
					"Doctor already has an appointment during this time slot. Please choose a different time.");
		}

		List<Appointment> patientAppointments = appointmentRepository.findByPatientId(
				appointmentRequestDto.getPatientId(), appointmentRequestDto.getAppointmentDate(),
				appointmentRequestDto.getStartTime(), appointmentRequestDto.getEndTime());
		
		if (!patientAppointments.isEmpty()) {
			throw new AppointmentAlreadyExistsException(
					"Patient already has an appointment during this time slot. Please choose a different time.");
		}

		Appointment appointment = AppointmentBuilder.buildAppointmentFromAppointmentRequestDTO(appointmentRequestDto);
		Appointment savedAppointment = appointmentRepository.save(appointment);
		
		try {
			sendAppointmentNotification(savedAppointment);
		} catch (Exception e) {
			log.error("Failed to send appointment notification for appointment {}: {}", 
					savedAppointment.getAppointmentId(), e.getMessage());
		}

		return getAppointmentDetails(savedAppointment.getAppointmentId());
	}

	private void sendAppointmentNotification(Appointment savedAppointment) {
		ResponseEntity<PatientResponseDto> patientDetails = patientClient.getPatientById(savedAppointment.getPatientId());
		if (patientDetails != null && patientDetails.getBody() != null) {
			PatientResponseDto patient = patientDetails.getBody();
			if (patient.getPatientEmail() != null && !patient.getPatientEmail().isEmpty()) {
				EmailRequestDto emailRequest = new EmailRequestDto();
				emailRequest.setTo(patient.getPatientEmail());
				emailRequest.setSubject("Appointment Confirmation - MedSync");
				emailRequest.setBody(
				        "<h2>Dear " + patient.getPatientName() + ",</h2>"
				        + "<p>Your appointment has been successfully scheduled.</p>"
				        + "<p><b>Appointment ID:</b> " + savedAppointment.getAppointmentId() + "</p>"
				        + "<p><b>Date :</b> " + savedAppointment.getAppointmentDate()+ "</p>"
				        + "<p><b>Time :</b> " + savedAppointment.getStartTime() +" - "+savedAppointment.getEndTime() + "</p>"
				        + "<p>Thank you for choosing MedSync.</p>"
				);
				notificationClient.sendEmail(emailRequest);
			}
		}
	}

	@Override
	public List<AppointmentResponseDTO> getAllAppointmentsForAllDoctors(LocalDate date) {
		List<Appointment> appointments = appointmentRepository.findByAppointmentDate(date);
		return buildResponseList(appointments);
	}

	@Override
	public List<AppointmentResponseDTO> getAllAppointmentsOfDoctor(String doctorId, LocalDate date) {
		List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, date);
		return buildResponseList(appointments);
	}

	@Override
	public List<AppointmentResponseDTO> getAllFutureAppointmentsOfDoctor(String doctorId) {
		List<Appointment> appointments = appointmentRepository.findByDoctorId(doctorId)
											.stream()
											.filter(appointment -> appointment.getAppointmentDate().isAfter(LocalDate.now()))
											.toList();
		return buildResponseList(appointments);
	}

	/**
	 * Builds response DTOs for a list of appointments, caching doctor/patient name
	 * lookups to avoid N+1 Feign calls.
	 */
	private List<AppointmentResponseDTO> buildResponseList(List<Appointment> appointments) {
		java.util.Map<String, String> doctorNameCache = new java.util.HashMap<>();
		java.util.Map<String, String> patientNameCache = new java.util.HashMap<>();

		return appointments.stream().map(appointment -> {
			Diagnosis diagnosis = diagnosisRepository.findByAppointmentId(appointment.getAppointmentId()).orElse(null);
			AppointmentResponseDTO dto = AppointmentDTOBuilder.buildAppointmentResponseDTO(appointment, diagnosis);

			String doctorName = doctorNameCache.computeIfAbsent(appointment.getDoctorId(), id -> {
				try { return doctorClient.getDoctorName(id); }
				catch (Exception e) { log.warn("Failed to fetch doctor name for {}: {}", id, e.getMessage()); return null; }
			});
			if (doctorName != null) dto.setDoctorName(doctorName);

			String patientName = patientNameCache.computeIfAbsent(appointment.getPatientId(), id -> {
				try { return patientClient.getPatientName(id); }
				catch (Exception e) { log.warn("Failed to fetch patient name for {}: {}", id, e.getMessage()); return null; }
			});
			if (patientName != null) dto.setPatientName(patientName);

			return dto;
		}).toList();
	}
	
	@Override
	@Transactional
	public AppointmentResponseDTO reScheduleAppointment(String appointmentId, RescheduleAppointmentDTO rescheduleAppointmentDTO) {
		if (rescheduleAppointmentDTO.getNewStartTime().isAfter(rescheduleAppointmentDTO.getNewEndTime()) || 
		    rescheduleAppointmentDTO.getNewStartTime().equals(rescheduleAppointmentDTO.getNewEndTime())) {
			throw new InvalidTimeException("Start time must be before end time");
		}

		if (rescheduleAppointmentDTO.getNewDate().isBefore(LocalDate.now())) {
			throw new InvalidTimeException("Cannot reschedule appointments to past dates.");
		}

		Appointment appointment = appointmentRepository.findById(appointmentId)
				.orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id " + appointmentId));

		appointment.setAppointmentDate(rescheduleAppointmentDTO.getNewDate());
		appointment.setStartTime(rescheduleAppointmentDTO.getNewStartTime());
		appointment.setEndTime(rescheduleAppointmentDTO.getNewEndTime());
		
		Appointment savedAppointment = appointmentRepository.save(appointment);
		
		try {
			sendRescheduleAppointmentNotification(savedAppointment);
		} catch (Exception e) {
			log.error("Failed to send reschedule notification: {}", e.getMessage());
		}

		return getAppointmentDetails(savedAppointment.getAppointmentId());
	}

	private void sendRescheduleAppointmentNotification(Appointment savedAppointment) {
		ResponseEntity<PatientResponseDto> patientDetails = patientClient.getPatientById(savedAppointment.getPatientId());
		if (patientDetails != null && patientDetails.getBody() != null) {
			PatientResponseDto patient = patientDetails.getBody();
			if (patient.getPatientEmail() != null && !patient.getPatientEmail().isEmpty()) {
				EmailRequestDto emailRequest = new EmailRequestDto();
				emailRequest.setTo(patient.getPatientEmail());
				emailRequest.setSubject("Appointment Rescheduled - MedSync");
				emailRequest.setBody(
				        "<h2>Dear " + patient.getPatientName() + ",</h2>"
				        + "<p>Your appointment has been <b>successfully rescheduled</b>.</p>"
				        + "<p><b>Appointment ID:</b> " + savedAppointment.getAppointmentId() + "</p>"
				        + "<p><b>New Date :</b> " + savedAppointment.getAppointmentDate() + "</p>"
				        + "<p><b>New Time :</b> " + savedAppointment.getStartTime() +" - "+savedAppointment.getEndTime() + "</p>"
				        + "<p>Thank you for choosing MedSync.</p>"
				);
				notificationClient.sendEmail(emailRequest);
			}
		}
	}	

	@Override
	@Transactional
	public boolean cancelAppointment(String appointmentId) {
		Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new AppointmentNotFoundException("Appointment not found"));
		if ("CANCELLED".equals(appointment.getStatus())) {
			throw new IllegalStateException("Appointment is already cancelled");
		}
		appointment.setStatus("CANCELLED");
		appointmentRepository.save(appointment);
		try {
			appointmentCancelledNotification(appointment);
		} catch (Exception e) {
			log.error("Failed to send cancellation notification: {}", e.getMessage());
		}
		return true;
	}

	private void appointmentCancelledNotification(Appointment appointment) {
		ResponseEntity<PatientResponseDto> patientDetails = patientClient.getPatientById(appointment.getPatientId());
		if (patientDetails != null && patientDetails.getBody() != null) {
			PatientResponseDto patient = patientDetails.getBody();
			if (patient.getPatientEmail() != null && !patient.getPatientEmail().isEmpty()) {
				EmailRequestDto emailRequest = new EmailRequestDto();
				emailRequest.setTo(patient.getPatientEmail());
				emailRequest.setSubject("Appointment Cancelled - MedSync");
				emailRequest.setBody(
				        "<h2>Dear " + patient.getPatientName() + ",</h2>"
				        + "<p>Your appointment has been <b>successfully cancelled</b>.</p>"
				        + "<p><b>Appointment ID:</b> " + appointment.getAppointmentId() + "</p>"
				        + "<p>Thank you for choosing MedSync.</p>"
				);
				notificationClient.sendEmail(emailRequest);
			}
		}
	}

	@Override
	@Transactional
	public AppointmentResponseDTO submitDiagnosis(String appointmentId, DiagnosisRequestDTO diagnosisRequest) {
		log.info("Submitting diagnosis for appointment ID: {}", appointmentId);
		
		Appointment appointment = appointmentRepository.findById(appointmentId)
				.orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id " + appointmentId));
		
		Diagnosis diagnosis = diagnosisRepository.findByAppointmentId(appointmentId)
				.orElse(new Diagnosis());
		
		diagnosis.setAppointmentId(appointmentId);
		diagnosis.setDiagnosisSummary(diagnosisRequest.getDiagnosisSummary());
		diagnosis.setPrescription(diagnosisRequest.getPrescription());
		diagnosis.setMedicines(diagnosisRequest.getMedicines());
		diagnosis.setNotesForReceptionist(diagnosisRequest.getNotesForReceptionist());
		diagnosis.setFollowUpSuggestion(diagnosisRequest.getFollowUpSuggestion());
		
		diagnosisRepository.save(diagnosis);
		
		appointment.setStatus("DIAGNOSED");
		appointmentRepository.save(appointment);
		
		return getAppointmentDetails(appointmentId);
	}

	@Override
	public AppointmentResponseDTO getAppointmentDetails(String appointmentId) {
		Appointment appointment = appointmentRepository.findById(appointmentId)
		.orElseThrow(()->new AppointmentNotFoundException("no appointment found with the the Id :" + appointmentId));
		
		Diagnosis diagnosis = diagnosisRepository.findByAppointmentId(appointmentId).orElse(null);
		AppointmentResponseDTO appointmentResponseDTO = AppointmentDTOBuilder.buildAppointmentResponseDTO(appointment, diagnosis);
		
		try {
			String doctorName = doctorClient.getDoctorName(appointment.getDoctorId());
			appointmentResponseDTO.setDoctorName(doctorName);
		} catch (Exception e) {
			log.warn("Failed to fetch doctor name: {}", e.getMessage());
		}
		
		try {
			String PatientName = patientClient.getPatientName(appointment.getPatientId());
			appointmentResponseDTO.setPatientName(PatientName);
		} catch (Exception e) {
			log.warn("Failed to fetch patient name: {}", e.getMessage());
		}
		
		return appointmentResponseDTO;
	}

	@Override
	@Transactional
	public AppointmentResponseDTO generateDietPlan(String appointmentId) {
		log.info("Generating AI diet plan for appointment ID: {}", appointmentId);
		
		Appointment appointment = appointmentRepository.findById(appointmentId)
				.orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id " + appointmentId));
		
		Diagnosis diagnosis = diagnosisRepository.findByAppointmentId(appointmentId)
				.orElseThrow(() -> new IllegalStateException("Diagnosis must be completed before generating a diet plan"));
		
		if (!"DIAGNOSED".equals(appointment.getStatus())) {
			throw new IllegalStateException("Appointment status must be DIAGNOSED");
		}
		
		String dietPlan = aiDietPlanService.generateDietPlan(appointment, diagnosis);
		diagnosis.setDietPlan(dietPlan);
		diagnosisRepository.save(diagnosis);
		
		return getAppointmentDetails(appointmentId);
	}
}
