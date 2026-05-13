package com.url_shortner.services;

import com.url_shortner.dto.UrlMappingDTO;
import com.url_shortner.models.UrlMapping;
import com.url_shortner.models.User;
import com.url_shortner.repository.UrlMappingRepository;
import com.url_shortner.repository.UserRepository;
import com.url_shortner.utility.ShortCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UrlMappingService {

    private final UrlMappingRepository urlMappingRepository;
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
}
