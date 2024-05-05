package com.example.neighsecureapi.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entry {
    private String id;
    private Date entryDate;
    private String terminalId;
    private String permissionId;
    private String comment;
}
