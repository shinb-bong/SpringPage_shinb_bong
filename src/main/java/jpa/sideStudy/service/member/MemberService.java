package jpa.sideStudy.service.member;

import jpa.sideStudy.config.MemberAdapter;
import jpa.sideStudy.domain.member.Member;
import jpa.sideStudy.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;

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
        return new MemberAdapter(member);
    }
}
