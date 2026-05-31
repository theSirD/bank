package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CardRepository extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {

    Page<Card> findByOwnerId(Long ownerId, Pageable pageable);

    Page<Card> findByOwnerIdAndStatus(Long ownerId, CardStatus status, Pageable pageable);

    Optional<Card> findByIdAndOwnerId(Long id, Long ownerId);

    Page<Card> findByOwnerIdAndPanLast4Containing(Long ownerId, String panLast4, Pageable pageable);
}
