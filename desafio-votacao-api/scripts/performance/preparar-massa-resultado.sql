BEGIN;

WITH pauta_criada AS (
    INSERT INTO pautas (titulo, descricao, data_criacao)
    VALUES (
        'Pauta de performance',
        'Massa local para medir o endpoint de resultado da votacao.',
        NOW()
    )
    RETURNING id
),
sessao_criada AS (
    INSERT INTO sessoes (pauta_id, inicio, fim)
    SELECT id, NOW() - INTERVAL '10 minutes', NOW() - INTERVAL '5 minutes'
    FROM pauta_criada
),
votos_criados AS (
    INSERT INTO votos (pauta_id, associado_id, voto, data_hora)
    SELECT
        pauta_criada.id,
        serie.associado_id,
        CASE WHEN serie.associado_id % 2 = 0 THEN 'SIM' ELSE 'NAO' END,
        NOW() - INTERVAL '5 minutes'
    FROM pauta_criada
    CROSS JOIN generate_series(1, 100000) AS serie(associado_id)
    RETURNING pauta_id, voto
)
SELECT
    pauta_id,
    COUNT(*) AS total_votos,
    COUNT(*) FILTER (WHERE voto = 'SIM') AS total_sim,
    COUNT(*) FILTER (WHERE voto = 'NAO') AS total_nao
FROM votos_criados
GROUP BY pauta_id;

COMMIT;
