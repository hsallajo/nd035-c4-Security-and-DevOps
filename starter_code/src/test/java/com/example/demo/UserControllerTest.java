package com.example.demo;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.security.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {
    private static final Logger log = LoggerFactory.getLogger(UserControllerTest.class);

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserRepository userRepository;

    @MockBean
    CartRepository cartRepository;

    @MockBean
    BCryptPasswordEncoder passwordEncoder;

    @MockBean
    UserDetailsServiceImpl userDetailsService;


    @WithMockUser
    @Test
    public void creates_new_user_with_valid_credentials() throws Exception{

        Object o = new Object(){
            public String username = "jane";
            public String password = "qwertyui2";
            public String confirmPassword = "qwertyui2";
        };

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(o);


        mockMvc.perform(post(new URI("/api/user/create/"))
        .content(json)
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));

        verify(userRepository).save(argThat((User u) -> u.getUsername().equals("jane")));
        verify(userRepository, times(1)).save(any());
        verify(passwordEncoder).encode(argThat((String password) -> password.equals("qwertyui2")));
        verify(passwordEncoder, times(1)).encode(any());
    }

    @WithMockUser
    @Test
    public void create_new_user_with_alphabet_only_password_throws_exception() throws Exception{

        Object o = new Object(){
            public String username = "jane";
            public String password = "qwertyui";
            public String confirmPassword = "qwertyui";
        };

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(o);

        mockMvc.perform(post(new URI("/api/user/create/"))
                .content(json)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @Test
    public void create_new_user_with_numbers_only_password_throws_exception() throws Exception{

        Object o = new Object(){
            public String username = "jane";
            public String password = "12345678";
            public String confirmPassword = "12345678";
        };

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(o);

        mockMvc.perform(post(new URI("/api/user/create/"))
                .content(json)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @Test
    public void create_new_user_with_too_short_password_throws_exception() throws Exception{

        Object o = new Object(){
            public String username = "jane";
            public String password = "abc123";
            public String confirmPassword = "abc123";
        };

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(o);

        mockMvc.perform(post(new URI("/api/user/create/"))
                .content(json)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @Test
    public void gets_user_with_id() throws Exception {

        User u = new User();
        u.setUsername("jane");
        u.setId(1);

        when(userRepository.findById(any())).thenReturn(java.util.Optional.of(u));

        mockMvc.perform(get("/api/user/id/{id}","1"))
                .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("jane"));

    }

    @WithMockUser
    @Test
    public void gets_user_with_existing_username() throws Exception{

        User u = new User();
        u.setUsername("jane");
        u.setId(1);

        when(userRepository.findByUsername(any())).thenReturn(u);

        mockMvc.perform(get("/api/user/{username}","jane"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{}"));

        verify(userRepository, times(1)).findByUsername(any());
    }

    @WithMockUser
    @Test
    public void gets_user_with_non_existent_username() throws Exception{

        when(userRepository.findByUsername(any())).thenReturn(null);

        mockMvc.perform(get("/api/user/{username}","na"))
                .andExpect(status().isNotFound());

        verify(userRepository, times(1)).findByUsername(any());
    }

}


