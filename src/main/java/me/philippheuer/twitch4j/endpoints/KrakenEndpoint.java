package me.philippheuer.twitch4j.endpoints;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import me.philippheuer.twitch4j.TwitchClient;
import me.philippheuer.twitch4j.auth.model.OAuthCredential;
import me.philippheuer.twitch4j.auth.model.twitch.Authorize;
import me.philippheuer.twitch4j.enums.Endpoints;
import me.philippheuer.twitch4j.exceptions.RestException;
import me.philippheuer.twitch4j.model.Token;
import me.philippheuer.twitch4j.model.TokenResponse;
import me.philippheuer.util.rest.HeaderRequestInterceptor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class KrakenEndpoint extends AbstractTwitchEndpoint {

	/**
	 * Class Constructor
	 *
	 * @param client The Twitch Client.
	 */
	public KrakenEndpoint(TwitchClient client) {
		super(client, client.getRestClient().getRestTemplate());
	}

	/**
	 * Endpoint: Get OAuth Token Information
	 * Gets information about the provided oAuthToken
	 * Requires Scope: none
	 *
	 * @param credential The credential the information should be fetched for.
	 * @return Information about the user, that issued the token - also provides info about scopes/valid/etc.
	 * @see Token
	 */
	public Token getToken(OAuthCredential credential) {
		RestTemplate restTemplate = this.restTemplate;

		// Parameters
		restTemplate.getInterceptors().add(new HeaderRequestInterceptor("Authorization", String.format("OAuth %s", credential.getToken())));

		// REST Request
		try {
			return restTemplate.getForObject("/", TokenResponse.class).getToken();
		} catch (Exception ex) {
			log.error("Request failed: " + ex.getMessage());
			log.trace(ExceptionUtils.getStackTrace(ex));

			// Default Response: Invalid Token
			return new Token();
		}
	}


	/**
	 * Gets/refreshes the token
	 *
	 * @param grant_type Valid values: authorization_code or refresh_token.
	 * @param redirect_url Redirect url.
	 * @param code authentication_code or refresh_token
	 * @return {@link Authorize} data on {@link Optional} container class
	 */
	public Optional<Authorize> getOAuthToken(String grant_type, String redirect_url, String code) {
		// Endpoint
		String requestUrl = String.format("%s/oauth2/token", Endpoints.API.getURL());
		RestTemplate restTemplate = client.getRestClient().getPlainRestTemplate();

		// Post Data
		MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
		postParameters.add("grant_type", grant_type);
		postParameters.add("client_id", client.getClientId());
		postParameters.add("client_secret", client.getClientSecret());
		postParameters.add("redirect_uri", redirect_url);
		if(grant_type.equals("authorization_code")) {
			postParameters.add("code", code);
		} else if(grant_type.equals("refresh_token")) {
			postParameters.add("refresh_token", code);
		}

		// REST Request
		try {
			Authorize responseObject = restTemplate.postForObject(requestUrl, postParameters, Authorize.class);

			return Optional.ofNullable(responseObject);
		} catch (RestException restException) {
			log.error("RestException: " + restException.getRestError().toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return Optional.empty();
	}

}
