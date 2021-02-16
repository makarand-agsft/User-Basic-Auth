package com.user.auth.model;

import javax.persistence.*;

@Entity
@Table(name = "api_key")
public class APIKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "apikey")
    private String apiKey;

}
