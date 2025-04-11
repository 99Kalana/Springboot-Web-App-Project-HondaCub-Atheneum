package org.example.hondasupercub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class VinHistoryDTO {

    private int vinId;

    @NotBlank(message = "VIN number cannot be blank")
    @Size(min = 3, max = 10, message = "VIN number must be between 3 and 10 characters for C50-C125")
    @Pattern(regexp = "^(C(50|65|70|90|100|125))[A-HJ-NPR-Z0-9]{0,7}$",
            message = "VIN number must start with C50, C65, C70, C90, C100, or C125")
    private String vinNumber;

    private int modelId;  // Only store the modelId, not the full ModelArchive entity

    @Size(max = 500, message = "Compatibility notes cannot exceed 500 characters")
    private String compatibilityNotes;


}
