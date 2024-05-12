package com.example.neighsecureapi.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Llave")
public class Key {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "llaveId")
    private String keyId;

    // TODO: validar formatos de fecha y horas
    @Column(name = "fechaGenerada")
    private Date generationDate;

    @Column(name = "horaGenerada")
    private Date generationTime;

    @Column(name = "diaGenerado")
    private String generationDay;
}
