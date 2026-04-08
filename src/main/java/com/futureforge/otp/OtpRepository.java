package com.futureforge.otp;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface OtpRepository extends JpaRepository<OtpEntity, Long> {

    // ✅ ALWAYS use this (latest OTP only)
    Optional<OtpEntity> findTopByEmailOrderByExpiryTimeDesc(String email);

    // ✅ delete old OTPs before inserting new one
    void deleteByEmail(String email);
}