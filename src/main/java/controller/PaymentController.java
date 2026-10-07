
package controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import entity.Payment;
import service.PaymentService;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/bill/{billId}")
    public ResponseEntity<Payment> recordPayment(
            @PathVariable Long billId,
            @RequestBody Payment payment) {

        Payment savedPayment =
                paymentService.recordPayment(billId, payment);

        return new ResponseEntity<>(savedPayment, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(
                paymentService.getAllPayments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                paymentService.getPaymentById(id));
    }

    @GetMapping("/bill/{billId}")
    public ResponseEntity<List<Payment>> getPaymentsByBill(
            @PathVariable Long billId) {

        return ResponseEntity.ok(
                paymentService.getPaymentsByBill(billId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                paymentService.getPaymentsByStatus(status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(
            @PathVariable Long id) {

        paymentService.deletePayment(id);

        return ResponseEntity.noContent().build();
    }
}
