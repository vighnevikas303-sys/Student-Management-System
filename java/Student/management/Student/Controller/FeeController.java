package Student.management.Student.Controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import Student.management.Student.model.Fee;
import Student.management.Student.service.FeeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fee")
@CrossOrigin(origins = "*")
public class FeeController {

    @Autowired
    private FeeService feeService;

    @GetMapping
    public ResponseEntity<List<Fee>> getAllFees() {
        return ResponseEntity.ok(feeService.getAllFees());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Fee>> getFeeByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(feeService.getFeesByStudentId(studentId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Fee>> getFeeByStatus(@PathVariable String status) {
        return ResponseEntity.ok(feeService.getFeesByStatus(status));
    }

    @PostMapping
    public ResponseEntity<Fee> addFee(@RequestBody Fee fee) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feeService.saveFee(fee));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Fee> updateFee(@PathVariable Long id, @RequestBody Fee fee) {
        try {
            return ResponseEntity.ok(feeService.updateFee(id, fee));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteFee(@PathVariable Long id) {
        feeService.deleteFee(id);
        Map<String, String> res = new HashMap<>();
        res.put("message", "Fee record deleted");
        return ResponseEntity.ok(res);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getFeeSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalCollected", feeService.getTotalCollected());
        summary.put("totalPending",   feeService.getTotalPending());
        summary.put("paidCount",      feeService.countByStatus("Paid"));
        summary.put("pendingCount",   feeService.countByStatus("Pending"));
        summary.put("partialCount",   feeService.countByStatus("Partial"));
        return ResponseEntity.ok(summary);
    }
}

