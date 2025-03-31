package com.uberaemos.urlshortener.service;

import com.uberaemos.urlshortener.model.Url;
import com.uberaemos.urlshortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ShortenerService {

    @Value("${url-shortener.base-url}")
    private String baseUrl;

    private final IdGeneratorService idGeneratorService;
    private final UrlRepository urlRepository;

    public String shortenUrl(String url) {
        Optional<Url> byLongUrl = urlRepository.findByLongUrl(url);
        if (byLongUrl.isPresent()) {
            return byLongUrl.get().getShortUrl();
        }

        long id = idGeneratorService.generateId();
        String encoded = Base62Encoder.encode(id);
        String shortUrl = baseUrl.concat(encoded);

        Url entity = new Url(id, shortUrl, url);
        urlRepository.save(entity);
        return shortUrl;
    }

    public Optional<Url> getLongUrl(String url) {
        return urlRepository.findByShortUrl(url);
    }
}
