package com.flmhospitals.Generator;

import com.flmhospitals.dao.AppointmentRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AppointmentIdGenerator {
	private final AppointmentRepository appointmentRepository;

	public AppointmentIdGenerator(AppointmentRepository appointmentRepository) {
		this.appointmentRepository = appointmentRepository;
	}

	public String generateNextAppointmentId() {
		log.info("Generating new appointment ID");
		String prefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		String lastId = appointmentRepository.findLastAppointmentId();
		log.info("Last appointment ID from database: {}", lastId);

		int nextNumber = 1;

		if (lastId != null && lastId.length() > 14) {
			String lastIdDate = lastId.substring(0, 14);

			if (prefix.equals(lastIdDate)) {
				String numberPart = lastId.substring(14);
				nextNumber = Integer.parseInt(numberPart) + 1;

				String suffix = String.format("%05d", nextNumber);
				String newId = prefix + suffix;
				log.info("Generated appointment ID (incremented): {}", newId);
				return newId;
			} else {
				int firstPatientNumber = 1;
				String suffix = String.format("%05d", firstPatientNumber);
				String newId = prefix + suffix;
				log.info("Generated appointment ID (new timestamp): {}", newId);
				return newId;
			}
		}

		String suffix = String.format("%05d", nextNumber);
		String newId = prefix + suffix;
		log.info("Generated appointment ID (first ever): {}", newId);
		return newId;
	}
}
