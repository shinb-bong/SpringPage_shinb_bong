package jpa.sideStudy.controller.context;

import jpa.sideStudy.domain.context.ContextCategory;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContextSearchDto {

    //제목 찾기 항목
    private String cond;

    // 작성된 내용
    private String findText;

    // 카테고리 찾기
    private ContextCategory contextCategory;

}
