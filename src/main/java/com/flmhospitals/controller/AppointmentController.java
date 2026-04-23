package com.flmhospitals.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flmhospitals.dto.AppointmentRequestDTO;
import com.flmhospitals.dto.AppointmentResponseDTO;
import com.flmhospitals.dto.DiagnosisRequestDTO;
import com.flmhospitals.dto.RescheduleAppointmentDTO;
import com.flmhospitals.model.Appointment;
import com.flmhospitals.service.AppointmentService;

@RestController
@RequestMapping("/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {
	
	private final AppointmentService appointmentService;
	
	public AppointmentController(AppointmentService appointmentService) {
		this.appointmentService = appointmentService;
	}

	@GetMapping("/getDoctorPatients/{staffId}")
	public List<String> getPatientsVisitedByDoctor(@PathVariable(name="staffId") String staffId, @RequestParam("startDate") String startdate,@RequestParam("endDate") String enddate){
		LocalDate startDate = LocalDate.parse(startdate);
		LocalDate endDate = LocalDate.parse(enddate);
		return appointmentService.getPatientsByDoctor(staffId,startDate,endDate);
	}
	
	@PostMapping("/bookAppointment")
	public ResponseEntity<AppointmentResponseDTO> bookAppointment(@RequestBody AppointmentRequestDTO appointmentRequestDto){
		AppointmentResponseDTO ResponseDto = appointmentService.bookAppointment(appointmentRequestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto);
	}
	
	@GetMapping("/{date}")
	public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointmentsForAllDoctors(@PathVariable("date") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)  LocalDate date){
		return ResponseEntity.ok(appointmentService.getAllAppointmentsForAllDoctors(date));
	}
	
	@GetMapping("/{doctorid}/{date}")
	public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointmentsOfDoctor(@PathVariable("doctorid") String doctorId,@PathVariable("date") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate date){
		return ResponseEntity.ok(appointmentService.getAllAppointmentsOfDoctor(doctorId, date));
	}
	
	@DeleteMapping("/{appointmentId}")
	public ResponseEntity<String> cancelAppointment(@PathVariable("appointmentId") String appointmentId) {
	    appointmentService.cancelAppointment(appointmentId);
	    return ResponseEntity.ok("Appointment cancelled successfully");
	}

	@GetMapping("/allFutureAppointments/{doctorid}/")
	public ResponseEntity<List<AppointmentResponseDTO>> getAllFutureAppointmentsOfDoctor(@PathVariable("doctorid") String doctorId){
		return ResponseEntity.ok(appointmentService.getAllFutureAppointmentsOfDoctor(doctorId));
	}
	
	@PutMapping("/reScheduleAppointment/{appointmentId}")
	public ResponseEntity<AppointmentResponseDTO> reScheduleAppointment(@PathVariable(name="appointmentId") String appointmentId, @RequestBody RescheduleAppointmentDTO rescheduleAppointmentDTO){
		AppointmentResponseDTO ResponseDto = appointmentService.reScheduleAppointment(appointmentId,rescheduleAppointmentDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto);
	}	
	
	@GetMapping("/getAppointmentDetails/{appointmentId}")
	public ResponseEntity<AppointmentResponseDTO> getAppointmentDetails(@PathVariable String appointmentId) {
		return new ResponseEntity<>(appointmentService.getAppointmentDetails(appointmentId), HttpStatus.OK);
	}

	@PostMapping("/submitDiagnosis/{appointmentId}")
	public ResponseEntity<AppointmentResponseDTO> submitDiagnosis(@PathVariable String appointmentId,
			@RequestBody DiagnosisRequestDTO diagnosisRequest) {
		return new ResponseEntity<>(appointmentService.submitDiagnosis(appointmentId, diagnosisRequest), HttpStatus.OK);
	}
	@PostMapping("/generateDietPlan/{appointmentId}")
	public ResponseEntity<AppointmentResponseDTO> generateDietPlan(@PathVariable String appointmentId) {
		return new ResponseEntity<>(appointmentService.generateDietPlan(appointmentId), HttpStatus.OK);
	}
}
