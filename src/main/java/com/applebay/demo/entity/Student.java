package com.applebay.demo.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "students")
@Getter @Setter @NoArgsConstructor
public class Student {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "student_number", nullable = false, unique = true, length = 20)
    private String studentNumber;
    @Column(name = "first_name", nullable = false, length = 60)
    private String firstName;
    @Column(name = "last_name", nullable = false, length = 60)
    private String lastName;
    @Column(nullable = false, unique = true, length = 12)
    private String nic;
    @Column(nullable = false, unique = true, length = 120)
    private String email;
    @Column(nullable = false, length = 15)
    private String phone;
    @Column(nullable = false, length = 255)
    private String address;
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;
}
