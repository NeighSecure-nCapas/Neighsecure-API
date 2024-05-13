package com.example.neighsecureapi.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

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
    private UUID id;

    @Column(name = "fechaYHora")
    private Date entryDate;

    // TODO: validar si estan bien las relaciones
    @JoinColumn(name = "terminalId")
    @ManyToOne
    private Terminal terminalId;

    @JoinColumn(name = "permisoId")
    @ManyToOne
    private Permission permissionId;

    @Column(name = "comentario", nullable = true)
    private String comment;
}
