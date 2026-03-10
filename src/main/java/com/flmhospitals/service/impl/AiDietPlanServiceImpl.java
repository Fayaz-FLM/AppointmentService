package com.flmhospitals.service.impl;

import com.flmhospitals.model.Appointment;
import com.flmhospitals.model.Diagnosis;
import com.flmhospitals.service.AiDietPlanService;
import com.flmhospitals.clients.DoctorClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class AiDietPlanServiceImpl implements AiDietPlanService {

    private final ChatModel chatModel;
    private final DoctorClient doctorClient;

    public AiDietPlanServiceImpl(ChatModel chatModel, DoctorClient doctorClient) {
        this.chatModel = chatModel;
        this.doctorClient = doctorClient;
    }

    @Override
    public String generateDietPlan(Appointment appointment, Diagnosis diagnosis) {

        String specialization = doctorClient.getSpecialization(appointment.getDoctorId());
        String role = formatRole(specialization);

        String promptText = String.format(
            "You are a professional %s.\n" +
            "Generate a detailed 7-day diet plan for the following patient:\n\n" +
            "Reason for Visit: %s\n" +
            "Doctor Diagnosis: %s\n" +
            "Medicines: %s\n\n" +
            "Provide:\n" +
            "1. Recommended foods\n" +
            "2. Foods to avoid\n" +
            "3. Meal timing suggestions\n" +
            "4. Lifestyle tips",
            role,
            appointment.getReasonForVisit(),
            diagnosis.getDiagnosisSummary(),
            diagnosis.getMedicines()
        );

        return chatModel.call(new Prompt(promptText)).getResult().getOutput().getContent();
    }

    private String formatRole(String specialization) {
        if (specialization == null || specialization.isEmpty()) {
            return "clinical nutritionist";
        }
        // Convert CARDIOLOGIST to Cardiologist, etc.
        String formatted = specialization.toLowerCase().replace('_', ' ');
        return formatted;
    }
}