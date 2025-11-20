package com.healthcare.readmission.model.service;

import com.healthcare.readmission.model.entity.Patient;
import com.healthcare.readmission.model.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    /**
     * Create a new patient
     */
    public Patient createPatient(Patient patient) {
        // Check if patient ID already exists
        if (patient.getPatientId() != null && patientRepository.existsByPatientId(patient.getPatientId())) {
            throw new RuntimeException("Patient ID already exists");
        }

        patient.setCreatedAt(LocalDateTime.now());
        patient.setUpdatedAt(LocalDateTime.now());
        return patientRepository.save(patient);
    }

    /**
     * Find patient by ID (database primary key)
     */
    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id);
    }

    /**
     * Find patient by MIMIC-III patient ID
     */
    public Optional<Patient> findByPatientId(Integer patientId) {
        return patientRepository.findByPatientId(patientId);
    }

    /**
     * Get all patients
     */
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    /**
     * Get patients assigned to a doctor
     */
    public List<Patient> getPatientsByDoctor(Long doctorId) {
        return patientRepository.findByAssignedDoctorId(doctorId);
    }

    /**
     * Search patients by name
     */
    public List<Patient> searchPatientsByName(String name) {
        return patientRepository.findByFullNameContainingIgnoreCase(name);
    }

    /**
     * Update patient
     */
    public Patient updatePatient(Long id, Patient updatedPatient) {
        Optional<Patient> existingPatientOpt = patientRepository.findById(id);
        if (existingPatientOpt.isEmpty()) {
            throw new RuntimeException("Patient not found");
        }

        Patient existingPatient = existingPatientOpt.get();

        // Update fields
        if (updatedPatient.getFullName() != null) {
            existingPatient.setFullName(updatedPatient.getFullName());
        }
        if (updatedPatient.getDateOfBirth() != null) {
            existingPatient.setDateOfBirth(updatedPatient.getDateOfBirth());
        }
        if (updatedPatient.getGender() != null) {
            existingPatient.setGender(updatedPatient.getGender());
        }
        if (updatedPatient.getEmail() != null) {
            existingPatient.setEmail(updatedPatient.getEmail());
        }
        if (updatedPatient.getPhone() != null) {
            existingPatient.setPhone(updatedPatient.getPhone());
        }
        if (updatedPatient.getAddress() != null) {
            existingPatient.setAddress(updatedPatient.getAddress());
        }
        if (updatedPatient.getAssignedDoctorId() != null) {
            existingPatient.setAssignedDoctorId(updatedPatient.getAssignedDoctorId());
        }

        existingPatient.setUpdatedAt(LocalDateTime.now());
        return patientRepository.save(existingPatient);
    }

    /**
     * Assign doctor to patient
     */
    public Patient assignDoctor(Long patientId, Long doctorId) {
        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        if (patientOpt.isEmpty()) {
            throw new RuntimeException("Patient not found");
        }

        Patient patient = patientOpt.get();
        patient.setAssignedDoctorId(doctorId);
        patient.setUpdatedAt(LocalDateTime.now());
        return patientRepository.save(patient);
    }

    /**
     * Delete patient
     */
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
}
