package jpa.sideStudy.controller.context;

import jpa.sideStudy.config.AuthUser;
import jpa.sideStudy.config.MemberAdapter;
import jpa.sideStudy.controller.comment.CommentResponseDto;
import jpa.sideStudy.domain.context.Context;
import jpa.sideStudy.domain.file.FileEntity;
import jpa.sideStudy.domain.member.Member;
import jpa.sideStudy.repository.context.ContextRepository;
import jpa.sideStudy.repository.file.FileRepository;
import jpa.sideStudy.service.context.ContextService;
import jpa.sideStudy.service.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/contexts")
@Slf4j
public class ContextController {
    private final ContextService contextService;
    private final ContextRepository contextRepository;
    private final FileService fileService;
    private final FileRepository fileRepository;

    /**
     * 목록 조회
     * 총 3개씩 2줄 출력
     */
    @GetMapping()
    public String contextList(@PageableDefault Pageable pageable,
                              @ModelAttribute("contextSearchDto") ContextSearchDto contextSearchDto,
                              @RequestParam(required = false, defaultValue = "createdDate", name = "orderby") String orderCriteria,
                              Model model){
        Page<Context> findContext = contextService.findAll(contextSearchDto, pageable, orderCriteria);
        log.info("findContext.number={}",findContext.getContent());
        model.addAttribute("contextList",findContext);
        return "contexts/contextList";
    }

    /**
     * 글 작성폼
     */
    @GetMapping("/new")
    public String contextForm(@AuthenticationPrincipal MemberAdapter memberAdapter,
                              Model model){

        model.addAttribute("contextFormDto", ContextFormDto.builder().build());
        if(memberAdapter != null){
            log.info("loginMember{}",memberAdapter.getMember());
        }
        return "contexts/contextForm";
    }
    /**
     * 글 등록
     */
    @PostMapping("/new")
    public String uploadContext(@Valid ContextFormDto contextFormDto, BindingResult bindingResult,
                                @RequestParam("file") MultipartFile file,
                                @AuthUser Member member, Model model){

        if(bindingResult.hasErrors()){
            log.info("bindingResult = {}", bindingResult.getAllErrors());
            return "contexts/contextForm";
        }
        try{
            Long savedImgId = fileService.saveFile(file);
            if(savedImgId!=null){
                contextFormDto.setImgId(savedImgId);
            }
            contextService.write(contextFormDto, member);
        } catch (UsernameNotFoundException e){
            log.info("exception:{}",e.getMessage());
            return "contexts/contextForm";
        }
        return "redirect:/contexts/";
    }

    /**
     * 글 수정 페이지 보여주기
     */
    @PostAuthorize("isAuthenticated() and ((returnObject.name ==  principal.username )or hasRole('ROLE_ADMIN'))")
    @GetMapping("/edit/{id}")
    public String editForm(Model model, @PathVariable("id") Long id){
        Context findContext = contextService.findOne(id);

        ContextEditFormDto contextEditFormDto = ContextEditFormDto.builder()
                .id(id)
                .title(findContext.getTitle())
                .content(findContext.getContent())
                .contextCategory(findContext.getContextCategory())
                .imgId(findContext.getImgId())
                .build();
        model.addAttribute("contextEditFormDto",contextEditFormDto);
        model.addAttribute("viewCount",findContext.getViewCount());
        return "contexts/contextEditForm";
    }

    /**
     * 글 수정
     */
    @PreAuthorize("isAuthenticated() and (( #context.member.name == principal.name ) or hasRole('ROLE_ADMIN') )")
    @PostMapping("/edit/{id}")
    public String edit(@Valid ContextEditFormDto contextEditFormDto,
                       BindingResult bindingResult,
                       @PathVariable("id") Long contextId,
                       RedirectAttributes redirectAttributes,Model model){

        if(bindingResult.hasErrors()){
            log.info("error edit: {}",bindingResult.getAllErrors());
            return "contexts/contextEditForm";
        }
        try {
            contextService.edit(contextId, contextEditFormDto);
        }catch (Exception e){
            log.info("error edit: {}",e.getMessage());
        }
        redirectAttributes.addAttribute("id",contextId);
        return "redirect:/contexts/{id}";
    }

    /**
     * 상세페이지
     */
    @GetMapping("/{id}")
    public String detail(Model model, @PathVariable("id") Long id, @AuthUser Member member,
                         HttpServletRequest request,
                         HttpServletResponse response){
        viewCountUp(id, request, response);
        Context findContext = contextService.findOne(id);
        ContextDetailDto dto = new ContextDetailDto(findContext);
        List<CommentResponseDto> comments = dto.getComments();
        if (comments != null && !comments.isEmpty()){
            model.addAttribute("comments",comments);
        }

        model.addAttribute("findContext",dto);
        return "contexts/detailForm";
    }

    private void viewCountUp(Long id, HttpServletRequest request, HttpServletResponse response) {
        Cookie oldCookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("postView")) {
                    oldCookie = cookie;
                }
            }
        }

        if (oldCookie != null) {
            if (!oldCookie.getValue().contains("[" + id.toString() + "]")) {
                contextService.updateViewCount(id);
                oldCookie.setValue(oldCookie.getValue() + "_[" + id + "]");
                oldCookie.setPath("/");
                oldCookie.setMaxAge(60 * 60 * 24);
                response.addCookie(oldCookie);
            }
        } else {
            contextService.updateViewCount(id);
            Cookie newCookie = new Cookie("postView","[" + id + "]");
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 60 * 24);
            response.addCookie(newCookie);
        }
    }


    @PostAuthorize("isAuthenticated() and ((returnObject.name ==  principal.username )or hasRole('ROLE_ADMIN'))")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, Model model){
        contextService.delete(id);
        return "redirect:/contexts/";
    }

    /**
     * 내가 쓴글만
     */
    @PreAuthorize("isAuthenticated() and (( #model.id == principal.name ) or hasRole('ROLE_ADMIN') )")
    @GetMapping("/myList")
    public String myContext(Model model,@AuthUser Member member){
        List<Context> myList = contextService.findMyList(member);
        model.addAttribute("id",member.getEmail());
        model.addAttribute("myList",myList);
        return "contexts/myListForm";
    }

    /**
     * 이미지 출력
     */
    @GetMapping("/images/{fileId}")
    @ResponseBody
    public Resource downloadImage(@PathVariable("fileId") Long id, Model model) throws IOException{
        FileEntity file = fileRepository.findById(id).orElse(null);
        return new UrlResource("file:" + file.getSavedPath());
    }


}
