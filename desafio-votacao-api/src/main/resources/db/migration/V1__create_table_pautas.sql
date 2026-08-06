CREATE TABLE pautas (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descricao VARCHAR(1000),
    data_criacao TIMESTAMP NOT NULL
);