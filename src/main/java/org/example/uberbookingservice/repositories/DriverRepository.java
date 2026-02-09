package org.example.uberbookingservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.example.uberproject.models.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
}
