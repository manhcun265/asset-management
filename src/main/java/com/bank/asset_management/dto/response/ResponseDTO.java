package com.bank.asset_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO<T> {
    private int statusCode;
    private String error;
    private Object message; // Object để có thể là String hoặc List<String>
    private T data;
}

