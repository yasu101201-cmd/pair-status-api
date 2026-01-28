package com.example.pairstatusapi.controller;

import com.example.pairstatusapi.entity.MainCondition;
import com.example.pairstatusapi.entity.SubCondition;
import com.example.pairstatusapi.entity.ConditionUpdateEntity;
import com.example.pairstatusapi.service.ConditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/conditions")
public class ConditionController {

    private final ConditionService conditionService;

    private UUID currentUserId(Authentication auth) {
        return UUID.fromString((String) auth.getPrincipal());
    }

    // 旧URL互換：自分の最新
    @GetMapping("/me/latest")
    public ConditionUpdateEntity myLatest(Authentication auth) {
        return conditionService.getMyLatest(currentUserId(auth));
    }

    // 旧URL互換：相手の最新
    @GetMapping("/partner/latest")
    public ConditionUpdateEntity partnerLatest(Authentication auth) {
        return conditionService.getPartnerLatest(currentUserId(auth));
    }

    /**
     * 旧形式: {"condition":"GENKI"}
     * 新形式: {"mainCondition":"GENKI","subCondition":"SABISHII","note":"..."}
     * 両方受ける
     */
    @PostMapping
    public ResponseEntity<?> postCondition(@RequestBody Map<String, Object> body, Authentication auth) {
        UUID userId = currentUserId(auth);

        try {
            // 新形式優先
            String mainStr = str(body.get("mainCondition"));
            String subStr  = str(body.get("subCondition"));
            String note    = str(body.get("note"));

            // 旧形式 fallback
            if (mainStr == null || mainStr.isBlank()) {
                mainStr = str(body.get("condition"));
            }

            if (mainStr == null || mainStr.isBlank()) {
                return ResponseEntity.badRequest().body("mainCondition (or condition) is required");
            }

            MainCondition main = MainCondition.valueOf(mainStr);

            SubCondition sub = null;
            if (subStr != null && !subStr.isBlank() && !"NONE".equalsIgnoreCase(subStr)) {
                sub = SubCondition.valueOf(subStr);
            }

            var saved = conditionService.post(userId, main, sub, note);
            return ResponseEntity.ok(saved);

        } catch (IllegalArgumentException e) {
            // Enum変換失敗は 400 にする（500にしない）
            return ResponseEntity.badRequest().body("Invalid enum value: " + e.getMessage());
        }
    }

    private static String str(Object v) {
        return v == null ? null : String.valueOf(v);
    }

  @GetMapping("/talk")
public List<ConditionUpdateEntity> talk(Authentication auth) {
    return conditionService.getTalkHistory(currentUserId(auth));
}
}