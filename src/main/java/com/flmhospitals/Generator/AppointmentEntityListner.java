package com.flmhospitals.Generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.flmhospitals.model.Appointment;

import jakarta.persistence.PrePersist;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AppointmentEntityListner {

	public static AppointmentIdGenerator appointmentIdGenerator;

	@Autowired
	public void init(AppointmentIdGenerator appointmentIdGenerator) {
		this.appointmentIdGenerator = appointmentIdGenerator;
	}

	@PrePersist
	public void generateStaffId(Appointment appointment) {
		log.info("PrePersist triggered for appointment");
		if (appointment.getAppointmentId() == null || appointment.getAppointmentId().isEmpty()) {
			String generatedId = appointmentIdGenerator.generateNextAppointmentId();
			appointment.setAppointmentId(generatedId);
			log.info("Generated appointment ID: {}", generatedId);
		}
		if (appointment.getStatus() == null || appointment.getStatus().isEmpty()) {
			appointment.setStatus("Booked");
			log.info("Set default status: Booked");
		}
		log.info("PrePersist completed for appointment: {}", appointment.getAppointmentId());
	}

}
