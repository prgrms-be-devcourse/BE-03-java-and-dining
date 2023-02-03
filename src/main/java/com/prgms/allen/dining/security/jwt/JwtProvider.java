package com.prgms.allen.dining.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {

	private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);
	private static final String ROLES_SPLIT_REGEX = ",";
	private static final String NICKNAME_KEY = "nickname";
	private static final String ID_KEY = "id";
	private static final String ROLE_KEY = "role";

	private final String issuer;
	private final SecretKey key;
	private final int expirationMillis;

	public JwtProvider(
		@Value("${jwt.issuer}") String issuer,
		@Value("${jwt.secret}") String key,
		@Value("${jwt.expiration_ms}") int expirationMillis
	) {
		this.issuer = issuer;
		this.key = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
		this.expirationMillis = expirationMillis;
	}

	public String generateToken(String nickname, Long memberId, List<GrantedAuthority> authorities) {
		String parsedAuthorities = authorities.stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		long now = (new Date()).getTime();

		Date expiresIn = new Date(now + expirationMillis);
		return Jwts.builder()
			.setIssuer(issuer)
			.claim(NICKNAME_KEY, nickname)
			.claim(ID_KEY, memberId)
			.claim(ROLE_KEY, parsedAuthorities)
			.setExpiration(expiresIn)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public String getToken(Authentication authentication) {
		JwtAuthenticationPrincipal principal = (JwtAuthenticationPrincipal)authentication.getPrincipal();
		return principal.jwtToken();
	}

	public Authentication getAuthentication(String jwtToken) {
		Claims claims = parseClaims(jwtToken);
		Collection<? extends GrantedAuthority> authorities =
			Arrays.stream(claims.get(ROLE_KEY).toString().split(ROLES_SPLIT_REGEX))
				.map(SimpleGrantedAuthority::new)
				.toList();

		JwtAuthenticationPrincipal principal = new JwtAuthenticationPrincipal(
			jwtToken,
			Long.parseLong(
				String.valueOf(claims.get(ID_KEY))
			)
		);
		return JwtAuthenticationToken.authenticated(principal, "", authorities);
	}

	private Claims parseClaims(String accessToken) {
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(accessToken)
			.getBody();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			log.info("Invalid JWT Token", e);
		} catch (ExpiredJwtException e) {
			log.info("Expired JWT Token", e);
		} catch (UnsupportedJwtException e) {
			log.info("Unsupported JWT Token", e);
		} catch (IllegalArgumentException e) {
			log.info("JWT claims string is empty.", e);
		}
		return false;
	}
}
