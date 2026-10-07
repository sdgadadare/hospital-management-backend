
package controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import repository.PatientRepository;
import repository.DoctorRepository;
import repository.AppointmentRepository;
import repository.BillRepository;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
public class DashboardController {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final BillRepository billRepository;

    public DashboardController(
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            BillRepository billRepository) {

        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.billRepository = billRepository;
    }

    // Get hospital dashboard statistics
    @GetMapping("/stats")
    public Map<String, Object> getDashboardStats() {

        Map<String, Object> stats = new HashMap<>();

        long totalPatients = patientRepository.count();
        long totalDoctors = doctorRepository.count();
        long totalAppointments = appointmentRepository.count();

        BigDecimal totalRevenue = billRepository.findAll()
                .stream()
                .map(bill -> bill.getPaidAmount() == null
                        ? BigDecimal.ZERO
                        : bill.getPaidAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.put("totalPatients", totalPatients);
        stats.put("totalDoctors", totalDoctors);
        stats.put("totalAppointments", totalAppointments);
        stats.put("totalRevenue", totalRevenue);

        return stats;
    }
}
