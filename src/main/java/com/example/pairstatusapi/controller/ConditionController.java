package com.example.pairstatusapi.controller;

// import com.example.pairstatusapi.dto.ConditionRequest;
// import com.example.pairstatusapi.entity.ConditionType;
import com.example.pairstatusapi.entity.ConditionUpdateEntity;
import com.example.pairstatusapi.service.ConditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/conditions")
public class ConditionController {

    private final ConditionService conditionService;

    private UUID currentUserId(Authentication auth) {
        return UUID.fromString((String) auth.getPrincipal());
    }

    @GetMapping("/me/latest")
    public ConditionUpdateEntity myLatest(Authentication auth) {
        return conditionService.getMyLatest(currentUserId(auth));
    }


@GetMapping("/partner/latest")
public ConditionUpdateEntity partnerLatest(Authentication auth) {
    return conditionService.getPartnerLatest(currentUserId(auth));
}

   @PostMapping
public ResponseEntity<?> postCondition(
        @RequestBody Map<String, String> body,
        Authentication auth
) {
    UUID userId = currentUserId(auth);

    String conditionStr = body.get("condition");
    if (conditionStr == null || conditionStr.isBlank()) {
        return ResponseEntity.badRequest().body("condition is required");
    }

    // "GENKI" → ConditionType.GENKI に変換
    var condition = com.example.pairstatusapi.entity.ConditionType.valueOf(conditionStr);

    var saved = conditionService.post(userId, condition);

    return ResponseEntity.ok(saved);
}
}