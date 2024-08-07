package com.example.neighsecureapi.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Terminal")
public class Terminal {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "terminalId")
    private UUID terminalId;

    @Column(name = "tipoEntrada")
    private String entryType; // podria ser un enum (? peatonal o vehicular

    @OneToMany(mappedBy = "terminalId", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Entry> entries;
}
