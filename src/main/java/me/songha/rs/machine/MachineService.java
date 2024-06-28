package me.songha.rs.machine;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
public class MachineService {
    private final MachineRepository machineRepository;
    private final RestTemplate restTemplate;

    @Value("${api.base-url}")
    private String baseUrl;

    @Cacheable(value = "Machine", key = "#id", cacheManager = "cacheManager")
    public MachineDto getMachine(Long id) {
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found id " + id));

        BusinessLocationDto businessLocationDto = getBusinessLocationById(machine.getBusinessLocationId());
        MachineDto machineDto = machine.toMachineDto();
        machineDto.setBusinessLocationDto(businessLocationDto);
        return machineDto;
    }

    @Transactional
    public MachineDto save(MachineDto machineDto) {
        Long businessLocationId = machineDto.getBusinessLocationDto().getId();
        BusinessLocationDto businessLocationDto = getBusinessLocationById(businessLocationId); // Validate BusinessLocationId

        MachineDto createdMachine = machineRepository.save(machineDto.toEntity()).toMachineDto();
        createdMachine.setBusinessLocationDto(businessLocationDto);
        return createdMachine;
    }

    @Transactional
    public MachineDto update(Long id, MachineDto machineDto) {
        return machineRepository.findById(id).map(machine -> {
            machine.setMachineName(machineDto.getMachineName());
            machine.setMachineType(machineDto.getMachineType());
            machine.setManufacturer(machineDto.getManufacturer());
            machine.setBusinessLocationId(machineDto.getBusinessLocationDto().getId());
            return machineRepository.save(machine).toMachineDto();
        }).orElseThrow(() -> new ResourceNotFoundException("Not found id " + id));
    }

    @Transactional
    public void deleteById(Long id) {
        if (machineRepository.existsById(id)) {
            machineRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Not found id " + id);
        }
    }

    private BusinessLocationDto getBusinessLocationById(Long businessLocationId) {
        try {
            return restTemplate.getForObject(baseUrl + "/id/" + businessLocationId, BusinessLocationDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Not found BusinessLocationId " + businessLocationId);
        }
    }

}
