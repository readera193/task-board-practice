package com.example.taskboard.domain.user;

/** 使用者角色；資料庫以名稱字串儲存，JWT scope 與 Spring Security authority 為 {@code ROLE_<名稱>}。 */
public enum Role {
    USER,
    ADMIN
}
