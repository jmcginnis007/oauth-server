package com.fbm.auth.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.zaxxer.hikari.HikariDataSource;

@SuppressWarnings("deprecation")
@Configuration
@Order(SecurityProperties.BASIC_AUTH_ORDER)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Resource(name="springDataSource")
	private HikariDataSource dataSource;
    
	/* 
	  Passwords in eContacts users/authorities tables are encrypted by Agiapay app using Md5PasswordEncoder.
	  That encoder is no longer considered secure and is not supported starting with Spring Security 5.  So we 
	  created new tables in a different DB to store user info.   We'll have to figure out how to migrate existing users over
	  from the eContacts tables to the new ones.  I don't believe we can convert MD5 passwords created with a salt so we probably
	  have to come up with a way to reset all their passwords somehow.
	  We should use DelegatingPasswordEncoder instead of a specific one.  This will allow for future flexibility.  
	  See https://info.michael-simons.eu/2018/01/13/spring-security-5-new-password-storage-format/
	  and https://spring.io/blog/2017/11/01/spring-security-5-0-0-rc1-released
	 */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

      auth.jdbcAuthentication().dataSource(dataSource)
          .usersByUsernameQuery("select username, password, enabled from users where username=?")
          .authoritiesByUsernameQuery("select username, authority from users u, authorities a where u.user_id = a.user_id and username=?")
      	  .passwordEncoder(delegatingPasswordEncoder());
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
			.antMatchers("/login").permitAll()
			.anyRequest().authenticated()
			.and().formLogin().permitAll()
			.and().csrf().disable();
    }
    
    @Bean
    public PasswordEncoder delegatingPasswordEncoder() {
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("noop", NoOpPasswordEncoder.getInstance());
        DelegatingPasswordEncoder passworEncoder = new DelegatingPasswordEncoder(
          "bcrypt", encoders);
     
        return passworEncoder;
    }

}
