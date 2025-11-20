package com.healthcare.readmission.model.repository;

import com.healthcare.readmission.model.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Find patient by MIMIC-III patient ID
    Optional<Patient> findByPatientId(Integer patientId);

    // Find patients assigned to a doctor
    List<Patient> findByAssignedDoctorId(Long doctorId);

    // Find patients by gender
    List<Patient> findByGender(String gender);

    // Search patients by name (case-insensitive, partial match)
    List<Patient> findByFullNameContainingIgnoreCase(String name);

    // Check if patient ID exists
    boolean existsByPatientId(Integer patientId);
}
