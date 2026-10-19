package com.applebay.demo.repository;
import com.applebay.demo.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByNic(String nic);
    boolean existsByEmailIgnoreCase(String email);

    @Query(value = "select nextval('student_number_seq')", nativeQuery = true)
    Long nextStudentSequence();

    @Query("select s from Student s where lower(s.firstName) like concat('%', :q, '%') or lower(s.lastName) like concat('%', :q, '%') or lower(s.studentNumber) like concat('%', :q, '%') or lower(s.nic) like concat('%', :q, '%')")
    Page<Student> search(@Param("q") String q, Pageable pageable);
}
