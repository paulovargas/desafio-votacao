package com.sicredi.votacao.repository;

import com.sicredi.votacao.entity.Pauta;
import com.sicredi.votacao.entity.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessaoRepository extends JpaRepository<Sessao, Long> {

    boolean existsByPauta(Pauta pauta);

    Optional<Sessao> findByPauta(Pauta pauta);
}
