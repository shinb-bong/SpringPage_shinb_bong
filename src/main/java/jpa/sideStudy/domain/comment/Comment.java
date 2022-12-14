package jpa.sideStudy.domain.comment;

import jpa.sideStudy.domain.base.BaseEntity;
import jpa.sideStudy.domain.context.Context;
import jpa.sideStudy.domain.member.Member;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;

@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Comment {

    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String comment; // 댓글 내용

    // 어떤 게시물에 댓글이 작성되었는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "context_id")
    private Context contexts;

    // 작성자
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "created_date")
    @CreatedDate
    private String createdDate;
    @Column(name = "modified_date")
    @LastModifiedDate
    private String modifiedDate;

//    // 대 댓글
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "parent_id")
//    private Comment parent;

}
