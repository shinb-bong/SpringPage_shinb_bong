package jpa.sideStudy.controller.like;

import jpa.sideStudy.domain.context.Context;
import jpa.sideStudy.domain.likes.Likes;
import jpa.sideStudy.repository.context.ContextRepository;
import jpa.sideStudy.repository.likes.LikesRepository;
import jpa.sideStudy.repository.member.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class LikeApiControllerTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    LikesRepository likesRepository;
    @Autowired
    MockMvc mockMvc;
    @Autowired ContextRepository contextRepository;
    /**
     * 좋아요 API 테스트 Post = /api/like/{contextId}
     */
    @DisplayName("좋아요 테스트")
    @WithUserDetails(userDetailsServiceBeanName = "memberService",value = "test@naver.com")
    @Test
    void testCreateLike() throws Exception {
        Context context = addContexts();

        mockMvc.perform(post("/api/like/" + context.getId()))
                .andExpect(status().isOk());

        Likes like = likesRepository.findAll().get(0);

        Assertions.assertNotNull(like);
        Assertions.assertNotNull(like.getMember().getId());
        Assertions.assertNotNull(like.getContext().getId());
    }

    @DisplayName("좋아요 중복 취소 테스트  - fail")
    @WithUserDetails(userDetailsServiceBeanName = "memberService",value = "test@naver.com")
    @Test
    void testDuplicateLike() throws Exception {
        Context context = addContexts();
        mockMvc.perform(post("/api/like/" + context.getId()))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/like/" + context.getId()))
                .andExpect(status().isOk());

        List<Likes> all = likesRepository.findAll();
        // 취소가 되어서 사라져야함.
        assertTrue(all.isEmpty());

    }

    private Context addContexts() {
        Context context = Context.builder()
                .title("안녕하세요 저는 이승민입니다.")
                .content("ㅈㄱㄴ")
                .viewCount(0)
                .member(memberRepository.findAll().get(0))
                .build();
        return contextRepository.save(context);
    }

}