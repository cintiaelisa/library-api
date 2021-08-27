package br.com.cee.libraryapi.api.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReturnedLoanDTO {
    private Boolean returned;
}
