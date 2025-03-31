package com.uberaemos.urlshortener.api;

import com.uberaemos.urlshortener.service.ShortenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/url")
@RequiredArgsConstructor
public class UrlShortenerController {

    private final ShortenerService shortenerService;

    @PostMapping("/shorten")
    public String shortenUrl(@RequestParam String longUrl) {
        return shortenerService.shortenUrl(longUrl);
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Object> redirectUrl(@PathVariable String shortUrl) {
        return shortenerService.getLongUrl(shortUrl)
                .map(url -> ResponseEntity.status(302).location(URI.create(url.getLongUrl())).build())
                .orElse(ResponseEntity.status(404).build());
    }


}
