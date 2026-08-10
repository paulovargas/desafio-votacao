import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';

export type VotoOpcao = 'SIM' | 'NAO';
export type Operacao = 'pauta' | 'sessao' | 'voto' | 'resultado' | 'telas';

export interface CriarPautaPayload {
  titulo: string;
  descricao?: string;
}

export interface Pauta {
  id: number;
  titulo: string;
  descricao?: string;
  dataCriacao: string;
}

export interface AbrirSessaoPayload {
  pautaId: number;
  duracaoMinutos?: number | null;
}

export interface RegistrarVotoPayload {
  pautaId: number;
  associadoId: number;
  cpf: string;
  voto: VotoOpcao;
}

export interface ConsultarResultadoPayload {
  pautaId: number;
}

export interface ApiFailure {
  status: number;
  mensagem: string;
  detalhes: unknown;
}

@Injectable({ providedIn: 'root' })
export class VotacaoApiService {
  constructor(private readonly http: HttpClient) {}

  listarPautas(baseUrl: string): Observable<Pauta[]> {
    return this.http.get<Pauta[]>(`${this.normalizeBaseUrl(baseUrl)}/api/v1/pautas`).pipe(
      catchError((error: HttpErrorResponse) => throwError(() => this.toFailure(error)))
    );
  }

  criarPauta(baseUrl: string, payload: CriarPautaPayload): Observable<unknown> {
    return this.post(baseUrl, '/api/v1/mobile/acoes/nova-pauta', payload);
  }

  abrirSessao(baseUrl: string, payload: AbrirSessaoPayload): Observable<unknown> {
    return this.post(baseUrl, '/api/v1/mobile/acoes/abrir-sessao', payload);
  }

  registrarVoto(baseUrl: string, payload: RegistrarVotoPayload): Observable<unknown> {
    return this.post(baseUrl, '/api/v1/mobile/acoes/votar', payload);
  }

  consultarResultado(baseUrl: string, payload: ConsultarResultadoPayload): Observable<unknown> {
    return this.post(baseUrl, '/api/v1/mobile/acoes/consultar-resultado', payload);
  }

  obterTela(baseUrl: string, tela: string): Observable<unknown> {
    return this.http.get(`${this.normalizeBaseUrl(baseUrl)}/api/v1/mobile/telas/${tela}`).pipe(
      catchError((error: HttpErrorResponse) => throwError(() => this.toFailure(error)))
    );
  }

  private post(baseUrl: string, path: string, payload: unknown): Observable<unknown> {
    return this.http.post(`${this.normalizeBaseUrl(baseUrl)}${path}`, payload).pipe(
      catchError((error: HttpErrorResponse) => throwError(() => this.toFailure(error)))
    );
  }

  private normalizeBaseUrl(baseUrl: string): string {
    return baseUrl.trim().replace(/\/+$/, '');
  }

  private toFailure(error: HttpErrorResponse): ApiFailure {
    const body = error.error as { mensagem?: string } | string | null;
    const mensagem =
      typeof body === 'object' && body?.mensagem
        ? body.mensagem
        : error.message || 'Falha ao chamar a API';

    return {
      status: error.status,
      mensagem,
      detalhes: error.error
    };
  }
}
