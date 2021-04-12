package lt.irmantasm.nfqtask.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;


import java.net.URI;
import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    ReactiveUserDetailsService userDetailsService;

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager manager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        manager.setPasswordEncoder(new BCryptPasswordEncoder(5));
        return manager;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChaintwo(ServerHttpSecurity http) {
        http
                .csrf()
                .and()
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/").permitAll()
                        .pathMatchers(HttpMethod.GET, "/css/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/scripts/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/images/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/favicon.ico").permitAll()
                        .pathMatchers("/specialist/register").permitAll()
                        .pathMatchers("/specialist/login").permitAll()
                        .pathMatchers("/specialist/failure").permitAll()
                        .pathMatchers("/specialist/success").permitAll()
                        .pathMatchers("/customer/register").permitAll()
                        .pathMatchers("/customer/success").permitAll()
                        .pathMatchers("/customer/failure").permitAll()
                        .pathMatchers("/customer/add").permitAll()
                        .pathMatchers("/visits/sse").permitAll()
                        .anyExchange().authenticated())
                .httpBasic(Customizer.withDefaults())
                .formLogin(formLoginSpec -> formLoginSpec.loginPage("/specialist/login")
                        .authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler("/specialist/success"))
                        .authenticationFailureHandler(new RedirectServerAuthenticationFailureHandler("/specialist/failure")))
                .logout(logoutSpec -> logoutSpec
                        .logoutUrl("/specialist/logout")
                        .logoutSuccessHandler(logoutSuccessHandlerTwo()));
        return http.build();
    }

    @Bean
    RedirectServerLogoutSuccessHandler logoutSuccessHandlerTwo(){
        RedirectServerLogoutSuccessHandler handler = new RedirectServerLogoutSuccessHandler();
        handler.setLogoutSuccessUrl(URI.create("/"));
        return handler;
    }


}
