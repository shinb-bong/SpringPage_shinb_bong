package jpa.sideStudy.controller.member.naver;

import jpa.sideStudy.domain.member.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class SessionUser implements Serializable {

    private String email;

    private String name;
    private Gender gender;

    public SessionUser(String email, String name, Gender gender) {
        this.email = email;
        this.name = name;
        this.gender = gender;
    }
}
