package jpa.sideStudy.repository.likes;

import jpa.sideStudy.domain.context.Context;
import jpa.sideStudy.domain.likes.Likes;
import jpa.sideStudy.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikesRepository extends JpaRepository<Likes,Long> {

    Optional<Likes> findByMemberAndContext(Member member, Context context);

}
