package jpa.sideStudy.controller.member;

import jpa.sideStudy.domain.member.Address;
import jpa.sideStudy.domain.member.Gender;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@Data
public class MemberFormDto {
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotEmpty(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String email;

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min = 4, max = 16, message = "비밀번호는 4자 이상, 16자 이하로 입력해주세요.")
    private String password;

    private String validPassword;

    private String city;
    private String street;
    private String zipcode;

    private Gender gender;

    @Builder
    public MemberFormDto(String name, String email, String password, String city, String street, String zipcode, Gender gender) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
        this.gender = gender;
    }
}