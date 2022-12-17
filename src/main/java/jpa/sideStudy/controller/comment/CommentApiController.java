package jpa.sideStudy.controller.comment;

import jpa.sideStudy.config.AuthUser;
import jpa.sideStudy.domain.member.Member;
import jpa.sideStudy.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class CommentApiController {

    private final CommentService commentService;
    /**
     * 댓글 생성 API
     */
    @PostMapping("/contexts/{id}/comments")
    public ResponseEntity commentSave(@PathVariable Long id, @RequestBody CommentRequestDto dto,
                                      @AuthUser Member member){
        log.info("comment success {} ", dto);
        return ResponseEntity.ok(commentService.commentSave(member.getEmail(),id,dto));
    }
}
