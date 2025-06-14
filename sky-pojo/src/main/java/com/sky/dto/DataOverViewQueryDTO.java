package com.sky.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataOverViewQueryDTO implements Serializable {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime begin;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime end;

}
