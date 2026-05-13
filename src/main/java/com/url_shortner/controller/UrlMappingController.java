package com.url_shortner.controller;

import com.url_shortner.dto.UrlMappingDTO;
import com.url_shortner.models.User;
import com.url_shortner.services.UrlMappingService;
import com.url_shortner.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/urls")
@RequiredArgsConstructor
public class UrlMappingController {

    private final UrlMappingService urlMappingService;
    private final UserService userService;

    // Original Url in Map : {"OriginalUrl" : "https://example.com"}
    @PostMapping("/shorter")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UrlMappingDTO> createShortUrl(@RequestBody Map<String, String> urlRequest, Principal principal){
        String originalUrl = urlRequest.get("OriginalUrl");
        User user = userService.findByUsername(principal.getName());
        UrlMappingDTO urlMappingDTO = urlMappingService.createShortUrl(originalUrl, user);
        return ResponseEntity.ok(urlMappingDTO);
    }

    @GetMapping("/myurls")
    public ResponseEntity<List<UrlMappingDTO>> allLongUrlOfLoginUser(Principal principal){
        User user = userService.findByUsername(principal.getName());
        List<UrlMappingDTO> allUrls = urlMappingService.allUrlOfLoginUser(user);
        return ResponseEntity.ok(allUrls);
    }
}
