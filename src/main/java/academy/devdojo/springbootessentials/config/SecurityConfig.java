package academy.devdojo.springbootessentials.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import academy.devdojo.springbootessentials.service.CustomerUserDetailService;
import static academy.devdojo.springbootessentials.config.SecurityConstants.SIGN_UP_URL;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private CustomerUserDetailService customerUserDetailService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().configurationSource(request-> new CorsConfiguration().applyPermitDefaultValues())
		  .and().csrf().disable()
		  .authorizeRequests()
		  .antMatchers(HttpMethod.GET, SIGN_UP_URL).permitAll()
		  .antMatchers("/*/protected/**").hasRole("USER")
		  .antMatchers("/*/admin/**").hasRole("ADMIN")
		  .and()
		  .addFilter(new JWTAuthenticationFilter(authenticationManager()))
		  .addFilter(new JWTAuthorizationFilter(authenticationManager(), customerUserDetailService));
		  
		  
		http.authorizeRequests()
		.antMatchers("/*/protected/**").hasRole("USER")
		.antMatchers("/*/admin/**").hasRole("ADMIN")
		.anyRequest()
		.authenticated()
		.and().httpBasic()
		.and().csrf().disable();
	}
	
	

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(customerUserDetailService).passwordEncoder(new BCryptPasswordEncoder());
	}

}