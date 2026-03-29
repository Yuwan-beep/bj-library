package org.csu.bj.shiro;
import org.csu.bj.shiro.authc.aop.JwtFilter;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    @Bean
    JWTShiroRealm myRealm() {
        return new JWTShiroRealm();
    }

    @Bean
    DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(myRealm());
        return manager;
    }

    @Bean
    ShiroFilterFactoryBean shiroFilterFactoryBean() {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(securityManager());
        //如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        bean.setLoginUrl("/login");
        //登录成功后要跳转的链接
        bean.setSuccessUrl("/index");
        //未授权界面
        bean.setUnauthorizedUrl("/403");

        Map<String, String> map = new LinkedHashMap<>();
        // 添加自己的过滤器并且取名为jwt
        Map<String, Filter> filterMap = new HashMap<String, Filter>(1);
        filterMap.put("jwt", new JwtFilter());
        map.put("/api/posts/search","jwt");
        map.put("/api/post","jwt");
        //map.put("/user/register","jwt");
        map.put("/api/user/postsByUser","jwt");
        map.put("/api/group/merchant/add","jwt");
        map.put("/api/group/addmember","jwt");
        map.put("/api/group/getmembers","jwt");
        map.put("/api/group/shop/all","jwt");
        map.put("/car/add","jwt");
        map.put("/merchantSelfDeliveryOrder/generateOrder","jwt");
        map.put("/merchantSelfDeliveryOrder/pageListByMerchant","jwt");
        map.put("/merchant/getMerchantInfo","jwt");
        map.put("/defaultAddressSetting/edit","jwt");
        map.put("/defaultAddressSetting/queryDefaultAddressSetting","jwt");
        map.put("/unifiedOrder/pageOrder","jwt");



//        map.put("/tool/redirectCollectPage","jwt");
//        map.put("/tool/getCollectMessage","jwt");

        bean.setFilters(filterMap);
        // <!-- 过滤链定义，从上向下顺序执行，一般将/**放在最为下边
        //配置不会被拦截的链接
        bean.setFilterChainDefinitionMap(map);
        return bean;
    }
}

