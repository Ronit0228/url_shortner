package com.url_shortner.repository;

import com.url_shortner.models.ClickEvent;
import com.url_shortner.models.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {
    List<ClickEvent> findByUrlMappingAndClickAtBetween(UrlMapping urlMapping, LocalDateTime startDate, LocalDateTime endDate);
    List<ClickEvent> findByUrlMappingInAndClickAtBetween(List<UrlMapping> urlMappings, LocalDateTime startDate, LocalDateTime endDate);
}
