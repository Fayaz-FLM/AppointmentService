package com.flmhospitals.Generator;



import com.flmhospitals.dao.AppointmentRepository;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class AppointmentIdGenerator {

	private final AppointmentRepository appointmentRepository;

    public AppointmentIdGenerator(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public String generateNextAppointmentId() {
        String prefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String lastId = appointmentRepository.findLastAppointmentId();

        int nextNumber = 1;

        if (lastId != null && lastId.length() > 14) {
            String numberPart = lastId.substring(14); 
            nextNumber = Integer.parseInt(numberPart) + 1;
        }

        String suffix = String.format("%05d", nextNumber); 
        return prefix + suffix;
    }
}


