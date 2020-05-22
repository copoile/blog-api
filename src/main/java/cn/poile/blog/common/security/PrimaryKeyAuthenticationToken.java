package cn.poile.blog.common.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author: yaohw
 * @create: 2020-05-20 15:45
 **/
public class PrimaryKeyAuthenticationToken extends AbstractAuthenticationToken {


    private final Object principal;
    private Object credentials;

    public PrimaryKeyAuthenticationToken(Object principal) {
        super(null);
        this.principal = principal;
        this.credentials = principal;
        this.setAuthenticated(false);
    }

    public PrimaryKeyAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }


    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority userIdList instead");
        } else {
            super.setAuthenticated(false);
        }
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
    }
}
