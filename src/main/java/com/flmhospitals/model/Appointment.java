package com.flmhospitals.model;

import java.time.LocalDate;
import java.time.LocalTime;
import com.flmhospitals.Generator.AppointmentEntityListner;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "appointments")
@EntityListeners(value = AppointmentEntityListner.class)
public class Appointment {

	@Id
	@Column(name = "appointment_id", nullable = false, unique = true)
	private String appointmentId;

	private String patientId;

	private String doctorId;

	private LocalDate appointmentDate;

	private LocalTime startTime;

	private LocalTime endTime;

	private String status;

	private String notes;

	private String reasonForVisit;

	public Appointment(String patientId, String doctorId, LocalDate appointmentDate, LocalTime startTime, LocalTime endTime,

			String status, String notes) {
		super();
		this.patientId = patientId;
		this.doctorId = doctorId;
		this.appointmentDate = appointmentDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.status = status;
		this.notes = notes;
	}
}
