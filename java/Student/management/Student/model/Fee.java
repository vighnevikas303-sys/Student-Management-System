package Student.management.Student.model;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fee")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "paid_amount")
    private Double paidAmount = 0.0;

    @Column(name = "balance")
    private Double balance;

    @Column(name = "due_date")
    private String dueDate;

    @Column(name = "payment_date")
    private String paymentDate;

    @Column(name = "status")
    private String status = "Pending"; 

    @Column(name = "academic_year")
    private String academicYear;
}

