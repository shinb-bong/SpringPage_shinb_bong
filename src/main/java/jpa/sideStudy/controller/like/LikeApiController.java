package jpa.sideStudy.controller.like;


import jpa.sideStudy.config.AuthUser;
import jpa.sideStudy.domain.member.Member;
import jpa.sideStudy.service.likes.LikesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class LikeApiController {

    private  final LikesService likesService;

    @PostMapping("/like/{contextId}")
    public ResponseEntity<String> addLike(
            @AuthUser Member member,
            @PathVariable(name = "contextId") Long contextId){
        boolean result = false;
        if( member != null){
            result = likesService.addLike(member.getId(),contextId);
        }

        return result ?
                new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
