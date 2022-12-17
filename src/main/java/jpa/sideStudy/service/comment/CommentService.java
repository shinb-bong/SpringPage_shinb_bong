package jpa.sideStudy.service.comment;

import jpa.sideStudy.controller.comment.CommentRequestDto;
import jpa.sideStudy.domain.comment.Comment;
import jpa.sideStudy.domain.context.Context;
import jpa.sideStudy.domain.member.Member;
import jpa.sideStudy.repository.commentRepository.CommentRepository;
import jpa.sideStudy.repository.context.ContextRepository;
import jpa.sideStudy.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final ContextRepository contextRepository;

    /**
     * 댓글 생성
     */
    @Transactional
    public Long commentSave(String email, Long id, CommentRequestDto requestDto){
        Member findMember = memberRepository.findByEmail(email);
        Context contexts = contextRepository.findById(id).orElseThrow(() ->
                new IllegalStateException("댓글 쓰기 실패: 해당 게시물이 없습니다." + id));

        requestDto.setMember(findMember);
        requestDto.setContexts(contexts);

        Comment comment = requestDto.toEntity();
        commentRepository.save(comment);
        return requestDto.getId();
    }
}
