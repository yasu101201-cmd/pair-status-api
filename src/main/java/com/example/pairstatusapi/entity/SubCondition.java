package com.example.pairstatusapi.entity;

public enum SubCondition {
    // 通常サブ（常に出す）
    SABISHII,   // 寂しい
    TSURAI,     // 辛い
    URESHII,    // 嬉しい

    // 体調サブ（「体調悪い」を押した時だけ出す）
    TSUKARETA,  // 疲れた
    NEMUI,      // 眠い
    ONAKA       // お腹すいた
}