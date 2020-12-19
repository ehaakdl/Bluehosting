package com.blue.hosting.security.config;

import com.blue.hosting.security.manager.ParentProviderManager;
import com.blue.hosting.security.manager.account.AccountProviderManager;
import com.blue.hosting.security.filter.account.AccountLogoutFilter;
import com.blue.hosting.security.handler.account.AccountLogoutHandler;
import com.blue.hosting.utils.PageIndex;
import com.blue.hosting.security.filter.account.AccountLoginAuthFilter;
import com.blue.hosting.security.provider.account.AccountLoginAuthProvider;
import com.blue.hosting.security.handler.account.AccountLoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;

import java.util.LinkedList;


@Configuration
@EnableWebSecurity
@Order(SecurityProperties.BASIC_AUTH_ORDER - 10)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
   @Override
    public void configure(WebSecurity web)  {
       web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder builder, ParentProviderManager parentProviderManager) {
        builder.parentAuthenticationManager(parentProviderManager);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        try {
            http.httpBasic().disable()
                    .csrf().disable()
                    .securityContext().securityContextRepository(getCookieSecurityContextRepository())
                    .and()
                    .authorizeRequests()
                    .antMatchers("/account/login").anonymous()
                    .antMatchers("/**").permitAll()
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .formLogin()
                    .disable()
                    .addFilter(getAccountAuthenticationFilter())
                    .addFilter(getAccountLogoutFilter());

        }catch (Exception except){
            //log
            throw except;
        }
    }

    @Bean
    public CookieSecurityContextRepository getCookieSecurityContextRepository(){
        return new CookieSecurityContextRepository();
    }
    @Bean
    public SpringSecurityDialect springSecurityDialect(){
        return new SpringSecurityDialect();
    }

    @Bean
    public AccountLogoutFilter getAccountLogoutFilter() {
        AccountLogoutFilter accountLogoutFilter = new AccountLogoutFilter(PageIndex.INDEX, getAccountLogoutHandler());
        accountLogoutFilter.setFilterProcessesUrl(PageIndex.LOGOUT);
        return accountLogoutFilter;
    }

    @Bean
    public AccountLogoutHandler getAccountLogoutHandler(){
        return new AccountLogoutHandler();
    }

    @Bean
    public AccountLoginAuthFilter getAccountAuthenticationFilter() throws Exception {
        LinkedList<AuthenticationProvider> providerLink = new LinkedList<>();
        providerLink.add(getAccountIDAuthProvider());
        AccountLoginAuthFilter accountLoginAuthFilter = new AccountLoginAuthFilter(new AccountProviderManager(providerLink));
        accountLoginAuthFilter.setFilterProcessesUrl(PageIndex.LOGIN);
        accountLoginAuthFilter.setAuthenticationSuccessHandler(getLoginSuccessHandler());
        return accountLoginAuthFilter;
    }

    @Bean
    public AccountLoginSuccessHandler getLoginSuccessHandler() {
        return new AccountLoginSuccessHandler();
    }

    @Bean
    public BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccountLoginAuthProvider getAccountIDAuthProvider() {
        return new AccountLoginAuthProvider(getPasswordEncoder());
    }



}
