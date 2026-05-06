package Student.management.Student.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Student.management.Student.model.Fee;
import Student.management.Student.repository.FeeRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FeeService {

    @Autowired
    private FeeRepository feeRepository;

    public List<Fee> getAllFees() {
        return feeRepository.findAll();
    }

    public List<Fee> getFeesByStudentId(Long studentId) {
        return feeRepository.findByStudentId(studentId);
    }

    public Fee saveFee(Fee fee) {
        fee.setBalance(fee.getTotalAmount() - fee.getPaidAmount());
        if (fee.getPaidAmount() >= fee.getTotalAmount())       fee.setStatus("Paid");
        else if (fee.getPaidAmount() > 0)                      fee.setStatus("Partial");
        else                                                   fee.setStatus("Pending");
        return feeRepository.save(fee);
    }

    public Fee updateFee(Long id, Fee updated) {
        return feeRepository.findById(id).map(fee -> {
            fee.setPaidAmount(updated.getPaidAmount());
            fee.setTotalAmount(updated.getTotalAmount());
            fee.setBalance(updated.getTotalAmount() - updated.getPaidAmount());
            fee.setPaymentDate(updated.getPaymentDate());
            fee.setDueDate(updated.getDueDate());
            if (updated.getPaidAmount() >= updated.getTotalAmount())  fee.setStatus("Paid");
            else if (updated.getPaidAmount() > 0)                     fee.setStatus("Partial");
            else                                                      fee.setStatus("Pending");
            return feeRepository.save(fee);
        }).orElseThrow(() -> new RuntimeException("Fee record not found"));
    }

    public void deleteFee(Long id) {
        feeRepository.deleteById(id);
    }

    public List<Fee> getFeesByStatus(String status) {
        return feeRepository.findByStatus(status);
    }

    public Double getTotalCollected() {
        Double total = feeRepository.getTotalCollected();
        return total != null ? total : 0.0;
    }

    public Double getTotalPending() {
        Double total = feeRepository.getTotalPending();
        return total != null ? total : 0.0;
    }

    public long countByStatus(String status) {
        return feeRepository.countByStatus(status);
    }
}
