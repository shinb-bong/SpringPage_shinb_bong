package jpa.sideStudy.domain.likes;

import jpa.sideStudy.domain.comment.Comment;
import jpa.sideStudy.domain.context.Context;
import jpa.sideStudy.domain.member.Member;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@AllArgsConstructor
@ToString
/**
 * 만들어야 하는 이유 누가 좋아요를 눌렀는지 알수 없고 좋아요 취소가 불가능하다.
 * 그리고 단발성 좋아요.
 * 좋아요받은 갯수를 출력하려면 jpl로 count 써서 하면 될듯?
 */
public class Likes {

    @Id @GeneratedValue
    @Column(name = "likes_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "context_id")
    private Context context;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public Likes(Context context, Member member) {
        this.context = context;
        this.member = member;
    }
}
