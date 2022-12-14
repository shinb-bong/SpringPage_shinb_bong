package jpa.sideStudy.controller.context;

import jpa.sideStudy.domain.context.Context;
import jpa.sideStudy.domain.context.ContextCategory;
import jpa.sideStudy.domain.member.Member;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class ContextFormDto {

    @NotBlank(message = "제목은 작성해주셔아합니다.")
    private String title;

    @NotBlank(message = "내용은 작성해주셔아합니다.")
    private String content;

    private ContextCategory contextCategory;
    private Long imgId;
}
