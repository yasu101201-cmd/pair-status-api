package com.example.pairstatusapi.dto;

import com.example.pairstatusapi.entity.MainCondition;
import com.example.pairstatusapi.entity.SubCondition;

public record ConditionPostRequest(
        MainCondition condition,
        SubCondition subCondition,
        String note
) {}