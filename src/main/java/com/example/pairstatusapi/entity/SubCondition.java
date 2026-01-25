package com.example.pairstatusapi.entity;

public enum SubCondition {
    // 気持ち（常に表示してOK）
    LONELY,
    TOUGH,
    HAPPY,

    // 体調/生活（体調押した時におすすめとして出すやつ）
    TSUKARETA,
    NEMUI,
    ONAKA
}