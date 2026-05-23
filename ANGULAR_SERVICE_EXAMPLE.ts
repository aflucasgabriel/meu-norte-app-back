/**
 * EXEMPLO: Serviço para chamar a API Finance App do Angular
 *
 * Copie e adapte este código no seu projeto Angular
 * Dependências necessárias:
 * - Angular HttpClient
 * - HttpClientModule importado no AppModule
 *
 * npm install axios  (ou use o HttpClient nativo do Angular)
 */

// ===== EXEMPLO COM Angular HttpClient (Recomendado) =====

import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';

interface LoginRequest {
    email: string;
    senha: string;
}

interface LoginResponse {
    token: string;
    tipo: string;
    userId: number;
    nome: string;
}

interface TransacaoRequest {
    tipo: 'D' | 'R';
    valor: number;
    idCategoria: number;
    descricao?: string;
    dataTransacao?: string;
}

interface TransacaoResponse {
    idTransacao: number;
    idUsuario: number;
    idCategoria: number;
    nomeCategoria: string;
    valor: number;
    dataHoraTransacao: string;
    descricao: string;
    tipo: string;
    tipoDescricao: string;
}

interface ResumoMensalResponse {
    mes: number;
    ano: number;
    totalReceitas: number;
    totalDespesas: number;
    saldo: number;
    despesasPorCategoria: { categoria: string; total: number }[];
    receitasPorCategoria: { categoria: string; total: number }[];
}

interface EvolucaoMensalResponse {
    ano: number;
    receitas: { mes: number; total: number }[];
    despesas: { mes: number; total: number }[];
}

@Injectable({
    providedIn: 'root'
})
export class FinanceApiService {

    private baseUrl = 'http://localhost:8080/api';
    private tokenKey = 'finance_token';

    constructor(private http: HttpClient) {}

    // ===== AUTENTICAÇÃO =====

    /**
     * Registra um novo usuário
     */
    register(nome: string, email: string, senha: string) {
        return this.http.post(`${this.baseUrl}/users/register`, {
            nome,
            email,
            senha
        });
    }

    /**
     * Realiza login e armazena token
     */
    login(email: string, senha: string): Observable<LoginResponse> {
        return this.http.post<LoginResponse>(`${this.baseUrl}/auth/login`, {
            email,
            senha
        }).pipe(
            tap((response) => {
                localStorage.setItem(this.tokenKey, response.token);
            }),
            catchError(this.handleError)
        );
    }

    /**
     * Retorna token armazenado
     */
    getToken(): string | null {
        return localStorage.getItem(this.tokenKey);
    }

    /**
     * Remove token (logout)
     */
    logout(): void {
        localStorage.removeItem(this.tokenKey);
    }

    /**
     * Verifica se usuário está autenticado
     */
    isAuthenticated(): boolean {
        return !!this.getToken();
    }

    // ===== TRANSAÇÕES =====

    /**
     * Cria nova transação
     */
    criarTransacao(transacao: TransacaoRequest): Observable<TransacaoResponse> {
        return this.http.post<TransacaoResponse>(
            `${this.baseUrl}/transaction`,
            transacao,
            this.getHeaders()
        ).pipe(
            catchError(this.handleError)
        );
    }

    /**
     * Lista todas as transações do usuário
     */
    listarTransacoes(): Observable<TransacaoResponse[]> {
        return this.http.get<TransacaoResponse[]>(
            `${this.baseUrl}/transaction`,
            this.getHeaders()
        ).pipe(
            catchError(this.handleError)
        );
    }

    /**
     * Lista transações por tipo (D = despesa, R = receita)
     */
    listarPorTipo(tipo: 'D' | 'R'): Observable<TransacaoResponse[]> {
        return this.http.get<TransacaoResponse[]>(
            `${this.baseUrl}/transaction/tipo/${tipo}`,
            this.getHeaders()
        ).pipe(
            catchError(this.handleError)
        );
    }

    /**
     * Lista transações de um mês específico
     */
    listarPorMes(mes: number, ano: number): Observable<TransacaoResponse[]> {
        return this.http.get<TransacaoResponse[]>(
            `${this.baseUrl}/transaction/mes?mes=${mes}&ano=${ano}`,
            this.getHeaders()
        ).pipe(
            catchError(this.handleError)
        );
    }

