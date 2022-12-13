package jpa.sideStudy.domain;

import jpa.sideStudy.domain.context.Context;
import jpa.sideStudy.domain.member.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
class BaseTimeEntityTest {

    @Autowired EntityManager em;

    @Test
    public void BaseTimeEntity_등록() throws Exception{
        //given
        LocalDateTime now = LocalDateTime.of(2019,6,4,0,0,0);

        Member member = Member.builder().name("신봉규").build();
        em.persist(member);

        log.info("생성날짜 확인 : {} ",member.getCreatedDate());

        // then
        assertThat(member.getCreatedDate()).isAfter(now);
        assertThat(member.getLastModifiedDate()).isAfter(now);
    }

    @Test
    public void BaseTimeEntity_사람() throws Exception{
        //given
        LocalDateTime now = LocalDateTime.of(2019,6,4,0,0,0);

        Context con = Context.builder().title("신봉규").build();
        em.persist(con);

        log.info("생성사람 확인 : {} ",con.getCreatedBy());


    }

}