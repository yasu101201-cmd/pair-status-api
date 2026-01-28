package com.example.pairstatusapi.service;

import com.example.pairstatusapi.entity.*;
import com.example.pairstatusapi.exception.NotFoundException;
import com.example.pairstatusapi.repository.ConditionUpdateRepository;
import lombok.RequiredArgsConstructor;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConditionService {

    private final ConditionUpdateRepository conditionUpdateRepository;
    private final PairService pairService;

    // ★ これ1本に統一
    public ConditionUpdateEntity post(
            UUID userId,
            MainCondition mainCondition,
            SubCondition subCondition,
            String note
    ) {
        ConditionUpdateEntity e = new ConditionUpdateEntity();
        e.setUserId(userId);
        e.setMainCondition(mainCondition);
        e.setSubCondition(subCondition);
        e.setNote(note);
        return conditionUpdateRepository.save(e);
    }

    public ConditionUpdateEntity getMyLatest(UUID userId) {
        return conditionUpdateRepository
                .findFirstByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() ->
                        new NotFoundException("No condition yet. userId=" + userId)
                );
    }

    public ConditionUpdateEntity getPartnerLatest(UUID myUserId) {
        UUID partnerUserId = pairService.getPartnerUserIdOrThrow(myUserId);

        return conditionUpdateRepository
                .findFirstByUserIdOrderByCreatedAtDesc(partnerUserId)
                .orElseThrow(() ->
                        new NotFoundException("Partner has no condition yet. partnerUserId=" + partnerUserId)
                );
    }




public List<ConditionUpdateEntity> getTalkHistory(UUID myUserId) {
    // 自分
    List<ConditionUpdateEntity> mine =
            conditionUpdateRepository.findByUserIdInOrderByCreatedAtDesc(List.of(myUserId));

    // 相手（ペア前なら例外＝404で返す想定）
    UUID partnerUserId = pairService.getPartnerUserIdOrThrow(myUserId);
    List<ConditionUpdateEntity> partner =
            conditionUpdateRepository.findByUserIdInOrderByCreatedAtDesc(List.of(partnerUserId));

    // 混ぜる
    List<ConditionUpdateEntity> all = new ArrayList<>();
    all.addAll(mine);
    all.addAll(partner);

    // createdAt の新しい順（表示は後で逆にしてもOK）
    all.sort(Comparator.comparing(ConditionUpdateEntity::getCreatedAt).reversed());
    return all;
}

public List<ConditionUpdateEntity> getTalk(UUID myUserId) {
    UUID partnerId = pairService.getPartnerUserIdOrThrow(myUserId);

    List<ConditionUpdateEntity> list =
            conditionUpdateRepository.findByUserIdInOrderByCreatedAtDesc(
                    List.of(myUserId, partnerId)
            );

    // ✅ 古い → 新しい に並び替え
    Collections.reverse(list);

    return list;
}
}