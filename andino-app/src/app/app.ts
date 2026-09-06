import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, computed, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Observable } from 'rxjs';
import { Account, Transaction } from './bank.models';
import { BankService } from './bank.service';

@Component({
  selector: 'app-root',
  imports: [FormsModule, CurrencyPipe, DatePipe],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly accounts = signal<Account[]>([]);
  protected readonly selectedAccount = signal<Account | null>(null);
  protected readonly transactions = signal<Transaction[]>([]);
  protected readonly message = signal('');
  protected readonly error = signal('');
  protected readonly totalBalance = computed(() =>
    this.accounts().reduce((total, account) => total + account.balance, 0)
  );
  protected readonly otherAccounts = computed(() => {
    const selectedId = this.selectedAccount()?.id;
    return this.accounts().filter(account => account.id !== selectedId);
  });

  protected operationAmount = 0;
  protected transferAmount = 0;
  protected destinationId = '';
  protected ownerName = '';
  protected initialBalance = 0;

  constructor(private readonly bankService: BankService) {}

  ngOnInit(): void {
    this.loadAccounts();
  }

  protected selectAccount(account: Account): void {
    this.selectedAccount.set(account);
    this.loadTransactions(account.id);
  }

  protected deposit(): void {
    const account = this.selectedAccount();
    if (account) this.execute(this.bankService.deposit(account.id, this.operationAmount), 'Deposit completed');
  }

  protected withdraw(): void {
    const account = this.selectedAccount();
    if (account) this.execute(this.bankService.withdraw(account.id, this.operationAmount), 'Withdrawal completed');
  }

  protected transfer(): void {
    const account = this.selectedAccount();
    if (!account || !this.destinationId) return;
    this.execute(this.bankService.transfer({
      fromAccountId: account.id,
      toAccountId: this.destinationId,
      amount: this.transferAmount
    }), 'Transfer completed');
  }

  protected createAccount(): void {
    if (!this.ownerName.trim()) return;
    this.bankService.createAccount({ ownerName: this.ownerName, initialBalance: this.initialBalance }).subscribe({
      next: account => {
        this.ownerName = '';
        this.initialBalance = 0;
        this.message.set('Account created');
        this.loadAccounts(account.id);
      },
      error: response => this.showError(response)
    });
  }

  private execute(operation: Observable<Account>, successMessage: string): void {
    this.message.set('');
    this.error.set('');
    operation.subscribe({
      next: account => {
        this.message.set(successMessage);
        this.operationAmount = 0;
        this.transferAmount = 0;
        this.loadAccounts(account.id);
      },
      error: response => this.showError(response)
    });
  }

  private loadAccounts(selectedId?: string): void {
    this.bankService.getAccounts().subscribe({
      next: accounts => {
        this.accounts.set(accounts);
        const selected = accounts.find(account => account.id === selectedId)
          ?? accounts.find(account => account.id === this.selectedAccount()?.id)
          ?? accounts[0]
          ?? null;
        this.selectedAccount.set(selected);
        if (selected) this.loadTransactions(selected.id);
      },
      error: response => this.showError(response)
    });
  }

  private loadTransactions(accountId: string): void {
    this.bankService.getTransactions(accountId).subscribe({
      next: transactions => this.transactions.set(transactions),
      error: response => this.showError(response)
    });
  }

  private showError(response: { error?: { message?: string } }): void {
    this.message.set('');
    this.error.set(response.error?.message ?? 'Could not connect to Andino API');
  }
}
