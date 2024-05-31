package com.example.neighsecureapi.domain.dtos.userDTOs;

import lombok.Data;

@Data
public class GoogleLoginDTO {
    /*
   "id": "12345678901234567890",
  "email": "user@example.com",
  "verified_email": true,
  "name": "John Doe",
  "given_name": "John",
  "family_name": "Doe",
  "link": "https://plus.google.com/12345678901234567890",
  "picture": "https://lh3.googleusercontent.com/photo.jpg",
  "locale": "en"

    * */
    private String id;
    private String email;
    private boolean verified_email;
    private String name;
    private String given_name;
    private String family_name;
    private String link;
    private String picture;
    private String locale;
}
