package jpa.sideStudy.service.member;

import jpa.sideStudy.config.MemberAdapter;
import jpa.sideStudy.controller.member.naver.SessionUser;
import jpa.sideStudy.domain.member.Member;
import jpa.sideStudy.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService implements UserDetailsService, OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MemberRepository memberRepository;

    private final HttpSession httpSession;

    /**
     * 회원가입
     */
    @Transactional
    public Long join(Member member){
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    // 회원 조회는 repository에서 바로 가져다 쓸 예정

    /**
     * 회원 제거
     * 일단은 역할을 DEL로 바꾸는걸로함.
     * 지워버리면 Log나 이런걸 못찍기 때문에.
     */
    @Transactional
    public Long delete(Member member){
        member.deleteMember();
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        //EXCEPTION
        Member findMember = memberRepository.findByEmail(member.getEmail());// DB에 유니크 제약조건으로 최후의 방어해야함.
        if(findMember != null){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 회원 로그인 구현
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);
        if(member == null){
            throw new UsernameNotFoundException(email);
        }
        httpSession.setAttribute("member", new SessionUser(member.getEmail(), member.getName(), member.getGender()));
        return new MemberAdapter(member);
    }

    /**
     * 네이버
     */
    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        //네이버와 카카오를 구분
        OAuthAttributes attributes = OAuthAttributes.of(registrationId,userNameAttributeName,oAuth2User.getAttributes());
        Member member = saveOrUpdate(attributes);
        httpSession.setAttribute("member", new SessionUser(member.getEmail(), member.getName(), member.getGender()));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
                );
    }

    private Member saveOrUpdate(OAuthAttributes attributes) {
         Member member = memberRepository.findByEmail(attributes.getEmail());
         if(member == null){
             member = attributes.toEntity();
             return memberRepository.save(member);
         } else {
             return member;
         }

    }
}
