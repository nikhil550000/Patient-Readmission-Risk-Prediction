package com.healthcare.readmission.config;

import com.healthcare.readmission.auth.entity.User;
import com.healthcare.readmission.auth.service.UserService;
import com.healthcare.readmission.model.entity.Patient;
import com.healthcare.readmission.model.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private PatientService patientService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n=== Initializing Database with Sample Data ===\n");

        // Check if data already exists
        if (userService.getAllUsers().size() > 0) {
            System.out.println("✓ Database already initialized. Skipping sample data creation.");
            return;
        }

        // Create sample users
        createSampleUsers();

        // Create sample patients
        createSamplePatients();

        System.out.println("\n=== Database Initialization Complete ===\n");
    }

    private void createSampleUsers() {
        System.out.println("Creating sample users...");

        try {
            // Admin user
            User admin = userService.createUser(
                    "admin",
                    "admin123",
                    "admin@hospital.com",
                    "ADMIN",
                    "System Administrator");
            System.out.println("✓ Created admin user: " + admin.getUsername());

            // Doctor users
            User doctor1 = userService.createUser(
                    "dr.smith",
                    "doctor123",
                    "dr.smith@hospital.com",
                    "DOCTOR",
                    "Dr. John Smith");
            System.out.println("✓ Created doctor: " + doctor1.getFullName());

            User doctor2 = userService.createUser(
                    "dr.jones",
                    "doctor123",
                    "dr.jones@hospital.com",
                    "DOCTOR",
                    "Dr. Sarah Jones");
            System.out.println("✓ Created doctor: " + doctor2.getFullName());

            // Patient user (for patient portal access)
            User patient = userService.createUser(
                    "patient1",
                    "patient123",
                    "patient1@email.com",
                    "PATIENT",
                    "John Doe");
            System.out.println("✓ Created patient user: " + patient.getFullName());

        } catch (Exception e) {
            System.err.println("Error creating users: " + e.getMessage());
        }
    }

    private void createSamplePatients() {
        System.out.println("\nCreating sample patients...");

        try {
            // Patient 1
            Patient patient1 = new Patient();
            patient1.setPatientId(10001);
            patient1.setFullName("John Doe");
            patient1.setDateOfBirth(LocalDate.of(1965, 5, 15));
            patient1.setGender("M");
            patient1.setEmail("john.doe@email.com");
            patient1.setPhone("555-0101");
            patient1.setAddress("123 Main St, Boston, MA 02101");
            patient1.setAssignedDoctorId(2L); // Dr. Smith
            patientService.createPatient(patient1);
            System.out.println("✓ Created patient: " + patient1.getFullName());

            // Patient 2
            Patient patient2 = new Patient();
            patient2.setPatientId(10002);
            patient2.setFullName("Jane Smith");
            patient2.setDateOfBirth(LocalDate.of(1972, 8, 22));
            patient2.setGender("F");
            patient2.setEmail("jane.smith@email.com");
            patient2.setPhone("555-0102");
            patient2.setAddress("456 Oak Ave, Boston, MA 02102");
            patient2.setAssignedDoctorId(2L); // Dr. Smith
            patientService.createPatient(patient2);
            System.out.println("✓ Created patient: " + patient2.getFullName());

            // Patient 3
            Patient patient3 = new Patient();
            patient3.setPatientId(10003);
            patient3.setFullName("Robert Johnson");
            patient3.setDateOfBirth(LocalDate.of(1958, 11, 30));
            patient3.setGender("M");
            patient3.setEmail("robert.j@email.com");
            patient3.setPhone("555-0103");
            patient3.setAddress("789 Elm St, Boston, MA 02103");
            patient3.setAssignedDoctorId(3L); // Dr. Jones
            patientService.createPatient(patient3);
            System.out.println("✓ Created patient: " + patient3.getFullName());

            // Patient 4
            Patient patient4 = new Patient();
            patient4.setPatientId(10004);
            patient4.setFullName("Mary Williams");
            patient4.setDateOfBirth(LocalDate.of(1980, 3, 12));
            patient4.setGender("F");
            patient4.setEmail("mary.w@email.com");
            patient4.setPhone("555-0104");
            patient4.setAddress("321 Pine Rd, Boston, MA 02104");
            patient4.setAssignedDoctorId(3L); // Dr. Jones
            patientService.createPatient(patient4);
            System.out.println("✓ Created patient: " + patient4.getFullName());

            // Patient 5
            Patient patient5 = new Patient();
            patient5.setPatientId(10005);
            patient5.setFullName("Michael Brown");
            patient5.setDateOfBirth(LocalDate.of(1945, 7, 8));
            patient5.setGender("M");
            patient5.setEmail("michael.b@email.com");
            patient5.setPhone("555-0105");
            patient5.setAddress("654 Maple Dr, Boston, MA 02105");
            patient5.setAssignedDoctorId(2L); // Dr. Smith
            patientService.createPatient(patient5);
            System.out.println("✓ Created patient: " + patient5.getFullName());

        } catch (Exception e) {
            System.err.println("Error creating patients: " + e.getMessage());
        }
    }
}
