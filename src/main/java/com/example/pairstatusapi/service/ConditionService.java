package com.example.pairstatusapi.service;

import com.example.pairstatusapi.entity.ConditionType;
import com.example.pairstatusapi.entity.ConditionUpdateEntity;
import com.example.pairstatusapi.exception.NotFoundException;
import com.example.pairstatusapi.repository.ConditionUpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConditionService {

    private final ConditionUpdateRepository conditionUpdateRepository;
    private final PairService pairService;

    public ConditionUpdateEntity post(UUID userId, ConditionType condition) {
        ConditionUpdateEntity e = new ConditionUpdateEntity();
        e.setUserId(userId);
        e.setCondition(condition);
        return conditionUpdateRepository.save(e);
    }

    public ConditionUpdateEntity getMyLatest(UUID userId) {
        return conditionUpdateRepository
                .findFirstByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new NotFoundException("No condition yet. userId=" + userId));
    }

    public ConditionUpdateEntity getPartnerLatest(UUID myUserId) {
        UUID partnerUserId = pairService.getPartnerUserIdOrThrow(myUserId);

        return conditionUpdateRepository
                .findFirstByUserIdOrderByCreatedAtDesc(partnerUserId)
                .orElseThrow(() -> new NotFoundException("Partner has no condition yet. partnerUserId=" + partnerUserId));
    }
}