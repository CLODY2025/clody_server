package com.clody.global.auth;

import com.clody.domain.member.entity.Member;
import com.clody.domain.member.exception.MemberErrorCode;
import com.clody.domain.member.exception.MemberException;
import com.clody.domain.member.repository.MemberRepository;
import com.clody.global.apiPayload.exception.GeneralException;
import com.clody.global.apiPayload.code.base.FailureCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberRepository memberRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // @CurrentMember 애노테이션 + 파라미터 타입이 Member인 경우에만 동작
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && Member.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null) {
            throw new GeneralException(FailureCode.JWT_INVALID_TOKEN);
        }

        Object principal = auth.getPrincipal();
        Long memberId;

        // 필터에서 principal을 Long으로 넣는 것이 정석이지만,
        // 혹시 숫자 문자열로 들어오는 환경도 대비(배포/테스트 환경 차이 등)
        if (principal instanceof Long l) {
            memberId = l;
        } else if (principal instanceof String s && isAllDigits(s)) {
            memberId = Long.valueOf(s);
        } else {
            log.warn("Unexpected principal type in SecurityContext: {}", principal.getClass());
            throw new GeneralException(FailureCode.JWT_INVALID_TOKEN);
        }

        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));
    }

    private boolean isAllDigits(String s) {
        // 공백 방지 및 숫자만 허용
        if (s == null || s.isBlank()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }
}