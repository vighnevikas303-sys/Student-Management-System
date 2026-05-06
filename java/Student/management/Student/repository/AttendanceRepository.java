package Student.management.Student.repository;




import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import Student.management.Student.model.Attendance;
import Student.management.Student.model.Student;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByDate(String date);

    List<Attendance> findByStudent(Student student);

    List<Attendance> findByStudentId(Long studentId);

    List<Attendance> findByDateAndStatus(String date, String status);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.status = 'Present'")
    long countPresentByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId")
    long countTotalByStudentId(@Param("studentId") Long studentId);

    boolean existsByStudentIdAndDate(Long studentId, String date);
}
