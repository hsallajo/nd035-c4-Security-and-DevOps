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
			log.info(API_TAG + '=' + API_CART + ","
					+ MSG_TAG + "=" + "'user not found'");

			throw new UserNotFoundException("User not found.");
		}

		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			log.info(API_TAG + '=' + API_CART + ","
					+ MSG_TAG + "=" + "'item not found'" + ","
					+ USER_ID + "=" + user.getId() + ","
					+ ITEM_ID + "=" + request.getItemId());

			throw new ItemNotFoundException("Item not found.");
		}

		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.addItem(item.get()));

		cartRepository.save(cart);

		log.info(API_TAG + '=' + API_CART + ","
				+ MSG_TAG + "=" + "'item(s) added successfully'" + ","
				+ USER_ID + "=" + user.getId() + ","
				+ ITEM_ID + "=" + item.get().getId());

		return ResponseEntity.ok(cart);
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {

		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			log.info(API_TAG + '=' + API_CART + ","
					+ MSG_TAG + "=" + "'user not found'");

			throw new UserNotFoundException("User not found.");
		}

		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			log.info(API_TAG + '=' + API_CART + ","
					+ MSG_TAG + "=" + "'item not found'" + ","
					+ USER_ID + "=" + user.getId() + ","
					+ ITEM_ID + "=" + request.getItemId());

			throw new ItemNotFoundException("Item not found.");
		}

		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.removeItem(item.get()));

		cartRepository.save(cart);

		log.info(API_TAG + '=' + API_CART + ","
				+ MSG_TAG + "=" + "'item(s) removed succesfully'" + ","
				+ USER_ID + "=" + user.getId() + ","
				+ ITEM_ID + "=" + request.getItemId());

		return ResponseEntity.ok(cart);
	}
		
}
