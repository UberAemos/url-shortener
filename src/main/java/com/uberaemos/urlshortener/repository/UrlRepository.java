package com.uberaemos.urlshortener.repository;

import com.uberaemos.urlshortener.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByLongUrl(String longUrl);

    Optional<Url> findByShortUrl(String shortUrl);
}
