package me.songha.rs.machine;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor // !Important!
@Data
public class MachineDto {
    private Long id;
    private BusinessLocationDto businessLocationDto;
    private String machineName;
    private String machineType;
    private String manufacturer;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createAt;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateAt;

    public Machine toEntity() {
        return Machine.builder()
                .id(this.id)
                .businessLocationId(this.businessLocationDto.getId())
                .machineName(this.machineName)
                .machineType(this.machineType)
                .manufacturer(this.manufacturer)
                .build();
    }

    @Builder
    public MachineDto(Long id, BusinessLocationDto businessLocationDto, String machineName, String machineType, String manufacturer, LocalDateTime createAt, LocalDateTime updateAt) {
        this.id = id;
        this.businessLocationDto = businessLocationDto;
        this.machineName = machineName;
        this.machineType = machineType;
        this.manufacturer = manufacturer;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public MachineDto(Machine machine, BusinessLocationDto businessLocationDto) {
        this.id = machine.getId();
        this.businessLocationDto = businessLocationDto;
        this.machineName = machine.getMachineName();
        this.machineType = machine.getMachineType();
        this.manufacturer = machine.getManufacturer();
        this.createAt = machine.getCreateAt();
        this.updateAt = machine.getUpdateAt();
    }
}
