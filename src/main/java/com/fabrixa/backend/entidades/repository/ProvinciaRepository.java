package com.fabrixa.backend.entidades.repository;

import com.fabrixa.backend.entidades.model.Provincia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProvinciaRepository extends JpaRepository<Provincia, String> {}
