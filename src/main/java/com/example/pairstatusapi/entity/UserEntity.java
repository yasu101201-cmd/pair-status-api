// package com.example.pairstatusapi.entity;

// import jakarta.persistence.*;
// import lombok.*;

// import java.time.OffsetDateTime;
// import java.util.UUID;

// @Entity
// @Table(
//         name = "users",
//         uniqueConstraints = {
//                 @UniqueConstraint(name = "uk_users_email", columnNames = "email")
//         }
// )
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// public class UserEntity {

//     @Id
//     private UUID id;

//     @Column(nullable = false, length = 200)
//     private String email;

//     // BCrypt 等のハッシュが入る（平文は絶対保存しない）
//     @Column(nullable = false, length = 200)
//     private String passwordHash;

//     // ペア参加前はnull
//     @Column(nullable = true)
//     private UUID pairId;

//     @Column(nullable = false)
//     private OffsetDateTime createdAt;
// }
package com.example.pairstatusapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(name = "uk_users_email", columnNames = "email")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    private UUID id;

    // ✅ 追加：ログイン識別子
    @Column(nullable = false, length = 200)
    private String email;

    // ペア参加前はnull
    @Column(nullable = true)
    private UUID pairId;
}