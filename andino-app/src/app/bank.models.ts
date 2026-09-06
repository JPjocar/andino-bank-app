export interface Account {
  id: string;
  ownerName: string;
  accountNumber: string;
  balance: number;
  currency: string;
  createdAt: string;
}

export interface Transaction {
  id: string;
  accountId: string;
  type: 'DEPOSIT' | 'WITHDRAWAL' | 'TRANSFER_IN' | 'TRANSFER_OUT';
  amount: number;
  description: string;
  createdAt: string;
}

export interface CreateAccountRequest {
  ownerName: string;
  initialBalance: number;
}

export interface TransferRequest {
  fromAccountId: string;
  toAccountId: string;
  amount: number;
}
