package me.songha.rs.machine;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/machine")
public class MachineController {
    private final MachineService machineService;

    @GetMapping(value = "/id/{id}")
    public ResponseEntity<MachineDto> getMachineDto(@PathVariable Long id) {
        try {
            MachineDto machineDto = machineService.getMachine(id);
            return ResponseEntity.ok(machineDto);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<MachineDto> saveBusinessLocation(@RequestBody MachineDto machineDto) {
        MachineDto createdMachine = machineService.save(machineDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMachine);
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<MachineDto> updateBusinessLocation(@PathVariable Long id, @RequestBody MachineDto newMachineDto) {
        MachineDto updatedMachine = machineService.update(id, newMachineDto);
        return ResponseEntity.ok(updatedMachine);
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deleteBusinessLocation(@PathVariable Long id) {
        machineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
