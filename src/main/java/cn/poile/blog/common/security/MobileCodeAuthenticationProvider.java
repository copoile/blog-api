package cn.poile.blog.common.security;

import cn.poile.blog.common.exception.BadMobileCodeException;
import cn.poile.blog.common.sms.SmsCodeService;
import cn.poile.blog.service.impl.UserDetailsServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 手机号验证码认证提供者
 * @author: yaohw
 * @create: 2019-11-07 16:50
 **/
@Log4j2
public class MobileCodeAuthenticationProvider implements AuthenticationProvider, MessageSourceAware {

    private SmsCodeService smsCodeService;

    private UserDetailsService userDetailsService;

    private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();


    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Long mobile = (Long) authentication.getPrincipal();
        String code = (String) authentication.getCredentials();
        if (!smsCodeService.checkSmsCode(mobile, code)) {
            throw new BadMobileCodeException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Invalid code"));
        }
        UserDetails user;
        try {
            user = userDetailsService.loadUserByUsername(Long.toString(mobile));
        } catch (UsernameNotFoundException var6) {

            throw new UsernameNotFoundException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));

        }
        check(user);
        MobileCodeAuthenticationToken authenticationToken = new MobileCodeAuthenticationToken(user, code, user.getAuthorities());
        authenticationToken.setDetails(authenticationToken.getDetails());
        return authenticationToken;
    }

    /**
     * 指定该认证提供者验证Token对象
     *
     * @param aClass
     * @return
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return MobileCodeAuthenticationToken.class.isAssignableFrom(aClass);
    }

    /**
     * 账号禁用、锁定、超时校验
     *
     * @param user
     */
    private void check(UserDetails user) {
        if (!user.isAccountNonLocked()) {
            throw new LockedException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.locked", "User account is locked"));
        } else if (!user.isEnabled()) {
            throw new DisabledException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"));
        } else if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.expired", "User account has expired"));
        }
    }

    public SmsCodeService getSmsCodeService() {
        return smsCodeService;
    }

    public void setSmsCodeService(SmsCodeService smsCodeService) {
        this.smsCodeService = smsCodeService;
    }

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public MessageSourceAccessor getMessages() {
        return messages;
    }

    public void setMessages(MessageSourceAccessor messages) {
        this.messages = messages;
    }

}
