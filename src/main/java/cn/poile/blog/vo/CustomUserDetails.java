package cn.poile.blog.vo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author: yaohw
 * @create: 2019-10-24 16:45
 **/
public class CustomUserDetails extends UserVo implements UserDetails {


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (!CollectionUtils.isEmpty(authorityList)) {
            return authorityList.stream().map(s -> createAuthority(s.getAuthorityCode())).collect(Collectors.toList());
        }
        return null;
    }

    private GrantedAuthority createAuthority(String authority) {
       return () -> {
               return authority;
       };
    }

    @Override
    public boolean isAccountNonExpired() {
        return !getStatus().equals(3);
    }

    @Override
    public boolean isAccountNonLocked() {
        return !getStatus().equals(1);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return !getStatus().equals(2);
    }
}
