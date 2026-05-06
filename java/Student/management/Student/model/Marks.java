package Student.management.Student.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "marks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Marks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "subject")
    private String subject;

    @Column(name = "marks")
    private Integer marks;

    @Column(name = "max_marks")
    private Integer maxMarks = 100;

    @Column(name = "semester")
    private String semester;

    @Column(name = "grade")
    private String grade;

    @Column(name = "exam_type")
    private String examType; // Internal, External, Final
}
