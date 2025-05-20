package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.Interest;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.domain.UserInterest;
import com.example.demo.dto.request.InterestRequestDto;
import com.example.demo.repository.InterestRepository;
import com.example.demo.repository.UserInterestRepository;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class InterestControllerTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private UserInterestRepository userInterestRepository;

    private MockMvc mockMvc;
    private Long userId;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        userInterestRepository.deleteAll();
        interestRepository.deleteAll();
        userRepository.deleteAll();

        User u = userRepository.save(User.builder()
            .name("tester")
            .email("tester@example.com")
            .password("pw")
            .role(Role.USER)
            .build()
        );
        userId = u.getId();
    }

    @Test
    @DisplayName("addInterest : 관심사 등록에 성공하고 DB에 Mapping 기록")
    void addInterest_success() throws Exception {

        InterestRequestDto dto = new InterestRequestDto();
        dto.setUserId(userId);
        dto.setName("AI");
        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/interest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andExpect(status().isOk());

        Interest interest = interestRepository.findByName("AI")
            .orElseThrow();
        assertThat(interest.getName()).isEqualTo("AI");

        List<UserInterest> mappings = userInterestRepository.findByUserId(userId);
        assertThat(mappings).hasSize(1);
        assertThat(mappings.get(0).getInterest().getName()).isEqualTo("AI");
    }
}