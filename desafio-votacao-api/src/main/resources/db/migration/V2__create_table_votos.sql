CREATE TABLE votos (
    id BIGSERIAL PRIMARY KEY,

    pauta_id BIGINT NOT NULL,

    associado_id BIGINT NOT NULL,

    voto VARCHAR(10) NOT NULL,

    data_hora TIMESTAMP NOT NULL,

    CONSTRAINT fk_voto_pauta
        FOREIGN KEY (pauta_id)
        REFERENCES pautas(id),

    CONSTRAINT uk_pauta_associado
        UNIQUE (pauta_id, associado_id)
);