package com.example.neighsecureapi.domain.dtos.userDTOs;

import lombok.Data;

@Data
public class GoogleLoginDTO {

//    email: "fernan.fo17@gmail.com"
//    email_verified : true
//    family_name:  "Figueroa"
//    given_name: "Fernando"
//    locale: "es"
//    name:"Fernando Figueroa"
//    picture : "https://lh3.googleusercontent.com/a/ACg8ocK61r7WGSfM_Pmi-OsmhOl0sFNTvip4yUtmZ4fS-9IAw1CIg5Rc=s96-c"
//    sub: "113931811518408662644"

    private String sub;
    private String email;
    private boolean verified_email;
    private String name;
    private String given_name;
    private String family_name;
    private String picture;
    private String locale;
}
