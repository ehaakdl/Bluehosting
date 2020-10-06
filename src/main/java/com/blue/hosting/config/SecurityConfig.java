package com.blue.hosting.config;

import com.blue.hosting.services.ConstPage;
import com.blue.hosting.services.account.AccountAuthFilter;
import com.blue.hosting.services.account.AccountIDAuthProvider;
import com.blue.hosting.services.account.LoginSuccessHandler;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.thymeleaf.dialect.IDialect;

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
                    .authorizeRequests()
                    .anyRequest().permitAll()
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .formLogin()
                    .disable()
                    .addFilterBefore(AccountAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        }catch (Exception except){
            //log
            throw except;
        }
    }

    @Bean
    public AccountAuthFilter AccountAuthenticationFilter() throws Exception {
        AccountAuthFilter accountAuthFilter;
        try {
            accountAuthFilter = new AccountAuthFilter(authenticationManager());
            accountAuthFilter.setFilterProcessesUrl(ConstPage.ACCOUNT_LOGIN);
            accountAuthFilter.setAuthenticationSuccessHandler(LoginSuccessHandler());
            accountAuthFilter.afterPropertiesSet();
        }catch (Exception except){
            //log
            throw except;
        }
        return accountAuthFilter;
    }

    @Bean
    public LoginSuccessHandler LoginSuccessHandler() {
        return new LoginSuccessHandler();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccountIDAuthProvider AccountIDAuthProvider() {
        return new AccountIDAuthProvider(passwordEncoder());
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) {
        authenticationManagerBuilder.authenticationProvider(AccountIDAuthProvider());
    }
}
