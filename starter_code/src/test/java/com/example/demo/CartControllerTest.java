package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class CartControllerTest {

    private static final Logger log = LoggerFactory.getLogger(CartControllerTest.class);

    public static final String USERNAME = "jane";
    public static final int USER_ID = 1;
    public static final long ITEM_1_ID = 1L;
    public static final String ITEM_1_NAME = "test item";
    public static final double ITEM_1_PRICE = 2.0;
    public static final String ITEM_1_DESCRIPTION = "Perfect item for any use.";
    public static final int NUMBER_OF_ITEMS = 3;
    public static final int NUMBER_OF_ITEMS_TO_BE_REMOVED = 2;


    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserRepository userRepository;

    @MockBean
    CartRepository cartRepository;

    @MockBean
    ItemRepository itemRepository;

    @MockBean
    Cart cart;

    @Test
    public void add_item_to_cart() throws Exception{

        ModifyCartRequest testRequest = createModifyCartRequest(USERNAME, ITEM_1_ID, NUMBER_OF_ITEMS);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(testRequest);

        User user = createUser();
        Item item = createItem();

        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);

        when(userRepository.findByUsername(any())).thenReturn(user);
        when(itemRepository.findById(any())).thenReturn(java.util.Optional.of(item));

        MvcResult res = mockMvc.perform(post(new URI("/api/cart/addToCart/"))
        .content(json)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isOk())
                .andReturn();

        String response = res.getResponse().getContentAsString();
        Cart updatedCart = new ObjectMapper().readValue(response, Cart.class);

        Assert.assertEquals(NUMBER_OF_ITEMS, updatedCart.getItems().size());
        Assert.assertEquals(
                BigDecimal.valueOf(NUMBER_OF_ITEMS*ITEM_1_PRICE),
                updatedCart.getTotal());

        verify(cartRepository, times(1)).save(any());

    }

    @Test
    public void add_item_to_cart_with_invalid_user_throws_exception() throws Exception {

        ModifyCartRequest testRequest = createModifyCartRequest(USERNAME, ITEM_1_ID, NUMBER_OF_ITEMS);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(testRequest);

        when(userRepository.findByUsername(any())).thenReturn(null);

        mockMvc.perform(post(new URI("/api/cart/addToCart/"))
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }

    @Test
    public void remove_item_from_cart() throws Exception {

        ModifyCartRequest testRequest = createModifyCartRequest(USERNAME, ITEM_1_ID, NUMBER_OF_ITEMS_TO_BE_REMOVED);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(testRequest);

        User user = createUser();
        Item item = createItem();

        Cart cart = new Cart();
        cart.setUser(user);
        IntStream.range(0, NUMBER_OF_ITEMS).forEach(x -> cart.addItem(item));
        user.setCart(cart);

        when(userRepository.findByUsername(any())).thenReturn(user);
        when(itemRepository.findById(any())).thenReturn(java.util.Optional.of(item));

        MvcResult res = mockMvc.perform(post(new URI("/api/cart/removeFromCart/"))
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();

        String response = res.getResponse().getContentAsString();
        Cart updatedCart = new ObjectMapper().readValue(response, Cart.class);

        Assert.assertEquals(NUMBER_OF_ITEMS - NUMBER_OF_ITEMS_TO_BE_REMOVED, updatedCart.getItems().size());
        Assert.assertEquals(
                BigDecimal.valueOf((NUMBER_OF_ITEMS - NUMBER_OF_ITEMS_TO_BE_REMOVED) * ITEM_1_PRICE),
                updatedCart.getTotal());

        verify(cartRepository, times(1)).save(any());

    }

    @Test
    public void remove_item_from_cart_with_invalid_item_throws_exception() throws Exception {

        ModifyCartRequest testRequest = createModifyCartRequest(USERNAME, 88, NUMBER_OF_ITEMS_TO_BE_REMOVED);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(testRequest);

        User user = createUser();

        Optional<Item> item = Optional.empty();

        when(userRepository.findByUsername(any())).thenReturn(user);
        when(itemRepository.findById(any())).thenReturn(item);

        mockMvc.perform(post(new URI("/api/cart/removeFromCart/"))
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }

    /*helper methods*/

    private ModifyCartRequest createModifyCartRequest(String username, long itemId, int quantity){

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(username);
        request.setItemId(itemId);
        request.setQuantity(quantity);
        return request;
    }

    private User createUser(){

        User user = new User();
        user.setUsername(USERNAME);
        user.setId(USER_ID);
        return user;
    }

    private Item createItem(){

        Item i = new Item();
        i.setId(ITEM_1_ID);
        i.setName(ITEM_1_NAME);
        i.setDescription(ITEM_1_DESCRIPTION);
        i.setPrice(BigDecimal.valueOf(ITEM_1_PRICE));
        return i;
    }

}
