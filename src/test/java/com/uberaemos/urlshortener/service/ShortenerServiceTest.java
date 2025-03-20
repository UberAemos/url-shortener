package com.uberaemos.urlshortener.service;

import com.uberaemos.urlshortener.model.Url;
import com.uberaemos.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShortenerServiceTest {

    @Mock
    private IdGeneratorService idGeneratorService;

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private ShortenerService shortenerService;

    private final String baseUrl = "http://short.ly/";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(shortenerService, "baseUrl", baseUrl);
    }

    @Test
    void testShortenUrl_ExistingUrl() {
        String longUrl = "https://example.com";
        String shortUrl = baseUrl + "abc123";
        Url existingUrl = new Url(1L, shortUrl, longUrl);

        when(urlRepository.findByLongUrl(longUrl)).thenReturn(Optional.of(existingUrl));

        String result = shortenerService.shortenUrl(longUrl);

        assertEquals(shortUrl, result);
        verify(urlRepository, never()).save(any());
        verify(idGeneratorService, never()).generateId();
    }

    @Test
    void testShortenUrl_NewUrl() {
        String longUrl = "https://example.com";
        long id = 12345L;
        String encoded = "3d7";
        String shortUrl = baseUrl + encoded;

        when(urlRepository.findByLongUrl(longUrl)).thenReturn(Optional.empty());
        when(idGeneratorService.generateId()).thenReturn(id);

        String result = shortenerService.shortenUrl(longUrl);

        assertEquals(shortUrl, result);
        verify(urlRepository).save(any(Url.class));
        verify(idGeneratorService).generateId();
    }

    @Test
    void testRedirectUrl_Found() {
        String shortUrl = baseUrl + "abc123";
        String longUrl = "https://example.com";
        Url url = new Url(1L, shortUrl, longUrl);

        when(urlRepository.findByShortUrl(shortUrl)).thenReturn(Optional.of(url));

        String result = shortenerService.redirectUrl(shortUrl);

        assertEquals(longUrl, result);
    }

    @Test
    void testRedirectUrl_NotFound() {
        String shortUrl = baseUrl + "notfound";

        when(urlRepository.findByShortUrl(shortUrl)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> shortenerService.redirectUrl(shortUrl));
        assertEquals("Url: " + shortUrl + " not found", exception.getMessage());
    }

}