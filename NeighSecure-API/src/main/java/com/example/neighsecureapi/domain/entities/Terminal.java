package com.example.neighsecureapi.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Terminal")
public class Terminal {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "terminalId")
    private String terminalId;

    @Column(name = "tipoEntrada")
    private String entryType; // podria ser un enum (? peatonal o vehicular

    @OneToMany(mappedBy = "terminalId", fetch = FetchType.LAZY)
    private List<Entry> entries;
}
