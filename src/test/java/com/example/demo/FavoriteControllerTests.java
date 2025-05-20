package com.example.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.example.demo.domain.Favorite;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.dto.request.FavoriteRequestDto;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class FavoriteControllerTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        favoriteRepository.deleteAll();
        userRepository.deleteAll();

        userRepository.save(
            User.builder()
                .name("tester")
                .email("tester@example.com")
                .password("pw")
                .role(Role.USER)
                .build()
        );
    }

    
    @Test
    @DisplayName("addFavorite: 좋아요 추가 toggle")
    public void addFavorite() throws Exception {

        final String url = "/favorite";
        
        Long userId = userRepository.findAll().get(0).getId();
        
        FavoriteRequestDto dto = new FavoriteRequestDto();
        dto.setUserId(userId);
        dto.setNewsTitle("테스트 제목");
        dto.setNewsSummary("테스트 요약");
        dto.setNewsLink("http://example.com");
        dto.setNewsThumbnail("thumb.png");
        dto.setNewsCategory("news");

        String requestBody = objectMapper.writeValueAsString(dto);

        
        ResultActions result = mockMvc.perform(
            post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        ).andDo(print());

        
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.newsTitle").value("테스트 제목"))
              .andExpect(jsonPath("$.newsCategory").value("news"));

        
        List<Favorite> favorites = favoriteRepository.findAll();
        assertThat(favorites).hasSize(1);

        Favorite saved = favorites.get(0);
        assertThat(saved.getUser().getId()).isEqualTo(userId);
        assertThat(saved.getNewsTitle()).isEqualTo("테스트 제목");
        assertThat(saved.getNewsCategory()).isEqualTo("news");
    }

    @Test
    @DisplayName("remote : 두번째 좋아요 클릭 시 좋아요 취소")
    void remoteFavorite() throws Exception{

        final String url = "/favorite";
        
        Long userId = userRepository.findAll().get(0).getId();

        FavoriteRequestDto dto = new FavoriteRequestDto();
        dto.setUserId(userId);
        dto.setNewsTitle("테스트 제목");
        dto.setNewsSummary("테스트 요약");
        dto.setNewsLink("http://example.com");
        dto.setNewsThumbnail("thumb.png");
        dto.setNewsCategory("news");

        String requestBody = objectMapper.writeValueAsString(dto);

        // 첫 번째 좋아요(add)
        mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
        ).andExpect(status().isOk());

        // 두 번째 좋아요(cancel)
        mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
        ).andDo(print())
        .andExpect(status().isNoContent());

        assertThat(favoriteRepository.findAll()).isEmpty();
    }

    
    @Test
    @DisplayName("findAllFavorites: 내 좋아요 목록 조회 시 리스트 반환")
    public void getFavorites_success() throws Exception {
        User user = userRepository.findAll().get(0);
        Favorite fav1 = favoriteRepository.save(
            Favorite.builder()
                .user(user)
                .newsTitle("A")
                .newsSummary("sumA")
                .newsLink("l1")
                .newsThumbnail("t1")
                .newsCategory("c1")
                .build()
        );
        Favorite fav2 = favoriteRepository.save(
            Favorite.builder()
                .user(user)
                .newsTitle("B")
                .newsSummary("sumB")
                .newsLink("l2")
                .newsThumbnail("t2")
                .newsCategory("c2")
                .build()
        );
        System.out.println(fav1);
        System.out.println(fav2);

        ResultActions result = mockMvc.perform(
            get("/{userId}/favorites", user.getId())
        ).andDo(print());

        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.length()").value(2))
              .andExpect(jsonPath("$[0].newsTitle").value("A"))
              .andExpect(jsonPath("$[1].newsTitle").value("B"));
    }

    
    @Test
    @DisplayName("cancelFavorite: 좋아요 취소")
    public void cancelFavorite_success() throws Exception {
        User user = userRepository.findAll().get(0);
        Favorite fav = favoriteRepository.save(
            Favorite.builder()
                .user(user)
                .newsTitle("C")
                .newsSummary("sumC")
                .newsLink("l3")
                .newsThumbnail("t3")
                .newsCategory("c3")
                .build()
        );

        mockMvc.perform(
            delete("/favorite/{id}", fav.getId())
        ).andDo(print())
         .andExpect(status().isNoContent());

        assertThat(favoriteRepository.findAll()).isEmpty();
    }

}
