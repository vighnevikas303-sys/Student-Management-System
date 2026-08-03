package Student.management.Student.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import Student.management.Student.model.Attendance;
import Student.management.Student.service.AttendanceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "http://localhost:8081")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<List<Attendance>> getAllAttendance() {
        return ResponseEntity.ok(attendanceService.getAllAttendance());
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Attendance>> getByDate(@PathVariable String date) {
        return ResponseEntity.ok(attendanceService.getAttendanceByDate(date));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Attendance>> getByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByStudentId(studentId));
    }

    @PostMapping
    public ResponseEntity<Attendance> saveAttendance(@RequestBody Attendance attendance) {
        return ResponseEntity.ok(attendanceService.saveAttendance(attendance));
    }

    @PostMapping("/mark")
    public ResponseEntity<Attendance> markAttendance(@RequestBody Map<String, String> body) {
        Long studentId = Long.parseLong(body.get("studentId"));
        String date    = body.get("date");
        String status  = body.get("status");
        return ResponseEntity.ok(attendanceService.markAttendance(studentId, date, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Attendance deleted");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats/{date}")
    public ResponseEntity<Map<String, Object>> getStatsByDate(@PathVariable String date) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("present", attendanceService.getPresentCountByDate(date));
        stats.put("absent",  attendanceService.getAbsentCountByDate(date));
        return ResponseEntity.ok(stats);
    }
}
