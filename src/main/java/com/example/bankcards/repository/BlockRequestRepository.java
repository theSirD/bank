package com.example.bankcards.repository;

import com.example.bankcards.entity.BlockRequest;
import com.example.bankcards.entity.BlockRequestStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRequestRepository extends JpaRepository<BlockRequest, Long> {

    Optional<BlockRequest> findByCardIdAndStatus(Long cardId, BlockRequestStatus status);

    Page<BlockRequest> findByStatus(BlockRequestStatus status, Pageable pageable);

    List<BlockRequest> findByCardId(Long cardId);
}
