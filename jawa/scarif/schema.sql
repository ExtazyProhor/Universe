CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    object_id VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE sessions (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    user_agent TEXT,
    ip_address VARCHAR(255),
    closed BOOLEAN NOT NULL DEFAULT false,
    closed_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_sessions_user_id
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE refresh_tokens (
    id BIGINT PRIMARY KEY,
    token TEXT NOT NULL,
    session_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT false,
    revoked_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_refresh_tokens_session_id
        FOREIGN KEY (session_id) REFERENCES sessions(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_sessions_user_id ON sessions(user_id);
CREATE INDEX idx_refresh_tokens_session_id ON refresh_tokens(session_id);
