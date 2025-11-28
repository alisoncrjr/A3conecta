DROP DATABASE IF EXISTS conecta;
CREATE DATABASE conecta CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE conecta;

SET FOREIGN_KEY_CHECKS = 0;


CREATE TABLE users (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('CLIENTE', 'PRESTADOR', 'ADMIN') NOT NULL,
    
    -- Campos Comuns (Perfil)
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
    
    -- Índices para otimizar login e buscas
    INDEX idx_user_email (email),
    INDEX idx_user_role_cat (role, categoria)
) ENGINE=InnoDB;


CREATE TABLE projetos (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    cliente_id VARCHAR(36) NOT NULL,
    prestador_id VARCHAR(36), 
    
    titulo VARCHAR(100) NOT NULL,
    tipo_servico VARCHAR(100),
    descricao TEXT,
    
    data_inicio_estimada DATE,
    data_fim_estimada DATE,
    
    -- Status sincronizado com o Enum Java (Model.ProjetoConstrucao.StatusProjeto)
    status ENUM('SOLICITADO', 'ORCAMENTO_PENDENTE', 'AGUARDANDO_CLIENTE', 'APROVADO', 'EM_ANDAMENTO', 'CONCLUIDO', 'CANCELADO') DEFAULT 'SOLICITADO',
    
    -- Campos Financeiros
    valor_mao_de_obra DECIMAL(15, 2) DEFAULT 0.00,
    custo_estimado_materiais DECIMAL(15, 2) DEFAULT 0.00,
    custo_estimado_mao_de_obra DECIMAL(15, 2) DEFAULT 0.00,
    custo_real_materiais DECIMAL(15, 2) DEFAULT 0.00,
    custo_real_mao_de_obra DECIMAL(15, 2) DEFAULT 0.00,
    
    observacoes_prestador TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (cliente_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (prestador_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE itens_orcamento (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    projeto_id VARCHAR(36) NOT NULL,
    
    nome VARCHAR(150) NOT NULL,
    unidade VARCHAR(20) DEFAULT 'un',
    quantidade DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    valor_unitario DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    valor_total DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    
    FOREIGN KEY (projeto_id) REFERENCES projetos(id) ON DELETE CASCADE
) ENGINE=InnoDB;


CREATE TABLE contratos (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    cliente_id VARCHAR(36),
    prestador_id VARCHAR(36),
    projeto_id VARCHAR(36), 
    
    descricao_servico TEXT,
    data_contrato DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    -- Status sincronizado com Enum Java (Model.Contrato.ContratoStatus)
    status ENUM('PENDENTE', 'EM_ANDAMENTO', 'FINALIZADO', 'CANCELADO') DEFAULT 'PENDENTE',
    
    -- Controle Financeiro do Contrato
    valor_total DECIMAL(15, 2) DEFAULT 0.00,
    valor_recebido DECIMAL(15, 2) DEFAULT 0.00,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (cliente_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (prestador_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (projeto_id) REFERENCES projetos(id) ON DELETE SET NULL
) ENGINE=InnoDB;


CREATE TABLE avaliacoes (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    cliente_id VARCHAR(36) NOT NULL,
    prestador_id VARCHAR(36) NOT NULL,
    
    pontuacao INT NOT NULL CHECK (pontuacao BETWEEN 1 AND 5),
    comentario TEXT,
    data_avaliacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (cliente_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (prestador_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;


CREATE TABLE conversas (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    user1_id VARCHAR(36) NOT NULL,
    user2_id VARCHAR(36) NOT NULL,
    ultima_atividade DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user1_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (user2_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Garante unicidade de conversa entre duas pessoas
    UNIQUE KEY uk_conversas_users (user1_id, user2_id)
) ENGINE=InnoDB;


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

