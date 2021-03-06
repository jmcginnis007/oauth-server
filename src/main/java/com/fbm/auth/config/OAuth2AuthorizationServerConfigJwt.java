package com.fbm.auth.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfigJwt extends AuthorizationServerConfigurerAdapter {

	@Autowired
	@Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;

	@Autowired
	OauthConfig config;

	@Override
	public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.tokenKeyAccess("permitAll()")
		.checkTokenAccess("isAuthenticated()");
	}

	@Override
	public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
				.withClient("fbm-user")
				.secret("{noop}secret-fbm") 
				.authorizedGrantTypes("authorization_code","password", "refresh_token")
				.scopes("read", "write")
				.accessTokenValiditySeconds(3600) // 1 hour
				.refreshTokenValiditySeconds(2592000) // 30 days
			.and()
				.withClient("b2b-example")
				.secret("{noop}b2b")
				.authorizedGrantTypes("authorization_code", "refresh_token")
				.scopes("read")
				.accessTokenValiditySeconds(360) // 6 minutes
				.refreshTokenValiditySeconds(86400); // 1 day
	}

	@Bean
	@Primary
	public DefaultTokenServices tokenServices() {
		final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setTokenStore(tokenStore());
		defaultTokenServices.setSupportRefreshToken(true);
		return defaultTokenServices;
	}

	@Override
	public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
		endpoints.tokenStore(tokenStore()).tokenEnhancer(tokenEnhancerChain)
				.authenticationManager(authenticationManager);
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		final KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(
				new ClassPathResource(config.getKeyStoreName() + ".jks"), config.getKeyStorePass().toCharArray());
		converter.setKeyPair(keyStoreKeyFactory.getKeyPair(config.getKeyStoreName()));
		return converter;
	}

	@Bean
	public TokenEnhancer tokenEnhancer() {
		return new CustomTokenEnhancer();
	}
}
