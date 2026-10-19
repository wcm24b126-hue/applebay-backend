package com.applebay.demo.repository;
import com.applebay.demo.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.enrollment.id = :enrollmentId")
    BigDecimal sumByEnrollmentId(@Param("enrollmentId") Long enrollmentId);

    List<Payment> findByEnrollmentIdOrderByPaymentDateDescIdDesc(Long enrollmentId);

    @Query(value = "select nextval('receipt_number_seq')", nativeQuery = true)
    Long nextReceiptSequence();

    @Query("select coalesce(sum(p.amount), 0) from Payment p")
    BigDecimal totalCollected();
}
