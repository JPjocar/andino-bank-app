INSERT INTO accounts (id, owner_name, account_number, balance, currency, created_at)
SELECT '11111111-1111-1111-1111-111111111111', 'Ana Quispe', 'AND-00000001', 1250.00, 'USD', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM accounts WHERE id = '11111111-1111-1111-1111-111111111111');

INSERT INTO accounts (id, owner_name, account_number, balance, currency, created_at)
SELECT '22222222-2222-2222-2222-222222222222', 'Luis Condori', 'AND-00000002', 500.00, 'USD', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM accounts WHERE id = '22222222-2222-2222-2222-222222222222');

INSERT INTO transactions (id, account_id, type, amount, description, created_at)
SELECT 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'DEPOSIT', 1250.00, 'Initial balance', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM transactions WHERE id = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa');

INSERT INTO transactions (id, account_id, type, amount, description, created_at)
SELECT 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222', 'DEPOSIT', 500.00, 'Initial balance', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM transactions WHERE id = 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb');
