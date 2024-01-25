package com.example.demoweb.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class ImageDTO {
    private String filename;
    private byte[] data;  // Dữ liệu ảnh dưới dạng mảng byte
}
