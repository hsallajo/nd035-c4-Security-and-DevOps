package com.example.demo.controllers;

import java.util.List;

import com.example.demo.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

import static com.example.demo.controllers.DemoAppConstants.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {

	private static final Logger log = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {

		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.info(API_TAG + '=' + ORDER_API + ","
					+ ERR_TAG + "=" + "'user has null value'" + ","
					+ MSG_TAG + "=" + "'" + username + "'' not found. Submit failed.'");

			throw new UserNotFoundException("User not found.");
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());

		orderRepository.save(order);
		log.info(API_TAG + '=' + ORDER_API + ","
				+ MSG_TAG + "=" + "'submitted successfully'" + ","
				+ USER_ID + "=" + user.getId() + ","
				+ USERNAME + "=" + user.getUsername() + ","
				+ ORDER_ID + "=" + order.getId());

		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getUserOrders(@PathVariable String username) {

		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.info(API_TAG + '=' + ORDER_API + ","
					+ ERR_TAG + "=" + "user '" + username + "'' not found. Get_user_orders failed.'");

			throw new UserNotFoundException("User not found.");
		}

		return ResponseEntity.ok(orderRepository.findByUser(user));
	}
}
