package com.blue.hosting.config;

import com.blue.hosting.security.CustomSecurityContextRepository;
import com.blue.hosting.security.exception.AuthenticationEndpointImpl;
import com.blue.hosting.security.ConstFilterValue;
import com.blue.hosting.security.CustomAnonymousAuthenticationFilter;
import com.blue.hosting.security.CustomProviderManager;
import com.blue.hosting.security.logout.AccountLogoutFilter;
import com.blue.hosting.security.logout.AccountLogoutHandler;
import com.blue.hosting.utils.ConstPage;
import com.blue.hosting.security.login.AccountLoginAuthFilter;
import com.blue.hosting.security.login.AccountLoginAuthProvider;
import com.blue.hosting.security.login.LoginSuccessHandler;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;

import java.util.LinkedList;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
@Order(SecurityProperties.BASIC_AUTH_ORDER - 10)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        AuthenticationManager authManager;
        try{
            authManager = super.authenticationManagerBean();
        }catch (Exception except){
            //log
            throw except;
        }
        return authManager;
    }

   @Override
    public void configure(WebSecurity web)  {
       web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        try {
            http.csrf().disable()
                    .exceptionHandling()
                    .authenticationEntryPoint(AuthenticationEndpointImpl())
                    .and()
                    .securityContext().securityContextRepository(CustomSecurityContextRepository())
                    .and()
                    .authorizeRequests()
                    .antMatchers("/account/login").hasRole("USER")
                    .antMatchers("/**").permitAll()
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .formLogin()
                    .disable()
                    .addFilterBefore(AccountAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(AccountLogoutFilter(), LogoutFilter.class)
                    .addFilter(CustomAnonymousAuthenticationFilter());
        }catch (Exception except){
            //log
            throw except;
        }
    }

    @Bean
    public CustomSecurityContextRepository CustomSecurityContextRepository(){
        return new CustomSecurityContextRepository();
    }
    @Bean
    public SpringSecurityDialect springSecurityDialect(){
        return new SpringSecurityDialect();
    }

    @Bean
    public CustomAnonymousAuthenticationFilter CustomAnonymousAuthenticationFilter(){
        return new CustomAnonymousAuthenticationFilter(ConstFilterValue.ANONYMOUS_KEY);
    }


    @Bean
    public AuthenticationEndpointImpl AuthenticationEndpointImpl(){
        AuthenticationEndpointImpl authenticationEndpoint = new AuthenticationEndpointImpl();
        return authenticationEndpoint;
    }

    @Bean
    public AccountLogoutFilter AccountLogoutFilter() {
        AccountLogoutFilter accountLogoutFilter = new AccountLogoutFilter(ConstPage.INDEX, getAccountLogoutHandler());
        accountLogoutFilter.setFilterProcessesUrl(ConstPage.LOGOUT);
        return accountLogoutFilter;
    }

    @Bean
    public AccountLogoutHandler getAccountLogoutHandler(){
        return new AccountLogoutHandler();
    }

    @Bean
    public AccountLoginAuthFilter AccountAuthenticationFilter() throws Exception {
        LinkedList<AuthenticationProvider> providerLink = new LinkedList<>();
        providerLink.add(getAccountIDAuthProvider());
        AccountLoginAuthFilter accountLoginAuthFilter = new AccountLoginAuthFilter(new CustomProviderManager(providerLink));
        accountLoginAuthFilter.setFilterProcessesUrl(ConstPage.LOGIN);
        accountLoginAuthFilter.setAuthenticationSuccessHandler(getLoginSuccessHandler());
        accountLoginAuthFilter.afterPropertiesSet();

        return accountLoginAuthFilter;
    }

    @Bean
    public LoginSuccessHandler getLoginSuccessHandler() {
        return new LoginSuccessHandler();
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
