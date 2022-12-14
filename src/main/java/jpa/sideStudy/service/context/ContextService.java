package jpa.sideStudy.service.context;

import jpa.sideStudy.controller.context.ContextEditFormDto;
import jpa.sideStudy.controller.context.ContextFormDto;
import jpa.sideStudy.controller.context.ContextSearchDto;
import jpa.sideStudy.domain.context.Context;
import jpa.sideStudy.domain.member.Member;
import jpa.sideStudy.repository.context.ContextQueryRepository;
import jpa.sideStudy.repository.context.ContextRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
@Slf4j
@RequiredArgsConstructor
public class ContextService {
    private final ContextRepository contextRepository;
    private final ContextQueryRepository contextQueryRepository;
    /**
     * 글 저장
     */
    @Transactional
    public Long write(ContextFormDto contextFormDto, Member member){
        Context context = Context.createContext(contextFormDto,member);
        contextRepository.save(context);
        return context.getId();
    }
    /**
     *  글 수정
     */
    @Transactional
    public Long edit(Long contextId, ContextEditFormDto contextEditFormDto){
        Context context = contextRepository.findById(contextId)
                .orElseThrow(()-> new IllegalStateException("게시물이 없습니다."));
        Long changeContextId = context.change(contextEditFormDto);
        return changeContextId;
    }
    /**
     * 조회 -1
     */
//    ContextSearch search
    public Page<Context> findAll(ContextSearchDto contextSearchDto, Pageable pageable, String orderCriteria){
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1); // page는 index 처럼 0부터 시작
        pageable= PageRequest.of(page,9,Sort.by(Sort.Direction.DESC,orderCriteria));

        Page<Context> findContext = contextQueryRepository.findAll(contextSearchDto, pageable);
        log.info("findContext = {}",findContext);
        return findContext;
    }

    /**
     * 단건 조회
     */
    public Context findOne(Long contextId){
        Context findContext = contextRepository.findById(contextId).orElseThrow(
                () -> new IllegalStateException("글이 없습니다.")
        );
        return findContext;
    }
    @Transactional
    public Long delete(Long id) {
        Context deleteContext = findOne(id);
        contextRepository.delete(deleteContext);
        return id;
    }

    /**
     * 조회수 증가
     */
    @Transactional
    public void updateVisit(Long id, int newViewCount) {
        Context context = contextRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("해당 게시물이 없습니다.")
        );
        context.updateViewCount(newViewCount);
    }

    public List<Context> findMyList(Member member) {
        List<Context> myList = contextQueryRepository.findMyList(member);
        return myList;
    }
}
