package me.songha.rs.machine;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class MachineConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(Machine.class, MachineDto.class).addMappings(mapper -> {
            mapper.map(Machine::getId, MachineDto::setId);
            mapper.map(Machine::getMachineName, MachineDto::setMachineName);
            mapper.map(Machine::getMachineType, MachineDto::setMachineType);
            mapper.map(Machine::getManufacturer, MachineDto::setManufacturer);
            mapper.map(Machine::getCreateAt, MachineDto::setCreateAt);
            mapper.map(Machine::getUpdateAt, MachineDto::setUpdateAt);
            mapper.skip(MachineDto::setBusinessLocationDto);
        });
        return modelMapper;
    }

    @Bean
    public ExecutorService executorService() {
        int coreCount = Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool(coreCount * 2);
    }

}