package jpa.sideStudy.domain.context;

import jpa.sideStudy.controller.context.ContextEditFormDto;
import jpa.sideStudy.controller.context.ContextFormDto;
import jpa.sideStudy.domain.base.BaseEntity;
import jpa.sideStudy.domain.comment.Comment;
import jpa.sideStudy.domain.likes.Likes;
import jpa.sideStudy.domain.member.Member;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Context extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "context_id")
    private Long id;

    // 게시물 제목, 내용, 게시물 본 수
    private String title;
    private String content;
    private int viewCount;
    private Long imgId; // 저장된 이미지 id

    @Enumerated(EnumType.STRING)
    private ContextCategory contextCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    // 양방향 연결
    @OneToMany(mappedBy = "contexts", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @OrderBy("id asc") // 댓글 정렬
    private List<Comment> comments = new ArrayList<>() ;

    /**
     * 디테일에서 좋아요 노출수와 다른 곳에서 좋아요 노출을 위해
     * 양방향 조회 설정
     * 원래는 단방향 설정으로 설정하는 것이 좋음
     */
    @OneToMany(mappedBy = "context", cascade = CascadeType.ALL)
    Set<Likes> likes = new HashSet<>();


    @Builder
    public Context(String title, String content, int viewCount, ContextCategory contextCategory, Member member,Long imgId) {
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.contextCategory = contextCategory;
        this.member = member;
        this.imgId = imgId;
    }

    /**
     * 좋아요 수도 자동 생성 시켜야함.
     */
    public static Context createContext(ContextFormDto contextFormDto, Member member){
        Context writeContext = Context.builder()
                .title(contextFormDto.getTitle())
                .content(contextFormDto.getContent())
                .viewCount(0)
                .contextCategory(contextFormDto.getContextCategory())
                .imgId(contextFormDto.getImgId())
                .member(member)
                .build();
        return writeContext;
    }

    public Long change(ContextEditFormDto contextEditFormDto) {
        this.title = contextEditFormDto.getTitle();
        this.content = contextEditFormDto.getContent();
        this.contextCategory = contextEditFormDto.getContextCategory();
        this.imgId = contextEditFormDto.getImgId();
        return this.getId();
    }

    public void updateViewCount(int newViewCount) {
        this.viewCount = newViewCount;
    }

}
