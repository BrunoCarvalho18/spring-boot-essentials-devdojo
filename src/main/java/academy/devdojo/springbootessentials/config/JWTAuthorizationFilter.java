package academy.devdojo.springbootessentials.config;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import academy.devdojo.springbootessentials.service.CustomerUserDetailService;
import io.jsonwebtoken.Jwts;
import static academy.devdojo.springbootessentials.config.SecurityConstants.*;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	private final CustomerUserDetailService customUserDetailService;

	public JWTAuthorizationFilter(AuthenticationManager authenticationManager,CustomerUserDetailService customUserDetailService) {
		super(authenticationManager);
		this.customUserDetailService = customUserDetailService;
	}
	
	 @Override
	    protected void doFilterInternal(HttpServletRequest request,
	                                    HttpServletResponse response,
	                                    FilterChain chain) throws IOException, ServletException {
	        String header = request.getHeader(HEADER_STRING);
	        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
	            chain.doFilter(request, response);
	            return;
	        }
	        UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken(request);
	        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	        chain.doFilter(request, response);
	    }

	    private UsernamePasswordAuthenticationToken getAuthenticationToken(HttpServletRequest request) {
	        String token = request.getHeader(HEADER_STRING);
	        if (token == null) return null;
	        String username = Jwts.parser().setSigningKey(SECRET)
	                .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
	                .getBody()
	                .getSubject();
	        UserDetails userDetails = customUserDetailService.loadUserByUsername(username);
	        return username != null ?
	                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()) : null;
	    }

}
