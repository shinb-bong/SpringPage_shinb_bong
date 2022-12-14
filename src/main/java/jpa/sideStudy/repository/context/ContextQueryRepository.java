package jpa.sideStudy.repository.context;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpa.sideStudy.controller.context.ContextSearchDto;
import jpa.sideStudy.domain.context.Context;
import jpa.sideStudy.domain.context.ContextCategory;
import jpa.sideStudy.domain.context.QContext;
import jpa.sideStudy.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ContextQueryRepository {
    private final JPAQueryFactory queryFactory;
    QContext context = QContext.context;
    public Page<Context> findAll(ContextSearchDto contextSearchDto, Pageable pageable) {
        QueryResults<Context> results = queryFactory.selectFrom(context)
                .where(likeContext(contextSearchDto.getCond(), contextSearchDto.getFindText())
                        ,likeCategory(contextSearchDto.getContextCategory())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(contextSort(pageable))
                .fetchResults();

        List<Context> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content,pageable,total);
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
     * 정렬할건지
     */
    private OrderSpecifier<?> contextSort(Pageable page) {
        //서비스에서 보내준 Pageable 객체에 정렬조건 null 값 체크
        if (!page.getSort().isEmpty()) {
            //정렬값이 들어 있으면 for 사용하여 값을 가져온다
            for (Sort.Order order : page.getSort()) {
                // 서비스에서 넣어준 DESC or ASC 를 가져온다.
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                // 서비스에서 넣어준 정렬 조건을 스위치 케이스 문을 활용하여 셋팅하여 준다.
                switch (order.getProperty()){
                    case "viewCount":
                        return new OrderSpecifier(direction, context.viewCount);
                    case "createdDate":
                        return new OrderSpecifier(direction, context.createdDate);
                }
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
