package jpa.sideStudy.domain.member;

import jpa.sideStudy.controller.member.MemberFormDto;
import jpa.sideStudy.domain.base.BaseTimeEntity;
import lombok.*;
import org.apache.catalina.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name  = "member_id")
    private Long id;

    // 로그인 시 필요
    @Column(unique = true)
    private String email;
    private String password;

    // 개인 정보
    private String name;
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private MemberRole role;
    // 주소
    @Embedded
    private Address address;

    @Builder
    public Member(String email, String password, String name, Gender gender, MemberRole role, Address address) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.role = role;
        this.address = address;
    }

    //== 생성 메소드
    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){
        Member member = Member.builder()
                .name(memberFormDto.getName())
                .email(memberFormDto.getEmail())
                .address(new Address(memberFormDto.getCity(), memberFormDto.getStreet(), memberFormDto.getZipcode()))
                .password(passwordEncoder.encode(memberFormDto.getPassword())) // 암호화
                .gender(memberFormDto.getGender())
                .role(MemberRole.USER)
                .build();
        return member;
    }


    public Member update(String name, Gender gender) {
        this.name = name;
        this.gender = gender;

        return this;
    }


    //=== 편의 메소드
    public void deleteMember(){
        this.role = MemberRole.DEL;
    }

}
