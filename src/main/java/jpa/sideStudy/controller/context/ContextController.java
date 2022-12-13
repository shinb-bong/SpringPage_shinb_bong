package jpa.sideStudy.controller.context;

import jpa.sideStudy.config.AuthUser;
import jpa.sideStudy.config.MemberAdapter;
import jpa.sideStudy.domain.context.Context;
import jpa.sideStudy.domain.member.Member;
import jpa.sideStudy.repository.context.ContextRepository;
import jpa.sideStudy.service.context.ContextService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/contexts")
@Slf4j
public class ContextController {
    private final ContextService contextService;

    /**
     * 목록 조회
     * 총 3개씩 2줄 출력
     */
    @GetMapping()
    public String contextList(@RequestParam(name = "offset",defaultValue = "0",required = false) Long offset,
                                  @RequestParam(name = "limit" ,defaultValue = "6",required = false) Long limit,
                                  @ModelAttribute("contextSearchDto") ContextSearchDto contextSearchDto, Model model){
        List<Context> findContext = contextService.findAll(contextSearchDto,offset,limit);
        model.addAttribute("contextList",findContext);
        return "contexts/contextList";
    }

    /**
     * 글 작성폼
     */
    @GetMapping("/new")
    public String contextForm(@AuthenticationPrincipal MemberAdapter memberAdapter, Model model){
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
                                @AuthUser Member member, Model model){

        if(bindingResult.hasErrors()){
            log.info("bindingResult = {}", bindingResult.getAllErrors());
            return "contexts/contextForm";
        }
        try{
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
                .viewCount(findContext.getViewCount())
                .contextCategory(findContext.getContextCategory())
                .build();
        model.addAttribute("contextEditFormDto",contextEditFormDto);
        return "contexts/contextEditForm";
    }

    @PreAuthorize("isAuthenticated() and (( #context.member.name == principal.name ) or hasRole('ROLE_ADMIN') )")
    @PutMapping("/edit/{id}")
    public String edit(ContextEditFormDto contextEditFormDto, BindingResult bindingResult,
                       @PathVariable Long contextId, RedirectAttributes redirectAttributes,Model model){
        if(bindingResult.hasErrors()){
            return "contexts/contextEditForm";
        }
        try {
            contextService.edit(contextId, contextEditFormDto);
        }catch (Exception e){
            e.printStackTrace();
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
        return "redirect:/contexts/contextList";
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


}
