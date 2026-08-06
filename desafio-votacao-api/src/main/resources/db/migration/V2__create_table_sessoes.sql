CREATE TABLE sessoes (
    id BIGSERIAL PRIMARY KEY,
    pauta_id BIGINT NOT NULL UNIQUE,
    inicio TIMESTAMP NOT NULL,
    fim TIMESTAMP NOT NULL,

    CONSTRAINT fk_sessao_pauta
        FOREIGN KEY (pauta_id)
        REFERENCES pautas(id)
);