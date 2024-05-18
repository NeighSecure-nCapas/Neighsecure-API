package com.example.neighsecureapi.utils;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArrayManagementTools {
    // utilidad para menjar arreglos y listas

    // funcion para convertir una lista de strings a un solo string separado por comas
    public String convertListToString(List<String> list) {
        return String.join(",", list);
    }

    // funcion para convertir un string separado por comas a una lista de strings
    public List<String> convertStringToList(String string) {
        return List.of(string.split(","));
    }

}
