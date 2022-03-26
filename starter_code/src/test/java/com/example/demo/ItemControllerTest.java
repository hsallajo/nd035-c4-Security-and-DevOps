package com.example.demo;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    public static final long ITEM_1_ID = 1L;
    public static final String ITEM_1_NAME = "perfect item";
    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemRepository itemRepository;

    @MockBean
    UserDetailsServiceImpl userDetailsService;

    @WithMockUser
    @Test
    public void gets_item_by_valid_item_id() throws Exception {

        Item item = new Item();
        item.setId(ITEM_1_ID);
        item.setName(ITEM_1_NAME);

        when(itemRepository.findById(ITEM_1_ID)).thenReturn(Optional.of(item));

        mockMvc.perform(get("/api/item/{id}","1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(ITEM_1_NAME));

        verify(itemRepository, times(1)).findById(any());
    }

    @WithMockUser
    @Test
    public void gets_item_by_valid_item_name() throws Exception {
        Item item = new Item();
        item.setId(ITEM_1_ID);
        item.setName(ITEM_1_NAME);

        List<Item> list = new ArrayList<>();
        list.add(item);

        when(itemRepository.findByName(ITEM_1_NAME)).thenReturn(list);

        mockMvc.perform(get("/api/item/name/{name}", ITEM_1_NAME))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(ITEM_1_NAME));

        verify(itemRepository, times(1)).findByName(any());
    }

    @WithMockUser
    @Test
    public void gets_all_items() throws Exception {

        Item item = new Item();
        item.setId(ITEM_1_ID);
        item.setName(ITEM_1_NAME);

        List<Item> list = new ArrayList<>();
        list.add(item);

        when(itemRepository.findAll()).thenReturn(list);

        mockMvc.perform(get("/api/item/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        verify(itemRepository, times(1)).findAll();
    }
}
