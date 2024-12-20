package com.example.application.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

import com.example.application.service.MobUserUserDetailsService;
import com.example.application.views.login.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true) 
public class SecurityConfig
extends VaadinWebSecurity
{
	
	
	
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
	    super.configure(http);
	  //  http.authorizeHttpRequests().requestMatchers("/users/register").permitAll();
	    setLoginView(http, LoginView.class);

	  
	}
 
	
//	  @Bean	   
//	  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//	        return http.csrf().disable()
//	                .authorizeHttpRequests()
//	                .requestMatchers("/products/welcome","/products/new","/users/register","/login","/users/new").permitAll()
//	                .and()
//	                .authorizeHttpRequests().requestMatchers("/products/**")
//	                .authenticated().and().formLogin().and().build();
//    }
	    
//	    @Bean
//	    public AuthenticationProvider provide() {
//	    	
//	    	
//	    	DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//	    	provider.setUserDetailsService(userDetailsService());
//	    	provider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
//	    	
//	    	return provider;
//	    	
//	    }
	
	    @Bean
	    public UserDetailsService userDetailsService() {
	       // return new UserInfoUserDetailsService();
	        return new MobUserUserDetailsService();
	    }
	
	@Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
        return authenticationProvider;
    }
	@Bean
    public AuthenticationManager manger(AuthenticationConfiguration config) throws Exception {
    	
    	return config.getAuthenticationManager();
    	
    }
	    
//	    @Bean
//	    public UserDetailsService userDetailsService() {
//	        return new UserService();
//	    }
}
