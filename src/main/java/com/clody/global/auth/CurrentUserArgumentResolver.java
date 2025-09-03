package com.clody.global.auth;

import com.clody.domain.member.entity.Member;
import com.clody.domain.member.repository.MemberRepository;
import com.clody.global.apiPayload.exception.GeneralException;
import com.clody.global.apiPayload.code.base.FailureCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberRepository memberRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class) 
                && parameter.getParameterType().equals(Member.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, 
                                NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new GeneralException(FailureCode.JWT_INVALID_TOKEN);
        }
        
        Long memberId = (Long) authentication.getPrincipal();
        
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(FailureCode._NOT_FOUND));
    }
}