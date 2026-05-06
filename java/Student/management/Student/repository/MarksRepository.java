package Student.management.Student.repository;




import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Student.management.Student.model.Marks;

import java.util.List;

@Repository
public interface MarksRepository extends JpaRepository<Marks, Long> {

    List<Marks> findByStudentId(Long studentId);

    List<Marks> findByStudentIdAndSemester(Long studentId, String semester);

    List<Marks> findBySemester(String semester);

    List<Marks> findBySubject(String subject);
}
