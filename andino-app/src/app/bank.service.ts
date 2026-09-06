import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Account, CreateAccountRequest, Transaction, TransferRequest } from './bank.models';

@Injectable({ providedIn: 'root' })
export class BankService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/v1';

  getAccounts() {
    return this.http.get<Account[]>(`${this.apiUrl}/accounts`);
  }

  createAccount(request: CreateAccountRequest) {
    return this.http.post<Account>(`${this.apiUrl}/accounts`, request);
  }

  getTransactions(accountId: string) {
    return this.http.get<Transaction[]>(`${this.apiUrl}/accounts/${accountId}/transactions`);
  }

  deposit(accountId: string, amount: number) {
    return this.http.post<Account>(`${this.apiUrl}/accounts/${accountId}/deposit`, { amount });
  }

  withdraw(accountId: string, amount: number) {
    return this.http.post<Account>(`${this.apiUrl}/accounts/${accountId}/withdraw`, { amount });
  }

  transfer(request: TransferRequest) {
    return this.http.post<Account>(`${this.apiUrl}/transfers`, request);
  }
}
