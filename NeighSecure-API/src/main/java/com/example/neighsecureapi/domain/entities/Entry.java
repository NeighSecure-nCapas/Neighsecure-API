package com.example.neighsecureapi.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Entrada")
public class Entry {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "entradaId")
    private String id;

    @Column(name = "fechaYHora")
    private Date entryDate;

    // TODO: agregar llaves foraneas respectivas
    @Column(name = "terminalId")
    private String terminalId;

    @Column(name = "permisoId")
    private String permissionId;

    @Column(name = "comentario")
    private String comment;
}
