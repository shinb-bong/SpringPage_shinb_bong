package jpa.sideStudy.controller.comment;

import jpa.sideStudy.domain.comment.Comment;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class CommentResponseDto {

    private Long id;
    private String comment;
    private String createdDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
    private String modifiedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
    private String writerName;
    private String  writerEmail;
    private Long contextsId;

    /* Entity -> Dto */
    public CommentResponseDto(Comment comment){
        this.id = comment.getId();
        this.comment = comment.getComment();
        this.createdDate = comment.getCreatedDate();
        this.modifiedDate = comment.getModifiedDate();
        this.writerName = comment.getMember().getName();
        this.contextsId = comment.getContexts().getId();
        this.writerEmail=comment.getMember().getEmail();
    }
}
