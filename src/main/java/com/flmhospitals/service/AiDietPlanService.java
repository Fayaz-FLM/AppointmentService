package com.flmhospitals.service;

import com.flmhospitals.model.Appointment;
import com.flmhospitals.model.Diagnosis;

public interface AiDietPlanService {
    String generateDietPlan(Appointment appointment, Diagnosis diagnosis);
}
