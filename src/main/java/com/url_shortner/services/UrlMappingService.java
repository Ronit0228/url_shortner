package com.url_shortner.services;

import com.url_shortner.dto.ClickEventDTO;
import com.url_shortner.dto.UrlMappingDTO;
import com.url_shortner.models.ClickEvent;
import com.url_shortner.models.UrlMapping;
import com.url_shortner.models.User;
import com.url_shortner.repository.ClickEventRepository;
import com.url_shortner.repository.UrlMappingRepository;
import com.url_shortner.utility.ShortCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UrlMappingService {

    private final UrlMappingRepository urlMappingRepository;
    private final ClickEventRepository clickEventRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final ModelMapper modelMapper;

    @Transactional
    public UrlMappingDTO createShortUrl(String originalUrl, User user){
        String shortUrl = generateShortUrl();

        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setOriginalUrl(originalUrl);
        urlMapping.setShortUrl(shortUrl);
        urlMapping.setUser(user);
        urlMapping.setCreateAt(LocalDateTime.now());

        UrlMapping saveUrlMapping = urlMappingRepository.save(urlMapping);

        UrlMappingDTO urlMappingDTO = modelMapper.map(saveUrlMapping, UrlMappingDTO.class);
        urlMappingDTO.setUsername(saveUrlMapping.getUser().getUsername());

        return urlMappingDTO;
    }

    @Transactional
    public List<UrlMappingDTO> allUrlOfLoginUser(User user){
        List<UrlMappingDTO> list = new ArrayList<>();
        for (UrlMapping element : urlMappingRepository.findByUser(user)) {
            UrlMappingDTO map = modelMapper.map(element, UrlMappingDTO.class);
            map.setUsername(element.getUser().getUsername());
            list.add(map);
        }
        return list;
    }

    private String generateShortUrl() {
        return shortCodeGenerator.generateShortUrl();
    }

    public List<ClickEventDTO> getClickEventsByDate(String shortUrl, LocalDateTime start, LocalDateTime end) {
        UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl);

        if(urlMapping != null) {
            List<ClickEvent> clickEvent = clickEventRepository.findByUrlMappingAndClickAtBetween(urlMapping, start, end);
            return clickEvent
                    .stream()
                    .map(element -> modelMapper.map(element, ClickEventDTO.class))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    public Map<LocalDate, Long> getTotalClickByUserAndDate(User user, LocalDate start, LocalDate end) {
        List<UrlMapping> urlMappings = urlMappingRepository.findByUser(user);
        List<ClickEvent> clickEvents = clickEventRepository.findByUrlMappingInAndClickAtBetween(urlMappings, start.atStartOfDay(), end.plusDays(1).atStartOfDay());

        return clickEvents.stream()
                .collect(Collectors.groupingBy(
                        clickEvent -> clickEvent.getClickAt().toLocalDate(),
                        Collectors.counting()
                ));
    }

    public UrlMapping getOriginalUrl(String shortUrl) {
        UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl);

        if(urlMapping != null){
            urlMapping.setClickCount(urlMapping.getClickCount() + 1);
            urlMappingRepository.save(urlMapping);

            // Record click event
            ClickEvent clickEvent = new ClickEvent();
            clickEvent.setClickAt(LocalDateTime.now());
            clickEvent.setUrlMapping(urlMapping);
            clickEventRepository.save(clickEvent);
        }
        return urlMapping;
    }
}
