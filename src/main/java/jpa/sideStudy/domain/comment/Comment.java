package jpa.sideStudy.domain.comment;

import jpa.sideStudy.domain.base.BaseEntity;
import jpa.sideStudy.domain.context.Context;
import lombok.*;

import javax.persistence.*;

@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Comment extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    // 어떤 게시물에 댓글이 작성되었는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "context_id")
    private Context context;

    private String Contents;

    // 대 댓글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

}
