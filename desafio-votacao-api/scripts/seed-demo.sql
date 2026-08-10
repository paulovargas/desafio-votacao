BEGIN;

WITH novas_pautas AS (
    INSERT INTO pautas (titulo, descricao, data_criacao)
    VALUES
        (
            'Aprovar novo regulamento interno',
            'Pauta aberta para testar registro de votos pelo app.',
            NOW() - INTERVAL '10 minutes'
        ),
        (
            'Contratar plataforma de atendimento',
            'Pauta encerrada com maioria de votos SIM.',
            NOW() - INTERVAL '2 hours'
        ),
        (
            'Alterar horario das assembleias',
            'Pauta encerrada com maioria de votos NAO.',
            NOW() - INTERVAL '2 hours'
        ),
        (
            'Revisar politica de beneficios',
            'Pauta encerrada com empate.',
            NOW() - INTERVAL '2 hours'
        ),
        (
            'Pauta sem sessao aberta',
            'Use esta pauta para testar a abertura de sessao.',
            NOW() - INTERVAL '5 minutes'
        )
    RETURNING id, titulo
),
sessao_aberta AS (
    INSERT INTO sessoes (pauta_id, inicio, fim)
    SELECT id, NOW() - INTERVAL '2 minutes', NOW() + INTERVAL '30 minutes'
    FROM novas_pautas
    WHERE titulo = 'Aprovar novo regulamento interno'
),
sessoes_encerradas AS (
    INSERT INTO sessoes (pauta_id, inicio, fim)
    SELECT id, NOW() - INTERVAL '90 minutes', NOW() - INTERVAL '30 minutes'
    FROM novas_pautas
    WHERE titulo IN (
        'Contratar plataforma de atendimento',
        'Alterar horario das assembleias',
        'Revisar politica de beneficios'
    )
),
votos_sim_aberta AS (
    INSERT INTO votos (pauta_id, associado_id, voto, data_hora)
    SELECT id, associado_id, 'SIM', NOW() - INTERVAL '1 minute'
    FROM novas_pautas
    CROSS JOIN generate_series(1001, 1003) associado_id
    WHERE titulo = 'Aprovar novo regulamento interno'
),
votos_aprovada AS (
    INSERT INTO votos (pauta_id, associado_id, voto, data_hora)
    SELECT id, associado_id, CASE WHEN associado_id <= 2004 THEN 'SIM' ELSE 'NAO' END, NOW() - INTERVAL '45 minutes'
    FROM novas_pautas
    CROSS JOIN generate_series(2001, 2005) associado_id
    WHERE titulo = 'Contratar plataforma de atendimento'
),
votos_reprovada AS (
    INSERT INTO votos (pauta_id, associado_id, voto, data_hora)
    SELECT id, associado_id, CASE WHEN associado_id <= 3002 THEN 'SIM' ELSE 'NAO' END, NOW() - INTERVAL '45 minutes'
    FROM novas_pautas
    CROSS JOIN generate_series(3001, 3005) associado_id
    WHERE titulo = 'Alterar horario das assembleias'
),
votos_empate AS (
    INSERT INTO votos (pauta_id, associado_id, voto, data_hora)
    SELECT id, associado_id, CASE WHEN associado_id % 2 = 0 THEN 'SIM' ELSE 'NAO' END, NOW() - INTERVAL '45 minutes'
    FROM novas_pautas
    CROSS JOIN generate_series(4001, 4004) associado_id
    WHERE titulo = 'Revisar politica de beneficios'
)
SELECT
    p.id AS pauta_id,
    p.titulo,
    CASE
        WHEN s.id IS NULL THEN 'SEM_SESSAO'
        WHEN s.fim > NOW() THEN 'SESSAO_ABERTA'
        ELSE 'SESSAO_ENCERRADA'
    END AS status_sessao,
    COUNT(v.id) FILTER (WHERE v.voto = 'SIM') AS votos_sim,
    COUNT(v.id) FILTER (WHERE v.voto = 'NAO') AS votos_nao,
    COUNT(v.id) AS total_votos
FROM novas_pautas np
JOIN pautas p ON p.id = np.id
LEFT JOIN sessoes s ON s.pauta_id = p.id
LEFT JOIN votos v ON v.pauta_id = p.id
GROUP BY p.id, p.titulo, s.id, s.fim
ORDER BY p.id;

COMMIT;
