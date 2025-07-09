package com.example.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email") // 이메일 중복 방지
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;  // 한글 닉네임 (중복 가능)

    @Column(nullable = false, unique = true)
    private String username;  // 영문/숫자 아이디 (@로 태그 가능, 중복 불가능)

    @Column(nullable = false, unique = true)
    private String email;  // 이메일 (중복 불가능, 같은 이메일이면 같은 유저)

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_social_logins", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "social_provider")
    private Set<String> socialProviders = new HashSet<>();  
    // 카카오/구글/네이버 로그인 기록 (동시에 연동 가능)

    public User(String nickname, String username, String email) {
        this.nickname = nickname;
        this.username = username;
        this.email = email;
    }

    public void addSocialProvider(String provider) {
        this.socialProviders.add(provider);
    }
}