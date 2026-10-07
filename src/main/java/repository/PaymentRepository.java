
package repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByBillId(Long billId);

    List<Payment> findByStatusIgnoreCase(String status);
}
