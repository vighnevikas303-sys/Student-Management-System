package Student.management.Student.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Student.management.Student.model.Student;
import Student.management.Student.repository.StudentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student updateStudent(Long id, Student updatedStudent) {
        return studentRepository.findById(id).map(student -> {
            student.setFirstName(updatedStudent.getFirstName());
            student.setLastName(updatedStudent.getLastName());
            student.setRollNumber(updatedStudent.getRollNumber());
            student.setEmail(updatedStudent.getEmail());
            student.setPhone(updatedStudent.getPhone());
            student.setCourse(updatedStudent.getCourse());
            student.setClassName(updatedStudent.getClassName());
            student.setSemester(updatedStudent.getSemester());
            student.setGender(updatedStudent.getGender());
            student.setDateOfBirth(updatedStudent.getDateOfBirth());
            student.setAddress(updatedStudent.getAddress());
            student.setAdmissionYear(updatedStudent.getAdmissionYear());
            student.setFeeStatus(updatedStudent.getFeeStatus());
            student.setStatus(updatedStudent.getStatus());
            return studentRepository.save(student);
        }).orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    public List<Student> searchStudents(String keyword) {
        return studentRepository.searchStudents(keyword);
    }

    public List<Student> getStudentsByCourse(String course) {
        return studentRepository.findByCourse(course);
    }

    public long getTotalStudents() {
        return studentRepository.count();
    }

    public long getActiveStudents() {
        return studentRepository.countByStatus("Active");
    }
}
