# Desafio Votacao Mobile

App Ionic + Angular para usar o fluxo de votacao do desafio.

## Executar

```powershell
npm.cmd install
npm.cmd start
```

Acesse:

```text
http://localhost:8100
```

Configure a API no campo `Base URL`. O valor padrao e:

```text
http://localhost:8080
```

## Funcionalidades

- Listar pautas cadastradas.
- Cadastrar nova pauta.
- Selecionar uma pauta.
- Abrir sessao de votacao.
- Registrar voto do associado.
- Consultar resultado da pauta.
- Visualizar os contratos JSON de tela do Anexo 1.

O app consome:

```text
GET /api/v1/pautas
```

E os endpoints mobile:

```text
POST /api/v1/mobile/acoes/nova-pauta
POST /api/v1/mobile/acoes/abrir-sessao
POST /api/v1/mobile/acoes/votar
POST /api/v1/mobile/acoes/consultar-resultado
GET /api/v1/mobile/telas/*
```

## Gerar APK Android

Pre-requisitos:

- Node.js e npm.
- Android Studio instalado.
- Android SDK configurado pelo Android Studio.
- JDK compativel com a versao do Android Gradle Plugin instalada pelo Capacitor.

Instale a plataforma Android do Capacitor:

```powershell
cd desafio-votacao-mobile
npm.cmd install
npm.cmd install @capacitor/android --save
```

Gere o build web e sincronize com o projeto Android:

```powershell
npm.cmd run build
npx.cmd cap add android
npx.cmd cap sync android
```

Abra no Android Studio:

```powershell
npx.cmd cap open android
```

No Android Studio, gere o APK:

```text
Build > Generate Signed Bundle / APK > APK
```

Para gerar um APK apenas de debug pelo terminal, depois que a pasta `android/` existir:

```powershell
cd android
.\gradlew.bat assembleDebug
```

O APK de debug fica em:

```text
android/app/build/outputs/apk/debug/app-debug.apk
```

Observacao: se a API estiver rodando na sua maquina e o app estiver em um emulador Android, use `http://10.0.2.2:8080` como `Base URL` no app. Em dispositivo fisico, use o IP da maquina na rede, por exemplo `http://192.168.1.100:8080`.
