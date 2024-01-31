package no.delalt.back.configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import no.delalt.back.cache.TokenCache;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private static JWTVerifier verifier;
  private final TokenCache tokenCache;

  public JwtAuthenticationFilter(TokenCache tokenCache, String secretKey) {
    this.tokenCache = tokenCache;

    Algorithm algorithm = Algorithm.HMAC256(
      secretKey.getBytes(StandardCharsets.UTF_8)
    );

    verifier = JWT.require(algorithm).build();
  }

  /**
   * Filters the incoming request and sets the authentication context if a valid token is present in the request header.
   *
   * @param  request       the HTTP servlet request object
   * @param  response      the HTTP servlet response object
   * @param  filterChain   the filter chain object
   * @throws ServletException  if there is a servlet-related error
   * @throws IOException       if there is an I/O related error
   */
  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull FilterChain filterChain
  )
    throws ServletException, IOException {
    String header = request.getHeader("Authorization");

    if (
      header == null ||
      !header.startsWith("Bearer ") ||
      header.trim().length() <= 7
    ) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = header.substring(7);
    String accountID = tokenCache.get(token);

    if (accountID == null) {
      try {
        DecodedJWT jwt = verifier.verify(token);
        accountID = jwt.getClaim("accountID").asString();
        tokenCache.put(token, accountID);
      } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Unauthorized\"}");
        return;
      }
    }

    List<GrantedAuthority> authorities = Collections.singletonList(
      new SimpleGrantedAuthority("USER")
    );
    Authentication authentication = new PreAuthenticatedAuthenticationToken(
      accountID,
      null,
      authorities
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);
    filterChain.doFilter(request, response);
  }
}
