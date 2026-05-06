package Student.management.Student.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Student.management.Student.model.Attendance;
import Student.management.Student.model.Student;
import Student.management.Student.repository.AttendanceRepository;
import Student.management.Student.repository.StudentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudentRepository studentRepository;

    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    public List<Attendance> getAttendanceByDate(String date) {
        return attendanceRepository.findByDate(date);
    }

    public List<Attendance> getAttendanceByStudentId(Long studentId) {
        return attendanceRepository.findByStudentId(studentId);
    }

    public Attendance saveAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    public Attendance markAttendance(Long studentId, String date, String status) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Optional<Attendance> existing = attendanceRepository
                .findAll()
                .stream()
                .filter(a -> a.getStudent().getId().equals(studentId) && a.getDate().equals(date))
                .findFirst();

        if (existing.isPresent()) {
            Attendance att = existing.get();
            att.setStatus(status);
            return attendanceRepository.save(att);
        } else {
            Attendance att = new Attendance();
            att.setStudent(student);
            att.setDate(date);
            att.setStatus(status);
            return attendanceRepository.save(att);
        }
    }

    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }

    public long getPresentCountByDate(String date) {
        return attendanceRepository.findByDateAndStatus(date, "Present").size();
    }

    public long getAbsentCountByDate(String date) {
        return attendanceRepository.findByDateAndStatus(date, "Absent").size();
    }
}
