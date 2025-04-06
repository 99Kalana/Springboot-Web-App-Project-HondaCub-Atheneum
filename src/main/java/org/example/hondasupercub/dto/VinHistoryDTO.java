package org.example.hondasupercub.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class VinHistoryDTO {

    private int vinId;

    private String vinNumber;

    private int modelId;  // Only store the modelId, not the full ModelArchive entity

    private String compatibilityNotes;


}
