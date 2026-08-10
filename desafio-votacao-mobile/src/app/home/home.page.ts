import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import {
  IonAccordion,
  IonAccordionGroup,
  IonBadge,
  IonButton,
  IonButtons,
  IonCard,
  IonCardContent,
  IonCardHeader,
  IonCardSubtitle,
  IonCardTitle,
  IonChip,
  IonContent,
  IonHeader,
  IonIcon,
  IonInput,
  IonItem,
  IonLabel,
  IonList,
  IonNote,
  IonSelect,
  IonSelectOption,
  IonSpinner,
  IonTextarea,
  IonTitle,
  IonToolbar,
  ToastController
} from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import {
  addCircleOutline,
  checkmarkCircleOutline,
  createOutline,
  eyeOutline,
  refreshOutline,
  sendOutline,
  settingsOutline,
  timeOutline
} from 'ionicons/icons';

import {
  ApiFailure,
  Pauta,
  VotacaoApiService,
  VotoOpcao
} from '../services/votacao-api.service';

type AcaoPauta = 'sessao' | 'voto' | 'resultado' | null;

interface PautaForm {
  titulo: string;
  descricao: string;
}

interface SessaoForm {
  duracaoMinutos: number | null;
}

interface VotoForm {
  associadoId: number | null;
  cpf: string;
  voto: VotoOpcao;
}

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    IonAccordion,
    IonAccordionGroup,
    IonBadge,
    IonButton,
    IonButtons,
    IonCard,
    IonCardContent,
    IonCardHeader,
    IonCardSubtitle,
    IonCardTitle,
    IonChip,
    IonContent,
    IonHeader,
    IonIcon,
    IonInput,
    IonItem,
    IonLabel,
    IonList,
    IonNote,
    IonSelect,
    IonSelectOption,
    IonSpinner,
    IonTextarea,
    IonTitle,
    IonToolbar
  ]
})
export class HomePage implements OnInit {
  readonly carregando = signal(false);
  readonly status = signal<'pronto' | 'sucesso' | 'erro'>('pronto');
  readonly resposta = signal<unknown>('Selecione uma pauta para votar, abrir sessao ou consultar resultado.');

  readonly apiBaseUrl = signal(localStorage.getItem('apiBaseUrl') || 'http://localhost:8080');
  readonly pautas = signal<Pauta[]>([]);
  readonly pautaSelecionada = signal<Pauta | null>(null);
  readonly acaoSelecionada = signal<AcaoPauta>(null);
  readonly mostrandoCadastro = signal(false);

  readonly statusLabel = computed(() => {
    const atual = this.status();
    if (atual === 'sucesso') {
      return 'Conectado';
    }
    if (atual === 'erro') {
      return 'Erro';
    }
    return 'Pronto';
  });

  readonly pauta: PautaForm = {
    titulo: '',
    descricao: ''
  };

  readonly sessao: SessaoForm = {
    duracaoMinutos: 1
  };

  readonly voto: VotoForm = {
    associadoId: null,
    cpf: '',
    voto: 'SIM'
  };

  constructor(
    private readonly api: VotacaoApiService,
    private readonly toastController: ToastController
  ) {
    addIcons({
      addCircleOutline,
      checkmarkCircleOutline,
      createOutline,
      eyeOutline,
      refreshOutline,
      sendOutline,
      settingsOutline,
      timeOutline
    });
  }

  ngOnInit(): void {
    void this.carregarPautas();
  }

  salvarApiBaseUrl(value: string | number | null | undefined): void {
    const baseUrl = String(value || '').trim() || 'http://localhost:8080';
    this.apiBaseUrl.set(baseUrl);
    localStorage.setItem('apiBaseUrl', baseUrl);
  }

  selecionarPauta(pauta: Pauta, acao: AcaoPauta = null): void {
    this.pautaSelecionada.set(pauta);
    this.acaoSelecionada.set(acao);
    this.resposta.set(`Pauta selecionada: ${pauta.titulo}`);
  }

