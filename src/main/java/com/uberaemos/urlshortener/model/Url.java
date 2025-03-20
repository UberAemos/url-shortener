package com.uberaemos.urlshortener.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "url")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Url {

    @Id
    private long id;

    @Column(name = "short-url", unique = true)
    private String shortUrl;

    @Column(name = "long-url", unique = true)
    private String longUrl;
}
