package me.songha.rs.machine;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
@Service
public class MachineService {
    private final MachineRepository machineRepository;
    private final RestTemplate restTemplate;
    private final ModelMapper modelMapper;
    private final Environment environment;
    private final ExecutorService executorService;

    @Value("${rs.service.url.business-location}")
    private String baseUrl;

    public MachineDto getMachineById(Long id) {
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found id " + id));

        BusinessLocationDto businessLocationDto = getBusinessLocationById(machine.getBusinessLocationId());
        MachineDto machineDto = machine.toMachineDto();
        machineDto.setBusinessLocationDto(businessLocationDto);
        return machineDto;
    }

    //    @Cacheable(value = "getMachinesByManufacturer", key = "#manufacturer", cacheManager = "cacheManager")
    public List<MachineDto> getMachinesByManufacturer(String manufacturer) {
        List<Machine> machines = machineRepository.findByManufacturer(manufacturer);
        List<CompletableFuture<MachineDto>> futures = machines.stream()
                .map(machine -> CompletableFuture.supplyAsync(() -> {
                    BusinessLocationDto location = getBusinessLocationById(machine.getBusinessLocationId());
                    return new MachineDto(machine, location);
                }, executorService))
                .toList();
        return futures.stream().map(CompletableFuture::join).toList();
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
        if (environment.matchesProfiles("local")) {
            return restTemplate.getForObject("http://192.168.0.37:30003/business-location/id/" + businessLocationId, BusinessLocationDto.class);
        }
        try {
            return restTemplate.getForObject(baseUrl + "/business-location/id/" + businessLocationId, BusinessLocationDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Not found BusinessLocationId " + businessLocationId);
        }
    }

}
