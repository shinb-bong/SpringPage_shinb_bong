package jpa.sideStudy.controller.context;

import jpa.sideStudy.config.AuthUser;
import jpa.sideStudy.config.MemberAdapter;
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
                                  @RequestParam(name = "order",required = false) Boolean boolOrder,
                                  @ModelAttribute("contextSearchDto") ContextSearchDto contextSearchDto, Model model){
        Page<Context> findContext = contextService.findAll(contextSearchDto, pageable);
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
     * 글 수정
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
    public String detail(Model model, @PathVariable("id") Long id){
        Context findContext = contextService.findOne(id);
        int newViewCount = findContext.getViewCount()+1;
        contextService.updateVisit(id,newViewCount);
        model.addAttribute("findContext",findContext);
        return "contexts/detailForm";
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
//        for (Context context : myList) {
//            contextForm()
//        }
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
