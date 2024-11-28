package com.wasa.api.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wasa.api.config.CutomeUserDetailService;
import com.wasa.api.config.JwtTokenProvider;
import com.wasa.api.model.AuthRequest;
import com.wasa.api.model.WebUser;
import com.wasa.api.util.CustomPasswordEncoder;

import io.sentry.Sentry;

@RequestMapping(value = "api")
@RestController
public class AuthController {
	@Autowired
	CutomeUserDetailService userDetails;
	@Autowired
	JwtTokenProvider jwtTokenProvider;
	@Autowired
	AuthenticationManager authManager;
	@Autowired
	ObjectMapper mapper = new ObjectMapper();
//	CustomPasswordEncoder encoder = new CustomPasswordEncoder();
	@PostMapping(value = "authenticate", produces = "application/json")
	public ResponseEntity<?> authenticate(@RequestBody AuthRequest request) throws JsonProcessingException {
		ObjectNode node = mapper.createObjectNode();
//		node.put("encodedPassword", encoder.encode(request.getPassword()));
		try {
			final WebUser user = userDetails.loadUserByUsername(request.getUsername());
			Authentication authentication = authManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
			node.put("status", "success");
			node.put("username", user.getUsername());
			node.put("token", jwtTokenProvider.generateToken(authentication));
		} catch (DisabledException e) {
			node.put("status", "error");
			node.put("error", "Account inactive.");
			Sentry.captureException(e);
		} catch (BadCredentialsException e) {
			node.put("status", "error");
			node.put("error", "Invalid creditentials.");
			Sentry.captureException(e);
		} catch (UsernameNotFoundException e) {
			node.put("status", "error");
			node.put("error", "User does not exist.");
			Sentry.captureException(e);
		} catch (Exception e) {
			node.put("status", "error");
			node.put("error", e.getMessage());
			Sentry.captureException(e);
		}
		return ResponseEntity.ok().body(mapper.writer().writeValueAsString(node));
	}
}
