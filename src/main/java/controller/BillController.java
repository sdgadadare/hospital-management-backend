
package controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import entity.Bill;
import service.BillService;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "http://localhost:5173")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    // Generate a new bill
    @PostMapping
    public Bill generateBill(@RequestBody Bill bill) {
        return billService.generateBill(bill);
    }

    // Get all bills
    @GetMapping
    public List<Bill> getAllBills() {
        return billService.getAllBills();
    }

    // Get bill by ID
    @GetMapping("/{id}")
    public Bill getBillById(@PathVariable Long id) {
        return billService.getBillById(id);
    }

    // Record a payment
    @PutMapping("/{id}/payment")
    public Bill recordPayment(
            @PathVariable Long id,
            @RequestParam BigDecimal amount,
            @RequestParam String paymentMethod) {

        return billService.recordPayment(id, amount, paymentMethod);
    }

    // Delete bill
    @DeleteMapping("/{id}")
    public String deleteBill(@PathVariable Long id) {
        billService.deleteBill(id);
        return "Bill deleted successfully";
    }
}