    /**
     * Lista transações em um período
     */
    listarPorPeriodo(inicio: string, fim: string): Observable<TransacaoResponse[]> {
        return this.http.get<TransacaoResponse[]>(
            `${this.baseUrl}/transaction/periodo?inicio=${inicio}&fim=${fim}`,
            this.getHeaders()
        ).pipe(
            catchError(this.handleError)
        );
    }

    /**
     * Lista transações de uma categoria
     */
    listarPorCategoria(idCategoria: number): Observable<TransacaoResponse[]> {
        return this.http.get<TransacaoResponse[]>(
            `${this.baseUrl}/transaction/categoria/${idCategoria}`,
            this.getHeaders()
        ).pipe(
            catchError(this.handleError)
        );
    }

    /**
     * Deleta uma transação
     */
    deletarTransacao(idTransacao: number): Observable<void> {
        return this.http.delete<void>(
            `${this.baseUrl}/transaction/${idTransacao}`,
            this.getHeaders()
        ).pipe(
            catchError(this.handleError)
        );
    }

    // ===== DASHBOARD =====

    /**
     * Resumo mensal: totais + breakdown por categoria
     */
    resumoMensal(mes: number, ano: number): Observable<ResumoMensalResponse> {
        return this.http.get<ResumoMensalResponse>(
            `${this.baseUrl}/transaction/dashboard/resumo?mes=${mes}&ano=${ano}`,
            this.getHeaders()
        ).pipe(
            catchError(this.handleError)
        );
    }

    /**
     * Evolução anual: mês a mês
     */
    evolucaoAnual(ano: number): Observable<EvolucaoMensalResponse> {
        return this.http.get<EvolucaoMensalResponse>(
            `${this.baseUrl}/transaction/dashboard/evolucao?ano=${ano}`,
            this.getHeaders()
        ).pipe(
            catchError(this.handleError)
        );
    }

    // ===== HELPERS =====

    /**
     * Constrói headers com autenticação JWT
     */
    private getHeaders() {
        const token = this.getToken();
        return {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        };
    }

    /**
     * Trata erros HTTP
     */
    private handleError(error: HttpErrorResponse) {
        let errorMessage = 'Erro ao processar requisição';

        if (error.error instanceof ErrorEvent) {
            errorMessage = error.error.message;
        } else {
            errorMessage = error.error?.erro || `Erro: ${error.status} - ${error.statusText}`;
        }

        console.error('Erro API:', errorMessage);
        return throwError(() => new Error(errorMessage));
    }
}

// ===== EXEMPLO DE USO NO COMPONENTE =====

/*
import { Component, OnInit } from '@angular/core';
import { FinanceApiService } from './services/finance-api.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  resumo: any;
  evolucao: any;
  transacoes: any[] = [];

  constructor(private api: FinanceApiService) {}

  ngOnInit(): void {
    this.carregarDados();
  }

  carregarDados(): void {
    const mesAtual = new Date().getMonth() + 1;
    const anoAtual = new Date().getFullYear();

    // Carregar resumo do mês
    this.api.resumoMensal(mesAtual, anoAtual).subscribe({
      next: (data) => this.resumo = data,
      error: (err) => console.error('Erro ao carregar resumo', err)
    });

    // Carregar evolução anual
    this.api.evolucaoAnual(anoAtual).subscribe({
      next: (data) => this.evolucao = data,
      error: (err) => console.error('Erro ao carregar evolução', err)
    });

    // Carregar transações
    this.api.listarTransacoes().subscribe({
      next: (data) => this.transacoes = data,
      error: (err) => console.error('Erro ao carregar transações', err)
    });
  }

  criarDespesa(): void {
    this.api.criarTransacao({
      tipo: 'D',
      valor: 150.50,
      idCategoria: 1,
      descricao: 'Uber para o trabalho'
    }).subscribe({
      next: (novaTransacao) => {
        console.log('Transação criada:', novaTransacao);
        this.carregarDados(); // Recarregar dados
      },
      error: (err) => console.error('Erro ao criar transação', err)
    });
  }

  deletarTransacao(id: number): void {
    this.api.deletarTransacao(id).subscribe({
      next: () => {
        console.log('Transação deletada');
        this.carregarDados();
      },
      error: (err) => console.error('Erro ao deletar', err)
    });
  }
}
*/

