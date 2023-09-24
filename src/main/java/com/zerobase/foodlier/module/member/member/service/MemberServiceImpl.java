package com.zerobase.foodlier.module.member.member.service;

import com.zerobase.foodlier.common.security.provider.JwtTokenProvider;
import com.zerobase.foodlier.common.security.provider.dto.TokenDto;
import com.zerobase.foodlier.global.auth.dto.SignInForm;
import com.zerobase.foodlier.global.profile.dto.MemberPrivateProfileForm;
import com.zerobase.foodlier.global.profile.dto.MemberPrivateProfileResponse;
import com.zerobase.foodlier.module.member.member.domain.model.Member;
import com.zerobase.foodlier.module.member.member.exception.MemberException;
import com.zerobase.foodlier.module.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.zerobase.foodlier.module.member.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Override
    public TokenDto signIn(SignInForm form) {
        Member member = memberRepository.findByEmail(form.getEmail()).stream()
//                .filter(m-> passwordEncoder.matches(form.getPassword(), m.getPassword()))
                .filter(m -> form.getPassword().equals(m.getPassword()))
                .findFirst()
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        return tokenProvider.createToken(member);
    }

    @Override
    public void signOut(String email) {
        tokenProvider.deleteRefreshToken(email);
    }

    @Override
    public MemberPrivateProfileResponse getPrivateProfile(String email) {
        Member member = findByEmail(email);

        return MemberPrivateProfileResponse.builder()
                .nickName(member.getNickname())
                .email(member.getEmail())
                .address(member.getAddress())
                .phoneNumber(member.getPhoneNumber())
                .profileUrl(member.getProfileUrl())
                .build();
    }

    @Override
    public void updatePrivateProfile(String email, MemberPrivateProfileForm form) {
        Member member = findByEmail(email);

        member.setNickname(form.getNickName());
        member.setPhoneNumber(form.getPhoneNumber());
        member.setAddress(form.getAddress());
        member.setProfileUrl(form.getProfileUrl());

        memberRepository.save(member);
    }

    @Override
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow();
    }
}
