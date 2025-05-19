package com.example.demo.service;

import com.example.demo.common.exception.CustomException;
import com.example.demo.common.exception.ErrorCode;
import com.example.demo.domain.News;
import com.example.demo.dto.response.NewsResponse;
import com.example.demo.dto.response.NewsSummaryResponse;
import com.example.demo.repository.NewsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsService {
    @Value("${naver.client.id}")
    private String CLIENT_ID;
    @Value("${naver.search.key}")
    private String CLIENT_SECRET;

    private final static int TOTAL_ITEM_SIZE = 10;

    private final NewsRepository newsRepository;
    private final OpenAiChatModel openAiChatModel;
    private final ObjectMapper objectMapper;

    public List<NewsResponse> getNews() {
        List<News> newsList = newsRepository.findAll();
        if (newsList.isEmpty()) throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);

        return newsList.stream().map(NewsResponse::from).toList();
    }


    public String getKeyword() {
        List<String> interests = List.of("경제", "IT", "주식", "대선");
        List<News> newsList = newsRepository.findAll();

        String interestStr = String.join(", ", interests);

        String titles = newsList.stream()
                .map(news -> news.getTitle() + " (" + news.getCategory() + ")")
                .collect(Collectors.joining("\n- ", "- ", ""));

        return openAiChatModel.call(
                """
                        사용자의 관심사는 다음과 같습니다: %s
                        
                        오늘의 뉴스 헤드라인과 카테고리는 다음과 같습니다:
                        %s
                        
                        위 정보를 바탕으로, 다음 조건에 따라 네이버 검색 API에 사용할 적절한 검색어를 5개 추천해주세요:
                        
                        - 검색어는 중복되지 않도록 서로 다른 주제를 다뤄야 합니다.
                        - 각 검색어는 하나의 명사 또는 간결한 단어로 구성해주세요 (예: 'AI', '부동산', '일자리').
                        - 검색어는 사용자의 관심사와 오늘 뉴스 헤드라인에 언급된 키워드를 기반으로 도출해주세요.
                        - 관심사와 관련된 키워드에 우선순위를 두되, 오늘 뉴스와 연관된 키워드도 적절히 반영해주세요.
                        - 추천된 검색어는 공백으로 구분된 5개의 단어로만 출력해주세요 (쉼표 없이).
                        
                        예시 출력: AI 부동산 일자리 스타트업 우크라이나
                        """.formatted(interestStr, titles)

        );
    }

    public List<NewsResponse> getSearchNews(String search) {
        List<NewsResponse> dtos = new ArrayList<>();
        String response = naverSearchApi(search, null, null);

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.path("items");
            if (items.isArray()) {
                for (JsonNode item : items) {
                    String link = item.path("link").asText();
                    if (!link.contains("https://n.news.naver.com") && !link.contains("https://news.naver.com")) {
                        //네이버 뉴스기사만 가져옴
                        continue;
                    }
                    String title = Jsoup.parse(item.path("title").asText()).text();
                    String description = Jsoup.parse(item.path("description").asText()).text();
                    String thumbnail = getThumbnail(link);
                    dtos.add(NewsResponse.builder()
                            .title(title)
                            .link(link)
                            .thumbnail(thumbnail)
                            .description(description)
                            .build());
                }
            } else {
                log.info("네이버 뉴스 응답 객체 비었음");
            }
        } catch (Exception e) {
            log.error("[News Service] getSearchNews");
            throw new CustomException(ErrorCode.NEWS_PARSING_ERROR);
        }

        return dtos;
    }

    public List<NewsResponse> getResponse(String keyword) {
        System.out.println("keyword : "+ keyword);
        List<NewsResponse> dtos = new ArrayList<>();
        String[] keywords = keyword.split(" ");
        int perKeywordSize = TOTAL_ITEM_SIZE / keywords.length;

        outer:
        for (String k : keywords) {
            int collected = 0;
            int start = 1;
            int display = 20;

            while (collected < perKeywordSize) {
                String response = naverSearchApi(k, display, start);

                try {
                    JsonNode root = objectMapper.readTree(response);
                    JsonNode items = root.path("items");

                    if (!items.isArray() || items.size() == 0) {
                        // 더 이상 결과가 없음 → 다음 키워드로 넘어감
                        break;
                    }

                    for (JsonNode item : items) {
                        String link = item.path("link").asText();
                        if (!link.contains("https://n.news.naver.com") && !link.contains("https://news.naver.com")) {
                            continue;
                        }
                        if (dtos.stream().anyMatch(dto -> dto.getLink().equals(link))) {
                            continue;
                        }

                        String title = Jsoup.parse(item.path("title").asText()).text();
                        String description = Jsoup.parse(item.path("description").asText()).text();
                        String thumbnail = getThumbnail(link);

                        dtos.add(NewsResponse.builder()
                                .title(title)
                                .link(link)
                                .thumbnail(thumbnail)
                                .description(description)
                                .build());

                        collected++;
                        if (collected >= perKeywordSize || dtos.size() >= TOTAL_ITEM_SIZE) {
                            break;
                        }
                    }

                    // 다음 페이지로 넘어갈 수 있도록 start 증가
                    start += display;

                } catch (Exception e) {
                    log.error("[News Service] getResponse", e);
                    throw new CustomException(ErrorCode.NEWS_PARSING_ERROR);
                }
            }

            if (dtos.size() >= TOTAL_ITEM_SIZE) break outer;
        }

        return dtos;
    }


    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void updateNewsHeadLine() {
        try {
            LocalDateTime batchTime = LocalDateTime.now();

            List<NewsSection> sections = List.of(
                    new NewsSection("https://news.naver.com/section/100", "정치"),
                    new NewsSection("https://news.naver.com/section/101", "경제"),
                    new NewsSection("https://news.naver.com/section/102", "사회"),
                    new NewsSection("https://news.naver.com/section/103", "생활/문화"),
                    new NewsSection("https://news.naver.com/section/104", "세계"),
                    new NewsSection("https://news.naver.com/section/105", "IT/과학")
            );

            List<News> newsList = new ArrayList<>();

            for (NewsSection section : sections) {
                Document doc = Jsoup.connect(section.url()).get();
                Elements newsLists = doc.select("ul[id^=_SECTION_HEADLINE_LIST_]");

                for (Element newsListElem : newsLists) {
                    Elements items = newsListElem.select("li");
                    for (Element item : items) {
                        Element linkElem = item.selectFirst("a.sa_text_title");
                        if (linkElem == null) continue;

                        String title = linkElem.text();
                        String link = linkElem.attr("href");

                        Element thumbnailContainer = item.selectFirst("a.sa_thumb_link img");
                        String thumbnail = thumbnailContainer != null ? thumbnailContainer.attr("data-src") : "";

                        Element summaryElem = item.selectFirst("div.sa_text_lede");
                        String summary = summaryElem != null ? summaryElem.text() : "";

                        newsList.add(News.builder()
                                .title(title)
                                .link(link)
                                .description(summary)
                                .thumbnail(thumbnail)
                                .category(section.name())
                                .createdTime(batchTime)
                                .build());
                    }
                }
            }
            newsRepository.saveAll(newsList);
            newsRepository.deleteByCreatedTimeBefore(batchTime);
        } catch (Exception e) {
            log.error("[News Service] updateNewsHeadLine");
            throw new CustomException(ErrorCode.NEWS_PARSING_ERROR);
        }
    }

    public String naverSearchApi(String query, Integer display, Integer start) {
        if (display == null) display = 10;
        if (start == null) start = 1;
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/news.json")
                .queryParam("query", "이재명")
                .queryParam("display", display)
                .queryParam("start", start)
                .queryParam("sort", "date")
                .encode(Charset.forName("UTF-8"))
                .build()
                .toUri();

        WebClient webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Naver-Client-Id", CLIENT_ID)
                .defaultHeader("X-Naver-Client-Secret", CLIENT_SECRET)
                .build();

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String getThumbnail(String path) {
        try {
            Document doc = Jsoup.connect(path).get();
            org.jsoup.select.Elements newsLists = doc.select("div[id^=img_a1]");
            for (Element news : newsLists) {
                Element img = news.selectFirst("img");
                if (img != null) {
                    String imageUrl = img.attr("data-src");
                    return imageUrl;
                }
            }
            return null;
        } catch (Exception e) {
            log.error("[News Service] getThumbnail");
            throw new CustomException(ErrorCode.NEWS_PARSING_ERROR);
        }
    }

    public NewsSummaryResponse getSummary(String link) {
        try {
            Document doc = Jsoup.connect(link).get();
            Elements article = doc.select("article[id^=dic_area]");

            if (!article.isEmpty()) {
                // 불필요한 태그 제거
                article.select("strong, span, div, em, img, script, style, br").remove();

                // 텍스트 추출 (줄바꿈 없이)
                String plainText = article.text();

                String response = openAiChatModel.call("""
                        아래는 뉴스 기사 본문입니다:
                        ---
                        %s
                        ---
                        
                        이 기사를 한국어로 요약해 주세요.
                        - **300자 내외**로, 가능한 한 **정보를 풍부하게** 담아 주세요.
                        - **핵심 인물, 사건, 발언, 맥락**을 모두 포함해 주세요.
                        - 뉴스 요약문처럼 **간결하지만 상세하게** 정리해 주세요.
                        """.formatted(plainText));

                return NewsSummaryResponse.builder()
                        .summary(response)
                        .build();
            }
            return null;
        } catch (Exception e) {
            log.error("[News Service] getSummary");
            throw new CustomException(ErrorCode.NEWS_PARSING_ERROR);
        }
    }
}