package jpa.sideStudy.domain.context;

import jpa.sideStudy.controller.context.ContextEditFormDto;
import jpa.sideStudy.controller.context.ContextFormDto;
import jpa.sideStudy.domain.base.BaseEntity;
import jpa.sideStudy.domain.member.Member;
import lombok.*;

import javax.persistence.*;

@ToString
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

    @Enumerated(EnumType.STRING)
    private ContextCategory contextCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
    private Long imgId;

//    /**
//     * 전체화면에서도 좋아요 수 노출을 위해
//     */
//    @OneToMany(mappedBy = "context",cascade = CascadeType.ALL)
//    Set<Likes> likes = new HashSet<>();

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
