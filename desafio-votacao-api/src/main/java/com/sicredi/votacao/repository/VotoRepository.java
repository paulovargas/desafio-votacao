package com.sicredi.votacao.repository;

import com.sicredi.votacao.entity.Pauta;
import com.sicredi.votacao.entity.Voto;
import com.sicredi.votacao.enums.OpcaoVoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotoRepository extends JpaRepository<Voto, Long> {

    boolean existsByPautaAndAssociadoId(Pauta pauta, Long associadoId);

    long countByPautaAndVoto(Pauta pauta, OpcaoVoto voto);
}