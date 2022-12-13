package jpa.sideStudy.service.member;

import jpa.sideStudy.controller.member.MemberFormDto;
import jpa.sideStudy.domain.member.Address;
import jpa.sideStudy.domain.member.Member;
import jpa.sideStudy.domain.member.MemberRole;
import jpa.sideStudy.repository.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired private MemberRepository memberRepository;
    @Autowired private MemberService memberService;
    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember(){
        MemberFormDto memberFormDto = MemberFormDto.builder()
                .email("daoh98@naver.com")
                .name("테스트")
                .password("1111")
                .build();
        return Member.createMember(memberFormDto,passwordEncoder);
    }

    @Test
    public void 회원등록(){
        Member member= createMember();
        Long joinId = memberService.join(member);

        Member findMember = memberRepository.findById(joinId).orElseThrow();
        assertEquals(member.getName(),findMember.getName());
    }

    @Test
    public void 중복이메일회원(){
        Member member1= Member.builder().email("daoh98@naver.com").build();
        Member member2 = Member.builder().email("daoh98@naver.com").build();
        memberService.join(member1);
        assertThrows(IllegalStateException.class,()->memberService.join(member2));
    }

    @Test
    public void 회원_제거(){
        Member member1 = Member.builder().email("daoh98@naver.com").role(MemberRole.USER).build();
        Long joinId = memberService.join(member1);

        Member findMember = memberRepository.findById(joinId).orElseThrow();
        findMember.deleteMember();
        assertEquals(findMember.getRole(), MemberRole.DEL);
    }


}