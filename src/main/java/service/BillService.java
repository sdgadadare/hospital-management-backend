package service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import entity.Appointment;
import entity.Bill;
import exception.ResourceNotFoundException;
import repository.AppointmentRepository;
import repository.BillRepository;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final AppointmentRepository appointmentRepository;

    public BillService(
            BillRepository billRepository,
            AppointmentRepository appointmentRepository) {

        this.billRepository = billRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public Bill generateBill(Bill bill) {

        if (bill == null) {
            throw new IllegalArgumentException("Bill data is required.");
        }

        if (bill.getAppointment() == null
                || bill.getAppointment().getId() == null) {

            throw new IllegalArgumentException(
                    "Appointment is required."
            );
        }

        if (bill.getTotalAmount() == null) {
            throw new IllegalArgumentException(
                    "Total amount is required."
            );
        }

        if (bill.getTotalAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Total amount cannot be negative."
            );
        }

        Long appointmentId = bill.getAppointment().getId();

        Appointment appointment =
                appointmentRepository.findById(appointmentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Appointment not found with ID: "
                                                + appointmentId
                                )
                        );

        if (billRepository.existsByAppointmentId(appointmentId)) {
            throw new IllegalArgumentException(
                    "A bill already exists for this appointment."
            );
        }

        BigDecimal paidAmount = bill.getPaidAmount();

        if (paidAmount == null) {
            paidAmount = BigDecimal.ZERO;
        }

        if (paidAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Paid amount cannot be negative."
            );
        }

        if (paidAmount.compareTo(bill.getTotalAmount()) > 0) {
            throw new IllegalArgumentException(
                    "Paid amount cannot exceed the total bill amount."
            );
        }

        if (bill.getBillingDate() == null) {
            bill.setBillingDate(LocalDate.now());
        }

        if (bill.getBillingDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Billing date cannot be in the future."
            );
        }

        bill.setAppointment(appointment);
        bill.setPaidAmount(paidAmount);

        if (bill.getPaymentMethod() != null) {
            bill.setPaymentMethod(
                    bill.getPaymentMethod().trim()
            );
        }

        updatePaymentStatus(bill);

        return billRepository.save(bill);
    }

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    public Bill getBillById(Long id) {

        return billRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Bill not found with ID: " + id
                        )
                );
    }

    @Transactional
    public Bill recordPayment(
            Long id,
            BigDecimal amount,
            String paymentMethod) {

        Bill bill = getBillById(id);

        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero."
            );
        }

        BigDecimal currentPaidAmount =
                bill.getPaidAmount() == null
                        ? BigDecimal.ZERO
                        : bill.getPaidAmount();

        BigDecimal remainingAmount =
                bill.getTotalAmount()
                        .subtract(currentPaidAmount);

        if (amount.compareTo(remainingAmount) > 0) {
            throw new IllegalArgumentException(
                    "Payment exceeds the remaining bill amount."
            );
        }

        if (paymentMethod == null
                || paymentMethod.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Payment method is required."
            );
        }

        BigDecimal newPaidAmount =
                currentPaidAmount.add(amount);

        bill.setPaidAmount(newPaidAmount);
        bill.setPaymentMethod(
                paymentMethod.trim()
        );

        updatePaymentStatus(bill);

        return billRepository.save(bill);
    }

    private void updatePaymentStatus(Bill bill) {

        BigDecimal paidAmount =
                bill.getPaidAmount() == null
                        ? BigDecimal.ZERO
                        : bill.getPaidAmount();

        BigDecimal totalAmount =
                bill.getTotalAmount();

        if (paidAmount.compareTo(BigDecimal.ZERO) == 0) {

            bill.setPaymentStatus("PENDING");

        } else if (paidAmount.compareTo(totalAmount) >= 0) {

            bill.setPaymentStatus("PAID");

        } else {

            bill.setPaymentStatus("PARTIAL");
        }
    }

    public void deleteBill(Long id) {

        Bill bill = getBillById(id);

        billRepository.delete(bill);
    }
}