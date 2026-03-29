package org.csu.bj.shiro;
import org.csu.bj.entity.Admin;
import org.csu.bj.utils.JwtUtil;
import org.csu.bj.utils.SpringContextUtils;
import org.csu.bj.shiro.authc.JwtToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class JWTShiroRealm extends AuthorizingRealm {

    /**
     * 必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("AuthorizationInfo");
        return new SimpleAuthorizationInfo();
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        System.out.println("doGetAuthenticationInfo");
        String token = (String) auth.getCredentials();
        if (token == null) {
            throw new AuthenticationException("token为空!");
        }
        // 校验token有效性
        Admin loginUser = this.checkUserTokenIsEffect(token);
        return new SimpleAuthenticationInfo(loginUser, token, getName());
    }

    /**
     * 校验token的有效性
     *
     * @param token
     */
    public Admin checkUserTokenIsEffect(String token) throws AuthenticationException {
        // 解密获得username，用于和数据库进行对比
        String username = JwtUtil.getUsername(token);
        log.info("———checkUserTokenIsEffect ——————— getPhone:"+ username);
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        String path = request.getServletPath();
        log.info("requestPath:" + path);
        if (username == null) {
            throw new AuthenticationException("token非法无效!");
        }
        int authority = JwtUtil.getAuthority(token);
        Admin admin = new Admin();

        admin.setUsername(username);
        admin.setAuthority(authority);
        return admin;
    }

}
