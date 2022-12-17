package jpa.sideStudy.controller.context;

import jpa.sideStudy.controller.comment.CommentResponseDto;
import jpa.sideStudy.domain.context.Context;
import jpa.sideStudy.domain.context.ContextCategory;
import jpa.sideStudy.domain.likes.Likes;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ContextDetailDto {
    private Long id;
    private String title;
    private String content;
    private int viewCount;
    private Long imgId;
    private ContextCategory contextCategory;
    private String writer;
    private LocalDateTime createdDate, modifiedDate;
    private String  writerEmail;
    private List<CommentResponseDto> comments;

    private int likesCount;

    public ContextDetailDto(Context contexts){
        this.id = contexts.getId();
        this.title =contexts.getTitle();
        this.writer = contexts.getMember().getName();
        this.content= contexts.getContent();
        this.viewCount=contexts.getViewCount();
        this.imgId=contexts.getImgId();
        this.contextCategory=contexts.getContextCategory();
        this.createdDate= contexts.getCreatedDate();
        this.modifiedDate= contexts.getLastModifiedDate();
        this.writerEmail=contexts.getMember().getEmail();
        this.comments= contexts.getComments().stream().map(CommentResponseDto::new).collect(Collectors.toList());
        this.likesCount = contexts.getLikes().size();

    }
}
