CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    owner_name VARCHAR(255) NOT NULL,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    balance NUMERIC(19, 2) NOT NULL CHECK (balance >= 0),
    currency VARCHAR(3) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER_IN', 'TRANSFER_OUT')),
    amount NUMERIC(19, 2) NOT NULL CHECK (amount > 0),
    description VARCHAR(120) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_transactions_account FOREIGN KEY (account_id) REFERENCES accounts (id)
);

CREATE INDEX idx_transactions_account_created
    ON transactions (account_id, created_at DESC);
