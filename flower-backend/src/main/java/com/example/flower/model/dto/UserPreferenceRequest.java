package com.example.flower.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceRequest {

    @NotEmpty(message = "En az 1 çiçek türü seçmelisiniz")
    @Size(max = 3, message = "En fazla 3 çiçek türü seçebilirsiniz")
    private List<String> flowerTypes;

    @NotEmpty(message = "En az 1 renk seçmelisiniz")
    @Size(max = 3, message = "En fazla 3 renk seçebilirsiniz")
    private List<String> colors;

    @NotNull(message = "Boyut seçmelisiniz")
    private String size;

    @NotNull(message = "Konsept seçmelisiniz")
    private String concept;

    @NotNull(message = "Buket stili seçmelisiniz")
    private String bouquetStyle;

    private String cardMessage;
}
