package com.sorokaandriy.auth_service.repository;

import com.sorokaandriy.auth_service.entity.VarificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VarificationTokenRepository extends JpaRepository<VarificationToken, UUID> {
}
