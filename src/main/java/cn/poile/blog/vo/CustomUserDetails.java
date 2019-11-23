package cn.poile.blog.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author: yaohw
 * @create: 2019-10-24 16:45
 **/
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class CustomUserDetails extends UserVo implements UserDetails {

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (!CollectionUtils.isEmpty(roleList)) {
            return roleList.stream().map(this::createAuthority).collect(Collectors.toSet());
        }
        return null;
    }

    private GrantedAuthority createAuthority(String authority) {
       return (()->authority);
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return !getStatus().equals(3);
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return !getStatus().equals(1);
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return !getStatus().equals(2);
    }
}
