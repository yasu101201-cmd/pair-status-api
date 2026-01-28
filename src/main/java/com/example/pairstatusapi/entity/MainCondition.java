package com.example.pairstatusapi.entity;

import java.util.Set;

public enum MainCondition {

    IIGAKANJI(Set.of(
        SubCondition.LONELY,
        SubCondition.PAINFUL,
        SubCondition.HAPPY,
        SubCondition.HUNGRY
    )),

    FUTSU(Set.of(
        SubCondition.LONELY,
        SubCondition.PAINFUL,
        SubCondition.HAPPY,
        SubCondition.HUNGRY
    )),

    HANASHITAI(Set.of(
        SubCondition.LONELY,
        SubCondition.PAINFUL,
        SubCondition.HAPPY,
        SubCondition.HUNGRY
    )),

    WARUI(Set.of(
        SubCondition.TIRED,
        SubCondition.SLEEPY,
        SubCondition.LONELY,
        SubCondition.PAINFUL
    )),

    TAICYOUWARUI(Set.of(
        SubCondition.COLD,
        SubCondition.FEVER,
        SubCondition.HEADACHE,
        SubCondition.SLUGGISH
    ));

    private final Set<SubCondition> allowedSubs;

    MainCondition(Set<SubCondition> allowedSubs) {
        this.allowedSubs = allowedSubs;
    }

    public boolean allows(SubCondition sub) {
        return sub == null || allowedSubs.contains(sub);
    }

    public Set<SubCondition> getAllowedSubs() {
        return allowedSubs;
    }
}