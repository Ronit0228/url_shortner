package com.url_shortner.controller;

import com.url_shortner.dto.ClickEventDTO;
import com.url_shortner.dto.UrlMappingDTO;
import com.url_shortner.models.User;
import com.url_shortner.services.UrlMappingService;
import com.url_shortner.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<UrlMappingDTO>> allLongUrlOfLoginUser(Principal principal){
        User user = userService.findByUsername(principal.getName());
        List<UrlMappingDTO> allUrls = urlMappingService.allUrlOfLoginUser(user);

        if(allUrls != null) return new ResponseEntity<>(allUrls, HttpStatus.FOUND);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/analytics/{shortUrl}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ClickEventDTO>> getUrlAnalytics(@PathVariable String shortUrl,
                                                         @RequestParam("startDate") String startDate,
                                                         @RequestParam("endDate") String endDate) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime start = LocalDateTime.parse(startDate, dateTimeFormatter);
        LocalDateTime end = LocalDateTime.parse(endDate, dateTimeFormatter);

        List<ClickEventDTO> clickEventDTOS = urlMappingService.getClickEventsByDate(shortUrl, start, end);

        if(clickEventDTOS != null) return new ResponseEntity<>(clickEventDTOS, HttpStatus.FOUND);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/totalClicks")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<LocalDate, Long>> getTotalClickByDate(Principal principal,
                                                            @RequestParam("startDate") String startDate,
                                                            @RequestParam("endDate") String endDate){

        User user = userService.findByUsername(principal.getName());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDate start = LocalDate.parse(startDate, dateTimeFormatter);
        LocalDate end = LocalDate.parse(endDate, dateTimeFormatter);

        Map<LocalDate, Long> totalClicks = urlMappingService.getTotalClickByUserAndDate(user, start, end);

        if(totalClicks != null) return new ResponseEntity<>(totalClicks, HttpStatus.FOUND);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
