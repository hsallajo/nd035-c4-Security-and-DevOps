package com.example.demo;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.security.UserDetailsServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserRepository userRepository;

    @MockBean
    OrderRepository orderRepository;

    @MockBean
    UserDetailsServiceImpl userDetailsService;

    @MockBean
    UserOrder userOrder;

    @WithMockUser
    @Test
    public void submit_order_with_valid_user() throws Exception {

        User user = TestHelp.createUser();

        Cart cart = new Cart();
        cart.setUser(user);
        cart.addItem(TestHelp.createItem1());
        user.setCart(cart);

        when(userRepository.findByUsername(any())).thenReturn(user);

        mockMvc.perform(post("/api/order/submit/" + TestHelp.USERNAME))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].name").value(TestHelp.ITEM_1_NAME));

        verify(orderRepository, times(1)).save(any());
        verify(orderRepository).save(argThat((UserOrder o) -> o.getItems().size() == cart.getItems().size()));

    }

    @WithMockUser
    @Test
    public void get_history_with_valid_user() throws Exception {

        User user = TestHelp.createUser();

        List<UserOrder> orders = new ArrayList<>();

        when(userRepository.findByUsername(TestHelp.USERNAME)).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);

        mockMvc.perform(get("/api/order/history/{username}",TestHelp.USERNAME))
                .andExpect(status().isOk());

        verify(orderRepository, times(1)).findByUser(user);
    }

}
