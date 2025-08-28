package kz.satpaev.sunkar.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageRow {
    private Type type;
    private int rowNumber;
    private int repeatCount;
    private byte[] columnData;
    private int blackPixelCount;

    public enum Type   {
        VOID, PIXEL
    }
}
