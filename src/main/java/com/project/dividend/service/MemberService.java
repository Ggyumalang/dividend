package com.project.dividend.service;

import com.project.dividend.exception.DividendException;
import com.project.dividend.model.MemberDto;
import com.project.dividend.model.SignIn;
import com.project.dividend.model.SignUp;
import com.project.dividend.persist.entity.MemberEntity;
import com.project.dividend.persist.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.project.dividend.model.constants.ErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("could not find user -> " + username));
    }

    public MemberDto register(SignUp.Request request) {
        boolean exists = memberRepository.existsByUsername(request.getUsername());
        if (exists) {
            throw new DividendException(ALREADY_EXIST_USER);
        }
        MemberEntity memberEntity = memberRepository.save(
                MemberEntity.builder()
                        .username(request.getUsername())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .roles(request.getRoles())
                        .build());
        return MemberDto.fromEntity(memberEntity);
    }

    public MemberDto authenticate(SignIn.Request member) {
        MemberEntity memberEntity = memberRepository.findByUsername(member.getUsername())
                .orElseThrow(() -> new DividendException(USER_ID_NOT_FOUND));

        if (!passwordEncoder.matches(member.getPassword(), memberEntity.getPassword())) {
            throw new DividendException(PASSWORD_NOT_MATCHED);
        }

        return MemberDto.fromEntity(memberEntity);
    }
}
