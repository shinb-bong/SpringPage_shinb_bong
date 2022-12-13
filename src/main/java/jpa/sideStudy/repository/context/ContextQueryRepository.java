package jpa.sideStudy.repository.context;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpa.sideStudy.controller.context.ContextSearchDto;
import jpa.sideStudy.domain.context.Context;
import jpa.sideStudy.domain.context.ContextCategory;
import jpa.sideStudy.domain.context.QContext;
import jpa.sideStudy.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ContextQueryRepository {
    private final JPAQueryFactory queryFactory;
    QContext context = QContext.context;
    public List<Context> findAll(ContextSearchDto contextSearchDto,Long offset, Long limit){
        QueryResults<Context> results = queryFactory.selectFrom(context)
                .where(likeContext(contextSearchDto.getCond(),contextSearchDto.getFindText())
                    ,likeCategory(contextSearchDto.getContextCategory())
                        )
                .offset(offset)
                .limit(limit)
                .fetchResults();
        return results.getResults();
    }


    /**
     * 카테고리 분류
     */
    private BooleanExpression likeCategory(ContextCategory contextCategory) {
        if(contextCategory != null){
            return context.contextCategory.eq(contextCategory);
        }
        return null;
    }

    /**
     * 제목만
     */
    private BooleanExpression likeContext(String cond,String text) {
        if (!StringUtils.isEmpty(cond)) {
            if (cond.equals("title")) {
                return context.title.like("%" + text + "%");
            } else if (cond.equals("content")) {
                return context.content.like("%" + text + "%");
            } else if (cond.equals("writer")) {
                return context.member.name.like("%" + text + "%");
            }
        }
        return null;
    }

    /**
     * 내 목록 불러오기
     */
    public List<Context> findMyList(Member member) {
        QContext context = QContext.context;

        QueryResults<Context> results = queryFactory.selectFrom(context)
                .where(context.member.eq(member))
                .limit(30)
                .fetchResults();
        return results.getResults();

    }
}
