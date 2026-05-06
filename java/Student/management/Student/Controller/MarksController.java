package Student.management.Student.Controller;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import Student.management.Student.model.Marks;
import Student.management.Student.service.MarksService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/marks")
@CrossOrigin(origins = "*")
public class MarksController {

    @Autowired
    private MarksService marksService;

    @GetMapping
    public ResponseEntity<List<Marks>> getAllMarks() {
        return ResponseEntity.ok(marksService.getAllMarks());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Marks>> getByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(marksService.getMarksByStudentId(studentId));
    }

    @GetMapping("/student/{studentId}/semester/{semester}")
    public ResponseEntity<List<Marks>> getByStudentAndSemester(@PathVariable Long studentId,
                                                                @PathVariable String semester) {
        return ResponseEntity.ok(marksService.getMarksByStudentAndSemester(studentId, semester));
    }

    @PostMapping
    public ResponseEntity<Marks> addMarks(@RequestBody Marks marks) {
        return ResponseEntity.status(HttpStatus.CREATED).body(marksService.saveMarks(marks));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Marks> updateMarks(@PathVariable Long id, @RequestBody Marks marks) {
        try {
            return ResponseEntity.ok(marksService.updateMarks(id, marks));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteMarks(@PathVariable Long id) {
        marksService.deleteMarks(id);
        Map<String, String> res = new HashMap<>();
        res.put("message", "Marks deleted");
        return ResponseEntity.ok(res);
    }
}
