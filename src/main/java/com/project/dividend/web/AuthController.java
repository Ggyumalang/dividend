package com.project.dividend.web;

import com.project.dividend.model.MemberDto;
import com.project.dividend.model.SignIn;
import com.project.dividend.model.SignUp;
import com.project.dividend.jwt.TokenProvider;
import com.project.dividend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUp.Request request) {
        return ResponseEntity.ok(memberService.register(request));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SignIn.Request request) {
        MemberDto memberDto = this.memberService.authenticate(request);
        String token = this.tokenProvider.generateToken(memberDto.getUsername(), memberDto.getRoles());
        log.info("user login -> " + request.getUsername());
        return ResponseEntity.ok(token);
    }
}
