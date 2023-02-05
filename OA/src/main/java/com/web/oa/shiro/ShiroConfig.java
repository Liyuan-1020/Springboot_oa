package com.web.oa.shiro;

/**
 * @author 饶庭祥 on 2023/2/4
 */

import com.web.oa.pojo.ActiveUser;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Filter;

/**
 * @author ah
 */
@Configuration
public class ShiroConfig {

    /*@Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //注入核心安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //注入拦截器
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("authc", new JsonPermissionFilter());
        shiroFilterFactoryBean.setFilters(filterMap);
        //配置拦截链
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/", "anon");
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/login/auth", "anon");
        filterChainDefinitionMap.put("/login/logout", "anon");
        filterChainDefinitionMap.put("/error", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm());
        securityManager.setSessionManager(sessionManager());
        securityManager.setCacheManager(cacheManager());
        return securityManager;
    }

    @Bean
    public SysUserRealm userRealm() {
        SysUserRealm userRealm = new SysUserRealm();
        userRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return userRealm;
    }

    *//*@Bean
    public ActiveUser userRealm() {
        ActiveUser userRealm = new ActiveUser();
        userRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return userRealm;
    }*//*

    *//**
     * 凭证匹配器
     * 执行login(token)后由securityManager调用，用于计算密码加密后的密文
     * @return
     *//*
    @Bean(name = "credentialsMatcher")
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        //设置散列算法
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        //设置散列计算次数，相当于md5(md5(""))
        hashedCredentialsMatcher.setHashIterations(1);
        //storedCredentialsHexEncoded默认是true，此时用的是密码加密用的是Hex编码；false时用Base64编码
        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
        return hashedCredentialsMatcher;
    }

    *//**
     * 会话管理
     * 默认使用容器session，这里改为自定义session
     * session的全局超时时间默认是30分钟
     * @return
     *//*
    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        // sessionId cookie
        SimpleCookie cookie = new SimpleCookie();
        cookie.setName("sid");
        // 设置cookie的存活时间为30分钟，与session timeout时间一致
        cookie.setMaxAge(1800);
        cookie.setHttpOnly(true);
        sessionManager.setSessionIdCookie(cookie);
        sessionManager.setSessionIdCookieEnabled(true);
        //默认使用MemerySessionDao，设置为EnterpriseCacheSessionDAO以配合ehcache实现分布式集群缓存支持
        sessionManager.setSessionDAO(new EnterpriseCacheSessionDAO());
        return sessionManager;
    }

    *//**
     * 缓存管理器
     * 配合session dao实现分布式集群session，用于进程内缓存session
     * @return
     *//*
    @Bean
    public EhCacheManager cacheManager() {
        EhCacheManager cacheManager = new EhCacheManager();
        cacheManager.setCacheManagerConfigFile("classpath:conf/ehcache.xml");
        return cacheManager;
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }*/

        //ShiroFilterFactoryBean 3
        public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager defaultWebSecurityManager){
            ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
            //创建安全管理器
            bean.setSecurityManager(defaultWebSecurityManager);
            return bean;
        }
        //defaultWebSecurityManager 2
        @Bean(name = "securityManager")
        public DefaultWebSecurityManager defaultWebSecurityManager(@Qualifier("userRealm") CustomRealm userRelm){
            System.out.println("securityManager.....");
            DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
            manager.setRealm(userRelm);
            return manager;
        }
        //创建 realm 对象,需要自定义类当前类 1
        @Bean(name = "userRealm")
        public CustomRealm userRelm(){
            System.out.println("shiroConfiguserRealm.....");
            return new CustomRealm();
        }

}
