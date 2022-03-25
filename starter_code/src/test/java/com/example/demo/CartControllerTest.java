package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
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
import java.util.ArrayList;
import java.util.List;

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
    public static final long ITEM_ID = 1L;
    public static final String ITEM_1_NAME = "test item";
    public static final double ITEM_1_PRICE = 2.0;
    public static final String ITEM_1_DESCRIPTION = "Perfect item for any use.";
    public static final int NUMBER_OF_ITEMS = 3;


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

        ModifyCartRequest testRequest = createTestRequest();
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(testRequest);

        User user = createUser();
        Item item = createItem();

        Cart cart = createCart(user, createItemList(item));
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

        ModifyCartRequest testRequest = createTestRequest();
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(testRequest);

        when(userRepository.findByUsername(any())).thenReturn(null);

        mockMvc.perform(post(new URI("/api/cart/addToCart/"))
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }

    /*helper methods*/

    private ModifyCartRequest createTestRequest(){

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(USERNAME);
        request.setItemId(ITEM_ID);
        request.setQuantity(NUMBER_OF_ITEMS);
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
        i.setId(ITEM_ID);
        i.setName(ITEM_1_NAME);
        i.setDescription(ITEM_1_DESCRIPTION);
        i.setPrice(BigDecimal.valueOf(ITEM_1_PRICE));
        return i;
    }

    private List<Item> createItemList(Item item){

        List<Item> items = new ArrayList<>();
        return items;
    }

    private Cart createCart(User user, List<Item> items){

        Cart cart = new Cart();
        cart.setItems(items);
        cart.setUser(user);
        return cart;
    }
}
