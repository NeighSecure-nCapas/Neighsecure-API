package com.example.neighsecureapi.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralResponse {
    String message;
    Object data;

    private GeneralResponse(Builder builder) {
        this.message = builder.message;
        this.data = builder.data;
    }

    public static class Builder {
        private String message;
        private Object data;

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public GeneralResponse build() {
            return new GeneralResponse(this);
        }
    }

    public static ResponseEntity<GeneralResponse> getResponse(HttpStatus status, String message, Object data){
        return new ResponseEntity<>(
                new Builder().message(message).data(data).build(),
                status
        );
    }

    public static ResponseEntity<GeneralResponse> getResponse(HttpStatus status, String message){
        return new ResponseEntity<>(
                new Builder().message(message).build(),
                status
        );
    }

    public static ResponseEntity<GeneralResponse> getResponse(HttpStatus status, Object data){
        return new ResponseEntity<>(
                new Builder().message(status.getReasonPhrase()).data(data).build(),
                status
        );
    }

    public static ResponseEntity<GeneralResponse> getResponse(String message, Object data){
        return new ResponseEntity<>(
                new Builder().message(message).data(data).build(),
                HttpStatus.OK
        );
    }

    public static ResponseEntity<GeneralResponse> getResponse(String message){
        return new ResponseEntity<>(
                new Builder().message(message).build(),
                HttpStatus.OK
        );
    }

    public static ResponseEntity<GeneralResponse> getResponse(HttpStatus status){
        return new ResponseEntity<>(
                new Builder().message(status.getReasonPhrase()).build(),
                status
        );
    }

    // agregar nuevos metodos de ser necesario

}
