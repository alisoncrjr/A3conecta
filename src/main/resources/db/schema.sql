-- ==========================================================
-- 🏗️ BANCO DE DADOS CONECTA - SETUP COMPLETO
-- ==========================================================

-- 1. Criação do Banco de Dados
-- NOTE: This script assumes you have a MySQL database created and the
-- JDBC URL points to that database. We intentionally DO NOT drop or
-- recreate the database here to avoid data loss on repeated runs.
-- The script will create the necessary tables if they don't exist.
SET FOREIGN_KEY_CHECKS = 0;

-- ==========================================================
-- 2. Tabela de Usuários (Single Table Inheritance)
-- Combina Cliente e Prestador, diferenciados pelo campo 'role'
-- ==========================================================
CREATE TABLE users (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('CLIENTE', 'PRESTADOR', 'ADMIN') NOT NULL,
    
    -- Campos Comuns
    endereco VARCHAR(255),
    telefone VARCHAR(20),
    foto_perfil_path VARCHAR(255) DEFAULT 'default_profile.png',
    
    -- Campos Específicos de Cliente
    cpf VARCHAR(14),
    
    -- Campos Específicos de Prestador
    cnpj VARCHAR(18),
    descricao_servico TEXT,
    categoria VARCHAR(50) DEFAULT 'Outro',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Índices para performance de Login e Busca
    INDEX idx_user_email (email),
    INDEX idx_user_role_cat (role, categoria)
) ENGINE=InnoDB;
-- ==========================================================
-- Seed data removed: database now contains only safe DDL
-- Populate test/example data using separate migration or application
-- code. This keeps the schema idempotent and safe for production use.
-- ==========================================================
    status ENUM('PENDENTE', 'EM_ANDAMENTO', 'FINALIZADO', 'CANCELADO') DEFAULT 'PENDENTE',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (cliente_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (prestador_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (projeto_id) REFERENCES projetos(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ==========================================================
-- 6. Tabela de Avaliações
-- Baseado em Model.Avaliacao
-- ==========================================================
CREATE TABLE avaliacoes (
    id VARCHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID()),
    cliente_id VARCHAR(36) NOT NULL,
    prestador_id VARCHAR(36) NOT NULL,
    
    pontuacao INT NOT NULL CHECK (pontuacao BETWEEN 1 AND 5),
    comentario TEXT,
    data_avaliacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (cliente_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (prestador_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ==========================================================
-- 7. Tabela de Conversas (Chat)
-- Baseado em Model.Conversa
-- ==========================================================
CREATE TABLE conversas (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    user1_id VARCHAR(36) NOT NULL,
    user2_id VARCHAR(36) NOT NULL,
    ultima_atividade DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user1_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (user2_id) REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_conversas_users (user1_id, user2_id)
) ENGINE=InnoDB;

-- ==========================================================
-- 8. Tabela de Mensagens
-- Baseado em Model.Mensagem
-- ==========================================================
CREATE TABLE mensagens (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    conversa_id VARCHAR(36) NOT NULL,
    remetente_id VARCHAR(36) NOT NULL,
    destinatario_id VARCHAR(36) NOT NULL,
    
    conteudo TEXT NOT NULL,
    data_envio DATETIME DEFAULT CURRENT_TIMESTAMP,
    lida BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY (conversa_id) REFERENCES conversas(id) ON DELETE CASCADE,
    FOREIGN KEY (remetente_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (destinatario_id) REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_msg_conversa (conversa_id, data_envio)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================================
-- 🚀 SEED DATA (DADOS DE TESTE)
-- Baseado no App.java seedData()
-- ==========================================================

-- 1. Inserir Clientes
INSERT INTO users (id, name, email, password_hash, role, cpf, endereco, telefone, foto_perfil_path) VALUES
(UUID(), 'João Silva', 'joao@example.com', 'Senha123!', 'CLIENTE', '11122233344', 'Rua A, 10', '(11) 98765-4321', 'joao.png'),
(UUID(), 'Maria Oliveira', 'maria@example.com', 'Senha123!', 'CLIENTE', '55566677788', 'Av. B, 20', '(21) 91234-5678', 'maria.png'),
(UUID(), 'Carlos Alberto', 'carlos@example.com', 'Senha123!', 'CLIENTE', '99988877766', 'Rua do Sol, 123', '(11) 9876-1234', 'carlos.png');

-- 2. Inserir Prestadores
INSERT INTO users (id, name, email, password_hash, role, cnpj, descricao_servico, endereco, telefone, categoria, foto_perfil_path) VALUES
(UUID(), 'Ana Souza', 'ana@example.com', 'Senha123!', 'PRESTADOR', '11222333000144', 'Encanamento residencial e comercial', 'Rua C, 30', '(31) 99887-6543', 'Encanador', 'ana.png'),
(UUID(), 'Pedro Costa', 'pedro@example.com', 'Senha123!', 'PRESTADOR', '55666777000188', 'Instalações elétricas, reparos e projetos', 'Av. D, 40', '(41) 97766-5544', 'Eletricista', 'pedro.png'),
(UUID(), 'Carla Lima', 'carla@example.com', 'Senha123!', 'PRESTADOR', '99888777000166', 'Pintura de interiores e exteriores, grafiato', 'Rua E, 50', '(51) 96655-4433', 'Pintor', 'carla.png'),
(UUID(), 'Lucas Martins', 'lucas@example.com', 'Senha123!', 'PRESTADOR', '12345678000190', 'Desenvolvimento de Websites e Aplicativos', 'Online', '(11) 91122-3344', 'Programador', 'lucas.png'),
(UUID(), 'Julia Campos', 'julia@example.com', 'Senha123!', 'PRESTADOR', '98765432000110', 'Serviços de Jardinagem e paisagismo', 'Bairro F, 60', '(11) 95566-7788', 'Jardineiro', 'julia.png'),
(UUID(), 'Fernanda Alves', 'fer@example.com', 'Senha123!', 'PRESTADOR', '22333444000155', 'Limpeza residencial e comercial, pós-obra', 'Rua G, 70', '(11) 92233-4455', 'Diarista', 'fernanda.png');

-- 3. Inserir Avaliações
INSERT INTO avaliacoes (cliente_id, prestador_id, pontuacao, comentario, data_avaliacao) VALUES
((SELECT id FROM users WHERE email='joao@example.com'), (SELECT id FROM users WHERE email='ana@example.com'), 5, 'Ótimo serviço de encanamento! Resolveu o problema rapidamente.', NOW() - INTERVAL 10 DAY),
((SELECT id FROM users WHERE email='maria@example.com'), (SELECT id FROM users WHERE email='ana@example.com'), 4, 'Encanador muito competente. Rápido e eficiente.', NOW() - INTERVAL 5 DAY),
((SELECT id FROM users WHERE email='joao@example.com'), (SELECT id FROM users WHERE email='pedro@example.com'), 3, 'Instalação ok, mas demorou um pouco mais que o previsto.', NOW() - INTERVAL 7 DAY),
((SELECT id FROM users WHERE email='maria@example.com'), (SELECT id FROM users WHERE email='carla@example.com'), 5, 'Pintura impecável! Adorei o resultado final e a limpeza.', NOW() - INTERVAL 2 DAY),
((SELECT id FROM users WHERE email='carlos@example.com'), (SELECT id FROM users WHERE email='lucas@example.com'), 5, 'Fez um site incrível para minha empresa. Super recomendo!', NOW() - INTERVAL 1 DAY),
((SELECT id FROM users WHERE email='joao@example.com'), (SELECT id FROM users WHERE email='julia@example.com'), 4, 'Jardinagem bem feita, meu jardim está lindo.', NOW() - INTERVAL 3 DAY);

-- 4. Inserir Contratos
INSERT INTO contratos (id, cliente_id, prestador_id, descricao_servico, data_contrato, status) VALUES
(UUID(), (SELECT id FROM users WHERE email='joao@example.com'), (SELECT id FROM users WHERE email='ana@example.com'), 'Conserto de vazamento na cozinha', NOW() - INTERVAL 15 DAY, 'FINALIZADO'),
(UUID(), (SELECT id FROM users WHERE email='maria@example.com'), (SELECT id FROM users WHERE email='pedro@example.com'), 'Instalação de tomadas no quarto', NOW() - INTERVAL 10 DAY, 'EM_ANDAMENTO'),
(UUID(), (SELECT id FROM users WHERE email='joao@example.com'), (SELECT id FROM users WHERE email='carla@example.com'), 'Pintura quarto infantil', NOW() - INTERVAL 3 DAY, 'PENDENTE'),
(UUID(), (SELECT id FROM users WHERE email='maria@example.com'), (SELECT id FROM users WHERE email='ana@example.com'), 'Troca de torneira do banheiro', NOW() - INTERVAL 20 DAY, 'FINALIZADO'),
(UUID(), (SELECT id FROM users WHERE email='carlos@example.com'), (SELECT id FROM users WHERE email='lucas@example.com'), 'Desenvolvimento de Landing Page', NOW() - INTERVAL 2 DAY, 'EM_ANDAMENTO'),
(UUID(), (SELECT id FROM users WHERE email='joao@example.com'), (SELECT id FROM users WHERE email='julia@example.com'), 'Manutenção de jardim', NOW() - INTERVAL 4 DAY, 'PENDENTE'),
(UUID(), (SELECT id FROM users WHERE email='maria@example.com'), (SELECT id FROM users WHERE email='fer@example.com'), 'Limpeza completa do apartamento', NOW() - INTERVAL 1 DAY, 'PENDENTE');

-- 5. Inserir Projetos de Construção (Novos dados)
INSERT INTO projetos (id, cliente_id, prestador_id, titulo, tipo_servico, descricao, data_inicio_estimada, data_fim_estimada, status, custo_estimado_materiais, custo_estimado_mao_de_obra, observacoes_prestador) 
VALUES
(UUID(), (SELECT id FROM users WHERE email='joao@example.com'), (SELECT id FROM users WHERE email='ana@example.com'), 'Reforma da Cozinha', 'Encanador', 'Troca de pisos, armários e bancadas', '2025-12-01', '2025-12-30', 'EM_ANDAMENTO', 2500.00, 1500.00, 'Aguardando entrega do granito para bancada.'),
(UUID(), (SELECT id FROM users WHERE email='maria@example.com'), (SELECT id FROM users WHERE email='carla@example.com'), 'Pintura Externa da Casa', 'Pintor', 'Pintura completa da fachada e muros', '2026-01-10', '2026-01-20', 'ORCAMENTO_PENDENTE', 1000.00, 800.00, NULL),
(UUID(), (SELECT id FROM users WHERE email='carlos@example.com'), (SELECT id FROM users WHERE email='lucas@example.com'), 'Site E-commerce', 'Programador', 'Criação de plataforma de e-commerce com catálogo.', '2026-02-01', '2026-03-15', 'ORCAMENTO_PENDENTE', 0.00, 5000.00, 'Aguardando aprovação do layout inicial.');

-- 6. Inserir Itens de Orçamento para o Projeto 1
INSERT INTO itens_orcamento (id, projeto_id, nome, unidade, quantidade, valor_unitario, valor_total) VALUES
(UUID(), (SELECT id FROM projetos WHERE titulo='Reforma da Cozinha'), 'Piso Porcelanato', 'm2', 20.0, 50.00, 1000.00),
(UUID(), (SELECT id FROM projetos WHERE titulo='Reforma da Cozinha'), 'Argamassa', 'kg', 25.0, 10.00, 250.00);

-- 7. Inicializar Conversas (Chat)
INSERT INTO conversas (id, user1_id, user2_id) VALUES
(UUID(), (SELECT id FROM users WHERE email='joao@example.com'), (SELECT id FROM users WHERE email='ana@example.com')),
(UUID(), (SELECT id FROM users WHERE email='maria@example.com'), (SELECT id FROM users WHERE email='pedro@example.com'));

-- 8. Inserir Mensagens Iniciais
INSERT INTO mensagens (id, conversa_id, remetente_id, destinatario_id, conteudo) VALUES
(UUID(), (SELECT id FROM conversas WHERE user1_id=(SELECT id FROM users WHERE email='joao@example.com') LIMIT 1), (SELECT id FROM users WHERE email='joao@example.com'), (SELECT id FROM users WHERE email='ana@example.com'), 'Olá Ana, você está disponível para um vazamento urgente?'),
(UUID(), (SELECT id FROM conversas WHERE user1_id=(SELECT id FROM users WHERE email='joao@example.com') LIMIT 1), (SELECT id FROM users WHERE email='ana@example.com'), (SELECT id FROM users WHERE email='joao@example.com'), 'Olá João! Posso ir agora, qual o endereço?'),
(UUID(), (SELECT id FROM conversas WHERE user1_id=(SELECT id FROM users WHERE email='maria@example.com') LIMIT 1), (SELECT id FROM users WHERE email='maria@example.com'), (SELECT id FROM users WHERE email='pedro@example.com'), 'Pedro, quero instalar umas tomadas novas. Qual seu valor?');

-- Fim do Script esta é minha tabela do meu banco e dados

-- ==========================================================
-- MIGRAÇÃO: adiciona colunas financeiras em `projetos` se ausentes
-- ==========================================================
-- Nota: usa `ADD COLUMN IF NOT EXISTS` (MySQL 8+). Se seu MySQL for mais antigo,
-- execute manualmente os ALTERs apropriados.
ALTER TABLE projetos
    ADD COLUMN IF NOT EXISTS valor_mao_de_obra DECIMAL(12,2) DEFAULT 0.00,
    ADD COLUMN IF NOT EXISTS custo_real_materiais DECIMAL(12,2) DEFAULT 0.00,
    ADD COLUMN IF NOT EXISTS custo_real_mao_de_obra DECIMAL(12,2) DEFAULT 0.00;

