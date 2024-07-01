package me.songha.rs.machine;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MachineRepository extends JpaRepository<Machine, Long> {
    List<Machine> findByManufacturer(String manufacturer);
}
