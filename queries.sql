-- ============================================
-- QUERIES ÚTEIS - Currency Exchange API
-- ============================================

-- Criar banco de dados
CREATE DATABASE IF NOT EXISTS currency_exchange_db 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE currency_exchange_db;

-- ============================================
-- CONSULTAS DE ANÁLISE
-- ============================================

-- Total de conversões por usuário
SELECT 
    u.full_name,
    u.email,
    COUNT(c.id) as total_conversions,
    SUM(c.source_amount) as total_converted_brl,
    SUM(c.target_amount) as total_received_foreign
FROM users u
LEFT JOIN conversions c ON u.id = c.user_id
GROUP BY u.id
ORDER BY total_conversions DESC;

-- Conversões dos últimos 7 dias
SELECT 
    DATE(c.created_at) as date,
    COUNT(*) as total_conversions,
    SUM(c.source_amount) as total_brl,
    AVG(c.exchange_rate) as avg_rate
FROM conversions c
WHERE c.created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
GROUP BY DATE(c.created_at)
ORDER BY date DESC;

-- Moedas mais convertidas
SELECT 
    cur.code,
    cur.name,
    COUNT(c.id) as conversion_count,
    SUM(c.target_amount) as total_amount
FROM currencies cur
JOIN conversions c ON cur.id = c.target_currency_id
GROUP BY cur.id
ORDER BY conversion_count DESC;

-- Histórico de taxas de câmbio (últimas 30 conversões de USD)
SELECT 
    c.exchange_rate,
    c.conversion_date,
    c.source_amount,
    c.target_amount,
    u.email as user_email
FROM conversions c
JOIN currencies cur ON c.target_currency_id = cur.id
JOIN users u ON c.user_id = u.id
WHERE cur.code = 'USD'
ORDER BY c.conversion_date DESC
LIMIT 30;

-- Logs de operações com erro
SELECT 
    l.endpoint,
    l.http_method,
    l.status_code,
    l.error_message,
    l.timestamp,
    u.email as user_email
FROM log_operations l
LEFT JOIN users u ON l.user_id = u.id
WHERE l.status = 'ERROR'
ORDER BY l.timestamp DESC
LIMIT 50;

-- Estatísticas de impostos coletados
SELECT 
    DATE(c.created_at) as date,
    COUNT(*) as conversions,
    SUM(c.tax_amount) as total_tax_collected,
    AVG(c.tax_rate) * 100 as avg_tax_percentage
FROM conversions c
GROUP BY DATE(c.created_at)
ORDER BY date DESC;

-- Performance dos endpoints (tempo médio de execução)
SELECT 
    l.endpoint,
    l.http_method,
    COUNT(*) as request_count,
    AVG(l.execution_time_ms) as avg_time_ms,
    MAX(l.execution_time_ms) as max_time_ms,
    MIN(l.execution_time_ms) as min_time_ms
FROM log_operations l
WHERE l.timestamp >= DATE_SUB(NOW(), INTERVAL 1 DAY)
GROUP BY l.endpoint, l.http_method
ORDER BY avg_time_ms DESC;

-- Usuários mais ativos (por conversões)
SELECT 
    u.full_name,
    u.email,
    u.role,
    COUNT(c.id) as total_conversions,
    MAX(c.created_at) as last_conversion
FROM users u
LEFT JOIN conversions c ON u.id = c.user_id
WHERE u.active = true
GROUP BY u.id
HAVING total_conversions > 0
ORDER BY total_conversions DESC
LIMIT 10;

-- ============================================
-- QUERIES DE MANUTENÇÃO
-- ============================================

-- Limpar logs antigos (mais de 90 dias)
DELETE FROM log_operations 
WHERE timestamp < DATE_SUB(NOW(), INTERVAL 90 DAY);

-- Desativar moedas não utilizadas
UPDATE currencies 
SET active = false 
WHERE id NOT IN (
    SELECT DISTINCT target_currency_id FROM conversions
);

-- Encontrar usuários inativos (sem conversões há mais de 6 meses)
SELECT 
    u.id,
    u.email,
    u.full_name,
    MAX(c.created_at) as last_activity
FROM users u
LEFT JOIN conversions c ON u.id = c.user_id
GROUP BY u.id
HAVING last_activity < DATE_SUB(NOW(), INTERVAL 6 MONTH)
    OR last_activity IS NULL;

-- ============================================
-- ÍNDICES RECOMENDADOS (se não criados automaticamente)
-- ============================================

CREATE INDEX idx_conversions_user_id ON conversions(user_id);
CREATE INDEX idx_conversions_created_at ON conversions(created_at);
CREATE INDEX idx_conversions_target_currency ON conversions(target_currency_id);

CREATE INDEX idx_log_operations_user_id ON log_operations(user_id);
CREATE INDEX idx_log_operations_timestamp ON log_operations(timestamp);
CREATE INDEX idx_log_operations_status ON log_operations(status);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_currencies_code ON currencies(code);

-- ============================================
-- BACKUP E RESTORE
-- ============================================

-- Fazer backup
-- mysqldump -u root -p currency_exchange_db > backup.sql

-- Restaurar backup
-- mysql -u root -p currency_exchange_db < backup.sql