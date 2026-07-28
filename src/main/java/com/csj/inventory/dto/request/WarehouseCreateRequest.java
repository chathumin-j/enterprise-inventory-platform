package com.csj.inventory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseCreateRequest {

    @NotBlank(message = "code is required")
    @Size(max = 30)
    private String code;

    @NotBlank(message = "name is required")
    @Size(max = 150)
    private String name;

    @Size(max = 255)
    private String location;
}
