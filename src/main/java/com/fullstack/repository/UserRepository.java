package com.fullstack.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fullstack.model.UserInfm;

@Repository
public interface UserRepository extends JpaRepository<UserInfm, Long> {
    Optional<UserInfm> findByUserId(String userId);
    Optional<UserInfm> findByEml(String eml);
} 