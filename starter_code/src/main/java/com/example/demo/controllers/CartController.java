package com.example.demo.controllers;

import java.util.Optional;
import java.util.stream.IntStream;

import com.example.demo.exceptions.ItemNotFoundException;
import com.example.demo.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

import static com.example.demo.controllers.DemoAppConstants.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	private static final Logger log = LoggerFactory.getLogger(CartController.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ItemRepository itemRepository;
	
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {

		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			log.info(API + '=' + CART_API + "/addToCart" + ","
					+ ERR_TAG + "=" + "'illegal request param (user == null)'" + ","
					+ MSG_TAG + "=" + "'user not found'" + ","
					+ USERNAME + "=" + request.getUsername());

			throw new UserNotFoundException("User not found.");
		}

		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			log.info(API + '=' + CART_API + "/addToCart" + ","
					+ ERR_TAG + "=" + "'illegal request param (item == null)'" + ","
					+ MSG_TAG + "=" + "'item not found'" + ","
					+ USER_ID + "=" + user.getId() + ","
					+ ITEM_ID + "=" + request.getItemId());

			throw new ItemNotFoundException("Item not found.");
		}

		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.addItem(item.get()));

		cartRepository.save(cart);

		log.info(API + '=' + CART_API + "/addToCart" + ","
				+ MSG_TAG + "=" + "'item(s) added'" + ","
				+ USER_ID + "=" + user.getId() + ","
				+ ITEM_ID + "=" + item.get().getId());

		return ResponseEntity.ok(cart);
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {

		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			log.info(API + '=' + CART_API + "/removeFromCart" + ","
					+ MSG_TAG + "=" + "'user not found'" + ","
					+ USERNAME + "=" + request.getUsername());

			throw new UserNotFoundException("User not found.");
		}

		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			log.info(API + '=' + CART_API + "/removeFromCart" + ","
					+ ERR_TAG + "=" + "'illegal request param (item == null)'" + ","
					+ MSG_TAG + "=" + "'item not found'" + ","
					+ USER_ID + "=" + user.getId() + ","
					+ ITEM_ID + "=" + request.getItemId());

			throw new ItemNotFoundException("Item not found.");
		}

		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.removeItem(item.get()));

		cartRepository.save(cart);

		log.info(API + '=' + CART_API + "/removeFromCart" + ","
				+ MSG_TAG + "=" + "'item(s) removed'" + ","
				+ USER_ID + "=" + user.getId() + ","
				+ ITEM_ID + "=" + request.getItemId());

		return ResponseEntity.ok(cart);
	}
		
}
