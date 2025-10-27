package com.flmhospitals.dao;

import com.flmhospitals.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {
	@Query(value = "SELECT appointment_id FROM appointments ORDER BY appointment_id DESC LIMIT 1", nativeQuery = true)
    String findLastAppointmentId();


}
