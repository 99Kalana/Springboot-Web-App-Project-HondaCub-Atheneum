package org.example.hondasupercub.dto;

import lombok.*;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Data
@Component
public class ResponseDTO {

    private int status;

    private String message;

    private Object data;
}
