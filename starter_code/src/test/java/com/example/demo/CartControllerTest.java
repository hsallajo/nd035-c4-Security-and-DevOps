package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import com.example.demo.security.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CartControllerTest {

    private static final Logger log = LoggerFactory.getLogger(CartControllerTest.class);

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserRepository userRepository;

    @MockBean
    CartRepository cartRepository;

    @MockBean
    ItemRepository itemRepository;

    @MockBean
    UserDetailsServiceImpl userDetailsService;

    @MockBean
    Cart cart;

    @WithMockUser
    @Test
    public void add_item_to_cart() throws Exception{

        ModifyCartRequest testRequest = TestHelp.createModifyCartRequest(
                TestHelp.USERNAME
                , TestHelp.ITEM_1_ID
                , TestHelp.NUMBER_OF_ITEMS);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(testRequest);

        User user = TestHelp.createUser();
        Item item = TestHelp.createItem();

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

        Assert.assertEquals(TestHelp.NUMBER_OF_ITEMS, updatedCart.getItems().size());
        Assert.assertEquals(
                BigDecimal.valueOf(TestHelp.NUMBER_OF_ITEMS * TestHelp.ITEM_1_PRICE),
                updatedCart.getTotal());

        verify(cartRepository, times(1)).save(any());

    }

    @WithMockUser
    @Test
    public void add_item_to_cart_with_invalid_user_throws_exception() throws Exception {

        ModifyCartRequest testRequest = TestHelp.createModifyCartRequest(
                TestHelp.USERNAME
                , TestHelp.ITEM_1_ID
                , TestHelp.NUMBER_OF_ITEMS);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(testRequest);

        when(userRepository.findByUsername(any())).thenReturn(null);

        mockMvc.perform(post(new URI("/api/cart/addToCart/"))
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    public void remove_item_from_cart() throws Exception {

        ModifyCartRequest testRequest = TestHelp.createModifyCartRequest(
                TestHelp.USERNAME
                , TestHelp.ITEM_1_ID
                , TestHelp.NUMBER_OF_ITEMS_TO_BE_REMOVED);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(testRequest);

        User user = TestHelp.createUser();
        Item item = TestHelp.createItem();

        Cart cart = new Cart();
        cart.setUser(user);
        IntStream.range(0, TestHelp.NUMBER_OF_ITEMS).forEach(x -> cart.addItem(item));
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

        Assert.assertEquals(
                TestHelp.NUMBER_OF_ITEMS - TestHelp.NUMBER_OF_ITEMS_TO_BE_REMOVED
                , updatedCart.getItems().size());

        Assert.assertEquals(
                BigDecimal.valueOf((TestHelp.NUMBER_OF_ITEMS - TestHelp.NUMBER_OF_ITEMS_TO_BE_REMOVED) * TestHelp.ITEM_1_PRICE)
                , updatedCart.getTotal());

        verify(cartRepository, times(1)).save(any());

    }

    @WithMockUser
    @Test
    public void remove_item_from_cart_with_invalid_item_throws_exception() throws Exception {

        ModifyCartRequest testRequest = TestHelp.createModifyCartRequest(
                TestHelp.USERNAME
                , 88
                , TestHelp.NUMBER_OF_ITEMS_TO_BE_REMOVED);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(testRequest);

        User user = TestHelp.createUser();

        Optional<Item> item = Optional.empty();

        when(userRepository.findByUsername(any())).thenReturn(user);
        when(itemRepository.findById(any())).thenReturn(item);

        mockMvc.perform(post(new URI("/api/cart/removeFromCart/"))
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }

}
