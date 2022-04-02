package com.example.demo.controllers;

import com.example.demo.exceptions.InvalidPasswordException;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import static com.example.demo.controllers.DemoAppConstants.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {

		return ResponseEntity.of(userRepository.findById(id));
	}

	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {

		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}

	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {

		User user = new User();
		user.setUsername(createUserRequest.getUsername());

		if (createUserRequest.getPassword() == null
				|| createUserRequest.getConfirmPassword() == null
				|| !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())){
			log.info(API + '=' + USER_API + "/createUser" + ","
					+ ERR_TAG + "=" + "'invalid password'" + ","
					+ MSG_TAG + "=" + "'create user failed'" + ","
					+ USERNAME + "=" + user.getUsername());

			throw new InvalidPasswordException(INVALID_PASSWORD_NO_MATCH);
		}

		if (createUserRequest.getPassword().length() < 8
				|| StringUtils.isAlpha(createUserRequest.getPassword())
				|| StringUtils.isNumeric(createUserRequest.getPassword())){
			log.info(API + '=' + USER_API + "/createUser" + ","
					+ ERR_TAG + "=" + "'invalid password'" + ","
					+ MSG_TAG + "=" + "'create user failed'" + ","
					+ USERNAME + "=" + user.getUsername());

			throw new InvalidPasswordException(INVALID_PASSWORD_TOO_SHORT);
		}

		user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));

		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);

		userRepository.save(user);

		log.info(API + '=' + USER_API + "/createUser" + ","
				+ MSG_TAG + "=" + "'user created'" + ","
				+ USERNAME + "=" + user.getUsername() + ","
				+ USER_ID + "=" + user.getId());

		return ResponseEntity.ok(user);
	}

}
