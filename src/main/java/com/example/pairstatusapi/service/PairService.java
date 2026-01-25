package com.example.pairstatusapi.service;

import com.example.pairstatusapi.dto.PairStatusResponse;
import com.example.pairstatusapi.entity.PairEntity;
import com.example.pairstatusapi.entity.PairEntity.PairState;
import com.example.pairstatusapi.entity.UserEntity;
import com.example.pairstatusapi.exception.ConflictException;
import com.example.pairstatusapi.exception.NotFoundException;
import com.example.pairstatusapi.repository.PairRepository;
import com.example.pairstatusapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PairService {

    private final PairRepository pairRepository;
    private final UserRepository userRepository;

    private static final SecureRandom RANDOM = new SecureRandom();
    // 0,O,1,I,l などを除外
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int JOIN_CODE_LENGTH = 8;

    // ① ペア作成：joinCode発行して、作成者userにpairIdをセット
    @Transactional
    public PairEntity create(UUID userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        if (user.getPairId() != null) {
            throw new ConflictException("User already paired: " + userId);
        }

        for (int i = 0; i < 10; i++) {
            PairEntity pair = new PairEntity();
            pair.setJoinCode(generateJoinCode());
            pair.setUserId1(userId);                 // ★必須
            pair.setState(PairState.WAITING);        // ★必須

            try {
                PairEntity savedPair = pairRepository.save(pair);

                user.setPairId(savedPair.getId());
                userRepository.save(user);

                return savedPair;

            } catch (DataIntegrityViolationException e) {
                // joinCode衝突 or 制約違反 → 作り直す
            }
        }

        throw new ConflictException("joinCode collision: too many retries");
    }

    // joinCode生成（人に優しい英数字）
    private String generateJoinCode() {
        StringBuilder sb = new StringBuilder(JOIN_CODE_LENGTH);
        for (int i = 0; i < JOIN_CODE_LENGTH; i++) {
            int idx = RANDOM.nextInt(CODE_CHARS.length());
            sb.append(CODE_CHARS.charAt(idx));
        }
        return sb.toString();
    }

    // ② 参加
    @Transactional
    public PairEntity join(UUID userId, String joinCode) {

        String code = joinCode.trim();

        // 参加するユーザーを取得
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        // 二重参加防止
        if (user.getPairId() != null) {
            throw new ConflictException("User already paired. userId=" + userId);
        }

        // joinCode からペア取得
        PairEntity pair = pairRepository.findByJoinCode(code)
                .orElseThrow(() -> new NotFoundException("Pair not found. joinCode=" + code));

        // 自分で作ったペアには入れない
        if (userId.equals(pair.getUserId1())) {
            throw new ConflictException("Cannot join your own pair. userId=" + userId);
        }

        // すでに成立してたら弾く
        if (pair.getState() == PairState.PAIRED || pair.getUserId2() != null) {
            throw new ConflictException("Pair already completed. pairId=" + pair.getId());
        }

        // 参加確定：pairs側を更新
        pair.setUserId2(userId);
        pair.setState(PairState.PAIRED);
        PairEntity savedPair = pairRepository.save(pair);

        // 参加した側の users.pairId も更新
        user.setPairId(savedPair.getId());
        userRepository.save(user);

        return savedPair;
    }

    // ③ 状態確認（pairの状態を返す）
    @Transactional(readOnly = true)
    public PairStatusResponse status(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        UUID pairId = user.getPairId();
        if (pairId == null) {
            return PairStatusResponse.none(userId);
        }

        PairEntity pair = pairRepository.findById(pairId)
                .orElseThrow(() -> new NotFoundException("Pair not found: " + pairId));

        UUID partnerUserId = userRepository
                .findFirstByPairIdAndIdNot(pairId, userId)
                .map(UserEntity::getId)
                .orElse(null);

        if (partnerUserId == null) {
            return PairStatusResponse.waiting(userId, pairId, pair.getJoinCode());
        }

        return PairStatusResponse.paired(userId, pairId, pair.getJoinCode(), partnerUserId);
    }

    // ④ 退出
    @Transactional
    public void leave(UUID userId) {

        UserEntity me = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        UUID pairId = me.getPairId();
        if (pairId == null) return;

        PairEntity pair = pairRepository.findById(pairId)
                .orElseThrow(() -> new NotFoundException("Pair not found: " + pairId));

        // 自分を抜ける
        me.setPairId(null);
        userRepository.save(me);

        // 相手を特定（pairs.userId1 / userId2 で判断）
        UUID partnerId = null;
        if (userId.equals(pair.getUserId1())) {
            partnerId = pair.getUserId2();
        } else if (userId.equals(pair.getUserId2())) {
            partnerId = pair.getUserId1();
        } else {
            throw new ConflictException("User is not a member of this pair. userId=" + userId);
        }

        // 相手も抜ける（重要）
        if (partnerId != null) {
            userRepository.findById(partnerId).ifPresent(partner -> {
                partner.setPairId(null);
                userRepository.save(partner);
            });
        }

        // ペア削除
        pairRepository.delete(pair);
    }

    // ⑤ 相手ユーザーIDを取得（ペア前 / 相手未参加なら例外）
    @Transactional(readOnly = true)
    public UUID getPartnerUserIdOrThrow(UUID myUserId) {

        UserEntity me = userRepository.findById(myUserId)
                .orElseThrow(() -> new NotFoundException("User not found: " + myUserId));

        UUID pairId = me.getPairId();
        if (pairId == null) {
            throw new NotFoundException("Pair not found for user (not joined yet). userId=" + myUserId);
        }

        PairEntity pair = pairRepository.findById(pairId)
                .orElseThrow(() -> new NotFoundException("Pair not found: " + pairId));

        UUID partnerId;
        if (myUserId.equals(pair.getUserId1())) {
            partnerId = pair.getUserId2();
        } else if (myUserId.equals(pair.getUserId2())) {
            partnerId = pair.getUserId1();
        } else {
            throw new ConflictException("User is not a member of this pair. userId=" + myUserId + " pairId=" + pairId);
        }

        if (partnerId == null) {
            throw new NotFoundException("Partner not joined yet. pairId=" + pairId);
        }

        return partnerId;
    }
}