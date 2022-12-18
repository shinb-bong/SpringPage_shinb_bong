package jpa.sideStudy.service.member;

import jpa.sideStudy.domain.member.Gender;
import jpa.sideStudy.domain.member.Member;
import jpa.sideStudy.domain.member.MemberRole;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@Builder
@RequiredArgsConstructor
public class OAuthAttributes {
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String name;
    private final String email;
    private final Gender gender;

    public static OAuthAttributes of(String registrationId,String userNameAttributeName,Map<String, Object> attributes) {
        if("naver".equals(registrationId)) {
            return ofNaver("id", attributes);
        }
        else if("kakao".equals(registrationId)){
            return ofKakao("id", attributes);
        }
        else if("google".equals(registrationId)){
            return ofGoogle(userNameAttributeName,attributes);
        }
        return null;
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {

        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();

    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String,Object> attributeAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String,Object> attributesProfile = (Map<String, Object>) attributeAccount.get("profile");

        return OAuthAttributes.builder()
                .name((String) attributesProfile.get("nickname"))
                .email((String) attributeAccount.get("email"))
                .gender((Gender) attributesProfile.get("gender"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {

        Map<String, Object> response = (Map<String, Object>)attributes.get("response");

        return OAuthAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public Member toEntity() {
        return Member.builder()
                .name(name)
                .email(email)
                .gender(gender)
                .role(MemberRole.USER)
                .build();
    }
}
