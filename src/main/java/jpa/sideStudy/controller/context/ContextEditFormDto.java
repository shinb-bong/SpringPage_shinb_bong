package jpa.sideStudy.controller.context;

import jpa.sideStudy.domain.context.ContextCategory;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class ContextEditFormDto {
    private Long id;

    @NotBlank(message = "제목은 작성해주셔아합니다.")
    private String title;

    @NotBlank(message = "내용은 작성해주셔아합니다.")
    private String content;

    private ContextCategory contextCategory;

    private int viewCount;
}
