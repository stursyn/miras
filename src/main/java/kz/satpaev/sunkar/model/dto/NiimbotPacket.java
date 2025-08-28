package kz.satpaev.sunkar.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NiimbotPacket {
  private byte command;
  private byte size;
  private byte[] data;
}
