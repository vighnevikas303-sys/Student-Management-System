package Student.management.Student.model;



import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "roll_number", unique = true)
    private String rollNumber;

    @Email(message = "Enter a valid email")
    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "course")
    private String course;

    @Column(name = "class_name")
    private String className;

    @Column(name = "semester")
    private String semester;

    @Column(name = "gender")
    private String gender;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "admission_year")
    private String admissionYear;

    @Column(name = "fee_status")
    private String feeStatus = "Pending";

    @Column(name = "status")
    private String status = "Active";
}