  async carregarPautas(): Promise<void> {
    await this.executar(async () => {
      const pautas = await firstValueFrom(this.api.listarPautas(this.apiBaseUrl()));
      this.pautas.set(pautas);

      if (pautas.length > 0 && !this.pautaSelecionada()) {
        this.pautaSelecionada.set(pautas[0]);
      }

      return pautas;
    }, 'Pautas carregadas');
  }

  async criarPauta(): Promise<void> {
    await this.executar(async () => {
      const response = await firstValueFrom(
        this.api.criarPauta(this.apiBaseUrl(), {
          titulo: this.pauta.titulo,
          descricao: this.pauta.descricao || undefined
        })
      );

      this.pauta.titulo = '';
      this.pauta.descricao = '';
      this.mostrandoCadastro.set(false);
      await this.carregarPautas();
      return response;
    }, 'Pauta cadastrada');
  }

  async abrirSessao(): Promise<void> {
    const pauta = this.pautaObrigatoria();

    await this.executar(async () => {
      return firstValueFrom(
        this.api.abrirSessao(this.apiBaseUrl(), {
          pautaId: pauta.id,
          duracaoMinutos: this.sessao.duracaoMinutos || null
        })
      );
    }, 'Sessao aberta');
  }

  async registrarVoto(): Promise<void> {
    const pauta = this.pautaObrigatoria();

    await this.executar(async () => {
      return firstValueFrom(
        this.api.registrarVoto(this.apiBaseUrl(), {
          pautaId: pauta.id,
          associadoId: this.numeroObrigatorio(this.voto.associadoId),
          cpf: this.voto.cpf.replace(/\D/g, ''),
          voto: this.voto.voto
        })
      );
    }, 'Voto enviado');
  }

  async consultarResultado(): Promise<void> {
    const pauta = this.pautaObrigatoria();

    await this.executar(async () => {
      return firstValueFrom(
        this.api.consultarResultado(this.apiBaseUrl(), {
          pautaId: pauta.id
        })
      );
    }, 'Resultado consultado');
  }

  async carregarContratoMobile(): Promise<void> {
    await this.executar(async () => {
      return firstValueFrom(this.api.obterTela(this.apiBaseUrl(), 'opcoes'));
    }, 'Contrato mobile carregado');
  }

  formatarResposta(): string {
    const valor = this.resposta();
    return typeof valor === 'string' ? valor : JSON.stringify(valor, null, 2);
  }

  dataCurta(data: string): string {
    return new Intl.DateTimeFormat('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    }).format(new Date(data));
  }

  private pautaObrigatoria(): Pauta {
    const pauta = this.pautaSelecionada();
    if (!pauta) {
      throw {
        status: 0,
        mensagem: 'Selecione uma pauta',
        detalhes: null
      } satisfies ApiFailure;
    }

    return pauta;
  }

  private numeroObrigatorio(value: number | null): number {
    if (!value) {
      throw {
        status: 0,
        mensagem: 'Informe os campos obrigatorios',
        detalhes: null
      } satisfies ApiFailure;
    }

    return Number(value);
  }

  private async executar<T>(acao: () => Promise<T>, mensagemSucesso: string): Promise<void> {
    this.carregando.set(true);

    try {
      const response = await acao();
      this.status.set('sucesso');
      this.resposta.set(response);
      await this.toast(mensagemSucesso, 'success');
    } catch (error) {
      const failure = error as ApiFailure;
      this.status.set('erro');
      this.resposta.set(failure);
      await this.toast(failure.mensagem || 'Falha na operacao', 'danger');
    } finally {
      this.carregando.set(false);
    }
  }

  private async toast(message: string, color: 'success' | 'danger'): Promise<void> {
    const toast = await this.toastController.create({
      message,
      duration: 1800,
      color,
      position: 'bottom'
    });

    await toast.present();
  }
}
