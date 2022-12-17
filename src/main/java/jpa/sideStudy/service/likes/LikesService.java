package jpa.sideStudy.service.likes;

import jpa.sideStudy.domain.context.Context;
import jpa.sideStudy.domain.likes.Likes;
import jpa.sideStudy.domain.member.Member;
import jpa.sideStudy.repository.context.ContextRepository;
import jpa.sideStudy.repository.likes.LikesRepository;
import jpa.sideStudy.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class LikesService {

    private final LikesRepository likesRepository;
    private final ContextRepository contextRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public boolean addRemoveLike(Long memberId,Long contextId){
        Member member = memberRepository.findById(memberId).orElseThrow();
        Context context = contextRepository.findById(contextId).orElseThrow();
        if(isNotAlreadyLike(member,context)){
            likesRepository.save(new Likes(context,member));
            return true;
        } else if (!isNotAlreadyLike(member,context)){
            Likes likes = likesRepository.findByMemberAndContext(member, context)
                    .orElseThrow(() -> new IllegalStateException("좋아요 한 기록이 없습니다."));
            likesRepository.delete(likes);
            return true;
        }

        return false;
    }

    private boolean isNotAlreadyLike(Member member, Context context) {
        return likesRepository.findByMemberAndContext(member,context).isEmpty();
    }
}
