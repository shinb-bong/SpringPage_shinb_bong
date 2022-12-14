package jpa.sideStudy;

import jpa.sideStudy.controller.member.MemberFormDto;
import jpa.sideStudy.domain.context.Context;
import jpa.sideStudy.domain.context.ContextCategory;
import jpa.sideStudy.domain.member.Member;
import jpa.sideStudy.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

/**
 * 시범용 데이터
 */
@Component
@RequiredArgsConstructor
public class InitDb {
    private final InitService initService;


    @PostConstruct
    public void init(){

        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @RequiredArgsConstructor
    @Transactional
    static class InitService {
        private final EntityManager em;
        private final PasswordEncoder passwordEncoder;
        private final MemberRepository memberRepository;

        public void dbInit1() {
            MemberFormDto memberFormDto = MemberFormDto.builder().name("정우진").email("test@naver.com").password("1234").build();
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            em.persist(member);
            Context context = Context.builder().title("ddd").content("aaa").contextCategory(ContextCategory.CODING).member(member).build();
            em.persist(context);
        }
        public void dbInit2(){
            Member findMember = memberRepository.findByEmail("test@naver.com");
            for (int i = 0; i < 20; i++) {
                Context context = Context.builder().title("ddd").content("aaa").contextCategory(ContextCategory.CODING).member(findMember).build();
                em.persist(context);
            }
        }

    }

}


