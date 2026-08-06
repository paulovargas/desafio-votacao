package com.sicredi.votacao.entity;

import com.sicredi.votacao.enums.OpcaoVoto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "votos", uniqueConstraints = @UniqueConstraint(columnNames = {"pauta_id", "associado_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pauta_id")
    private Pauta pauta;

    @Column(name = "associado_id", nullable = false)
    private Long associadoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OpcaoVoto voto;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @PrePersist
    public void prePersist(){
        this.dataHora = LocalDateTime.now();
    }
}
