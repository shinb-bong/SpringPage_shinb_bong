package jpa.sideStudy.controller.member;

import jpa.sideStudy.controller.member.naver.SessionUser;
import jpa.sideStudy.domain.member.Member;
import jpa.sideStudy.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/members")
@Controller
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final HttpSession httpSession;

    @GetMapping("/new")
    public String memberForm(Model model){
        model.addAttribute("memberFormDto",new MemberFormDto());
        return "member/memberForm";
    }

    @PostMapping("/new")
    public String memberJoinForm(@Valid MemberFormDto memberFormDto,
                                 BindingResult bindingResult, Model model){
        validatePassword(memberFormDto, bindingResult);
        if (bindingResult.hasErrors()){
            return "redirect:/member/memberForm";
        }

        try{
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.join(member);
        }catch (IllegalStateException e){
            model.addAttribute("errorMessage",e.getMessage());
            return "member/memberForm";
        }
        return "redirect:/members/login";
    }

    private static void validatePassword(MemberFormDto memberFormDto, BindingResult bindingResult) {
        if(!memberFormDto.getPassword().equals(memberFormDto.getValidPassword())){
            bindingResult.rejectValue("validPassword","400","위의 비밀번호와 일치하지 않습니다.");
        }
    }

    @GetMapping(value = "/login")
    public String loginMember(HttpServletRequest request, Model model) {

        /**
         * 이전 페이지로 되돌아가기 위한 Referer 헤더값을 세션의 prevPage attribute로 저장
         */
        String uri = request.getHeader("Referer");
        if (uri != null && !uri.contains("/login")) {
            request.getSession().setAttribute("prevPage", uri);
        }

        SessionUser member = (SessionUser) httpSession.getAttribute("member");
        if (member != null) {
            model.addAttribute("memberName", member.getEmail());
        }

        return "/member/memberLoginForm";
    }

    @GetMapping(value = "/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");
        return "/member/memberLoginForm";
    }

}
