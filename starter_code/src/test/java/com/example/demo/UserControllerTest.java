package com.example.demo;

import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.security.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
    private static final Logger log = LoggerFactory.getLogger(UserControllerTest.class);

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserRepository userRepository;

    @MockBean
    private CartRepository cartRepository;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    UserDetailsServiceImpl userDetailsService;

    @Test
    public void create_new_user() throws Exception{
        log.info("This is basic test.");

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

        verify(userRepository).save(any());
    }
}
