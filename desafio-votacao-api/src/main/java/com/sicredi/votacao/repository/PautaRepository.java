package com.sicredi.votacao.repository;

import com.sicredi.votacao.entity.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PautaRepository extends JpaRepository<Pauta, Long> {

    List<Pauta> findAllByOrderByDataCriacaoDesc();
}
