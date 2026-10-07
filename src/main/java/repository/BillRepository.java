package repository;

import org.springframework.data.jpa.repository.JpaRepository;

import entity.Bill;

public interface BillRepository extends JpaRepository<Bill, Long> {

    boolean existsByAppointmentId(Long appointmentId);
}