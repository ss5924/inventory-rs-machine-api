package me.songha.rs.machine;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Getter
@ToString
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "machine")
public class Machine implements Serializable {
    @Serial
    private static final long serialVersionUID = 8618306483779969709L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long businessLocationId;
    private String machineName;
    private String machineType;
    private String manufacturer;

    @CreatedDate
    private LocalDateTime createAt;
    @LastModifiedDate
    private LocalDateTime updateAt;

    public void setBusinessLocationId(Long businessLocationId) {
        this.businessLocationId = businessLocationId;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public void setMachineType(String machineType) {
        this.machineType = machineType;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    @Builder
    public Machine(Long id, Long businessLocationId, String machineName, String machineType, String manufacturer) {
        this.id = id;
        this.businessLocationId = businessLocationId;
        this.machineName = machineName;
        this.machineType = machineType;
        this.manufacturer = manufacturer;
    }

    public MachineDto toMachineDto() {
        return MachineDto.builder()
                .id(this.id)
                .machineName(this.machineName)
                .machineType(this.machineType)
                .manufacturer(this.manufacturer)
                .createAt(this.createAt)
                .updateAt(this.updateAt)
                .build();
    }

}
