package service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import entity.Bill;
import entity.Payment;
import exception.ResourceNotFoundException;
import repository.BillRepository;
import repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BillRepository billRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            BillRepository billRepository) {

        this.paymentRepository = paymentRepository;
        this.billRepository = billRepository;
    }

    @Transactional
    public Payment recordPayment(
            Long billId,
            Payment payment) {

        if (payment == null) {
            throw new IllegalArgumentException(
                    "Payment data is required."
            );
        }

        Bill bill =
                billRepository.findById(billId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Bill not found with ID: "
                                                + billId
                                )
                        );

        BigDecimal amount = payment.getAmount();

        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero."
            );
        }

        BigDecimal paidAmount =
                bill.getPaidAmount() == null
                        ? BigDecimal.ZERO
                        : bill.getPaidAmount();

        BigDecimal remainingAmount =
                bill.getTotalAmount()
                        .subtract(paidAmount);

        if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "This bill is already fully paid."
            );
        }

        if (amount.compareTo(remainingAmount) > 0) {

            throw new IllegalArgumentException(
                    "Payment exceeds the remaining bill amount."
            );
        }

        if (payment.getPaymentMethod() == null
                || payment.getPaymentMethod().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Payment method is required."
            );
        }

        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(
                    LocalDateTime.now()
            );
        }

        if (payment.getPaymentDate()
                .isAfter(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "Payment date cannot be in the future."
            );
        }

        payment.setPaymentMethod(
                payment.getPaymentMethod().trim()
        );

        payment.setStatus("SUCCESS");
        payment.setBill(bill);

        Payment savedPayment =
                paymentRepository.save(payment);

        BigDecimal updatedPaidAmount =
                paidAmount.add(amount);

        bill.setPaidAmount(updatedPaidAmount);

        if (updatedPaidAmount.compareTo(
                bill.getTotalAmount()) >= 0) {

            bill.setPaymentStatus("PAID");

        } else {

            bill.setPaymentStatus("PARTIAL");
        }

        bill.setPaymentMethod(
                payment.getPaymentMethod()
        );

        billRepository.save(bill);

        return savedPayment;
    }

    public List<Payment> getAllPayments() {

        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {

        return paymentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found with ID: "
                                        + id
                        )
                );
    }

    public List<Payment> getPaymentsByBill(
            Long billId) {

        if (!billRepository.existsById(billId)) {

            throw new ResourceNotFoundException(
                    "Bill not found with ID: "
                            + billId
            );
        }

        return paymentRepository.findByBillId(billId);
    }

    public List<Payment> getPaymentsByStatus(
            String status) {

        if (status == null
                || status.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Payment status is required."
            );
        }

        return paymentRepository
                .findByStatusIgnoreCase(
                        status.trim()
                );
    }

    @Transactional
    public void deletePayment(Long id) {

        Payment payment =
                getPaymentById(id);

        Bill bill = payment.getBill();

        if (bill != null) {

            BigDecimal currentPaidAmount =
                    bill.getPaidAmount() == null
                            ? BigDecimal.ZERO
                            : bill.getPaidAmount();

            BigDecimal paymentAmount =
                    payment.getAmount() == null
                            ? BigDecimal.ZERO
                            : payment.getAmount();

            BigDecimal updatedPaidAmount =
                    currentPaidAmount
                            .subtract(paymentAmount);

            if (updatedPaidAmount.compareTo(
                    BigDecimal.ZERO) < 0) {

                updatedPaidAmount =
                        BigDecimal.ZERO;
            }

            bill.setPaidAmount(
                    updatedPaidAmount
            );

            if (updatedPaidAmount.compareTo(
                    BigDecimal.ZERO) == 0) {

                bill.setPaymentStatus("PENDING");

            } else if (updatedPaidAmount.compareTo(
                    bill.getTotalAmount()) >= 0) {

                bill.setPaymentStatus("PAID");

            } else {

                bill.setPaymentStatus("PARTIAL");
            }

            billRepository.save(bill);
        }

        paymentRepository.delete(payment);
    }
}