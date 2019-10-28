package cn.poile.blog.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author: yaohw
 * @create: 2019-10-24 16:45
 **/
@Data
public class CustomUserDetails extends UserVo implements UserDetails {

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (!CollectionUtils.isEmpty(authorityList)) {
            return authorityList.stream().map(s -> createAuthority(s.getAuthorityCode())).collect(Collectors.toList());
        }
        return null;
    }

    private GrantedAuthority createAuthority(String authority) {
       return (()->authority);
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return !getStatus().equals(3);
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return !getStatus().equals(1);
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return !getStatus().equals(2);
    }
}
