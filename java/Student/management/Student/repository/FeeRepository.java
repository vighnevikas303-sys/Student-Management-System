package Student.management.Student.repository;




import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import Student.management.Student.model.Fee;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Long> {

    List<Fee> findByStudentId(Long studentId);

    List<Fee> findByStatus(String status);

    Optional<Fee> findByStudentIdAndAcademicYear(Long studentId, String academicYear);

    @Query("SELECT SUM(f.paidAmount) FROM Fee f WHERE f.status = 'Paid' OR f.status = 'Partial'")
    Double getTotalCollected();

    @Query("SELECT SUM(f.balance) FROM Fee f WHERE f.status = 'Pending' OR f.status = 'Partial'")
    Double getTotalPending();

    long countByStatus(String status);
}
