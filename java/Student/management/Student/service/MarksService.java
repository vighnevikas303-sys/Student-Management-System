package Student.management.Student.service;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Student.management.Student.model.Marks;
import Student.management.Student.repository.MarksRepository;


import java.util.List;

@Service
public class MarksService {

    @Autowired
    private MarksRepository marksRepository;



    public List<Marks> getAllMarks() {
        return marksRepository.findAll();
    }

    public List<Marks> getMarksByStudentId(Long studentId) {
        return marksRepository.findByStudentId(studentId);
    }

    public List<Marks> getMarksByStudentAndSemester(Long studentId, String semester) {
        return marksRepository.findByStudentIdAndSemester(studentId, semester);
    }

    public Marks saveMarks(Marks marks) {
        int m = marks.getMarks();
        if (m >= 90)      marks.setGrade("A+");
        else if (m >= 80) marks.setGrade("A");
        else if (m >= 70) marks.setGrade("B");
        else if (m >= 60) marks.setGrade("C");
        else if (m >= 40) marks.setGrade("D");
        else              marks.setGrade("F");
        return marksRepository.save(marks);
    }

    public Marks updateMarks(Long id, Marks updated) {
        return marksRepository.findById(id).map(marks -> {
            marks.setMarks(updated.getMarks());
            marks.setSubject(updated.getSubject());
            marks.setSemester(updated.getSemester());
            marks.setExamType(updated.getExamType());
            int m = updated.getMarks();
            if (m >= 90)      marks.setGrade("A+");
            else if (m >= 80) marks.setGrade("A");
            else if (m >= 70) marks.setGrade("B");
            else if (m >= 60) marks.setGrade("C");
            else if (m >= 40) marks.setGrade("D");
            else              marks.setGrade("F");
            return marksRepository.save(marks);
        }).orElseThrow(() -> new RuntimeException("Marks not found"));
    }

    public void deleteMarks(Long id) {
        marksRepository.deleteById(id);
    }

    public List<Marks> getMarksBySemester(String semester) {
        return marksRepository.findBySemester(semester);
    }
}
