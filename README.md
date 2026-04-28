# TMDB Movies Challenge

App Android nativo de listagem de filmes usando a API do TMDB.

## Setup

1. Copie `local.properties.example` para `local.properties`
2. Insira seu bearer token TMDB:
   ```
   TMDB_API_KEY=seu_bearer_token_aqui
   ```
3. Abra no Android Studio (Gradle JDK: openjdk-21) e rode o app

> Bearer token (não a API key v3): obtenha em https://www.themoviedb.org/settings/api

## Qualidade de código

Detekt + ktlint configurados. Rodar antes de commit:

```bash
./gradlew detekt          # análise estática (Compose-aware via compose-rules)
./gradlew ktlintCheck     # formatação
./gradlew ktlintFormat    # auto-fix formatação
./gradlew test            # testes unitários
./gradlew check           # todos acima
```

Config Detekt em `config/detekt/detekt.yml`. Inclui regras Compose via `io.nlopez.compose.rules:detekt`.

## Cobertura de testes

Kover ativo em todos os módulos. Roda + abre relatório:
```bash
./gradlew koverHtmlReport && open build/reports/kover/html/index.html
./gradlew koverXmlReport   # CI
```

Cobertura atual (LINE): **84.9%** focada em use cases / VMs / repositories / mappers (network/UI infra excluídos do alvo).

| Camada | % |
|--------|---|
| Domain models | 100% |
| Use case impls | 100% |
| Mappers | 100% |
| Detail VM/Screen | 94% |
| Designsystem components | 91% |
| Repository | 87% |
| Home VM/Screen | 84% |
| DTOs | 81% |
| PagingSources | 52% |

Ajustes em `build.gradle.kts` (root) — Kover excludes para Room-gerado, Composables, BuildConfig, theme tokens.

## CI

GitHub Actions em `.github/workflows/ci.yml`. Roda em cada PR + push para `main`:
1. ktlint
2. detekt
3. testes unitários (release variant)
4. assemble APK release (assinado se secrets configurados)
5. upload APK como artifact (`app-release`, 7 dias)
6. upload de relatórios (test + detekt) como artifacts

Cache do Gradle compartilhado entre runs. PR não escreve no cache (read-only).

### Signing

O APK é assinado automaticamente se os seguintes secrets estiverem configurados no repositório:

| Secret | Descrição |
|--------|-----------|
| `KEYSTORE_BASE64` | Keystore codificado em base64 (`base64 -i release.jks`) |
| `KEYSTORE_PASSWORD` | Senha do keystore |
| `KEY_ALIAS` | Alias da chave |
| `KEY_PASSWORD` | Senha da chave |

Sem os secrets, o CI gera um APK não-assinado normalmente.

## Telas

| Home | Detalhe |
|------|---------|
| Grid paginado de filmes populares | Hero poster 360dp com scrim |
| Busca inline com debounce 300ms | Título aparece ao scrollar (collapsing bar) |
| Filtro por gênero (chips scrolláveis) | Gêneros, sinopse, ano, runtime, nota |
| Aba Favoritos (offline, Room) | Botão CTA favoritar / remover |
| FAB shuffle para favorito aleatório | Transição fade + slide de volta |
| Banner offline automático | |
| Skeleton, empty state, error state | |

## Arquitetura

```
app/
├── feature/
│   ├── home/
│   │   ├── public/   ← HomeRoute (@Serializable)
│   │   └── impl/     ← HomeViewModel, HomeScreen, HomeNavigation
│   └── detail/
│       ├── public/   ← DetailRoute (@Serializable)
│       └── impl/     ← DetailViewModel, DetailScreen, DetailNavigation
├── domain/movies/
│   ├── public/       ← MoviesRepository (interface), UseCases (fun interface), Models
│   └── impl/         ← UseCaseImpl, DomainMoviesKoinModule
├── data/movies/
│   ├── public/       ← MoviesDataModule (Koin)
│   └── impl/         ← MoviesRepositoryImpl, RemoteMediator, PagingSources, Mapper
├── core/
│   ├── common/       ← DispatcherProvider, ConnectivityObserver
│   ├── database/
│   │   └── impl/     ← Room DAOs, Entities, MoviesDatabase (encapsulado)
│   ├── designsystem/ ← Tema M3, Spacing/Dimens tokens, MovieCard, FilterChipRow, ErrorState…
│   ├── network/
│   │   ├── public/   ← ImageUrlBuilder
│   │   └── impl/     ← Retrofit, AuthInterceptor
│   └── testing/      ← MainDispatcherRule, TestDispatcherProvider
```

**MVVM + Clean Architecture:** UI → ViewModel → UseCase → Repository → DataSource. Cada camada depende apenas da abstração da camada abaixo via `:public`. ViewModel expõe `StateFlow<UiState>` e a Screen é stateless — recebe dados e callbacks como parâmetros.

**Padrão public/impl:** cada módulo expõe apenas interfaces e modelos via `:public`; a implementação fica em `:impl` e não vaza dependências de framework.

**Inicialização de ViewModel:** lógica de inicialização (coleta de flows, carregamento inicial) fica em `onViewCreated()`, chamado via `LaunchedEffect(viewModel)` no arquivo de Navigation — não em `init`. Isso mantém o ViewModel testável sem efeitos colaterais na construção.

## Stack

| Camada | Tecnologia |
|--------|-----------|
| UI | Jetpack Compose + Material 3 |
| Navegação | Navigation Compose (type-safe routes) |
| DI | Koin 4.0 |
| Paginação | Paging 3 + RemoteMediator (offline-first) |
| Cache local | Room |
| Rede | Retrofit + OkHttp |
| Imagens | Coil 3 |
| Async | Coroutines + StateFlow |
| Conectividade | ConnectivityManager + NetworkCallback |

## Design tokens

Strings em `res/values/strings.xml` por módulo (i18n-ready).
Spacing semântico (`xxs..xxxl`) em `core/designsystem/theme/Spacing.kt` exposto via `MaterialTheme.spacing.xs`.
Dimens fixos (alturas, ícones, raios) em `Dimens` object.

## Offline-first

Filmes populares são armazenados no Room via `RemoteMediator`. Ao abrir sem internet, o app exibe o cache local. O banner de offline aparece automaticamente via `NET_CAPABILITY_VALIDATED`.

Favoritos são salvos localmente e sempre disponíveis offline.

## Testes

```bash
JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home \
  ./gradlew test
```

| Camada | Cobertura |
|--------|-----------|
| Domain | `MoviesRepositoryImpl`, `MovieMapper`, todos use cases (`UseCasesTest`) |
| Data | `PagingSourceTest` (Now Playing, Discover, Search) |
| Feature ViewModels | `HomeViewModel` (16 cenários), `DetailViewModel` (8 cenários) |
| Compose UI (JVM) | `HomeScreenTest`, `DetailScreenTest` (Robolectric + `createComposeRule`) |
| Designsystem | `MovieCard`, `FilterChipRow`, `EmptyState`, `ErrorState`, `OfflineBanner` |
| Navigation | rotas type-safe, round-trip serializável |
| **E2E instrumentado** | `UserJourneyTest` (11 flows) + `ConfigChangeTest` (4 cenários de rotation/process death) — `app/src/androidTest/`, MockWebServer + Koin override + Room in-memory |

**Rodar instrumentados** (precisa emulador conectado — não rodam no CI):
```bash
./gradlew :app:connectedDebugAndroidTest
```

Os testes E2E seguem o [guia oficial Compose Testing](https://developer.android.com/develop/ui/compose/testing):
- `createAndroidComposeRule<MainActivity>()` para Activity real
- Finders semânticos: `onNodeWithText`, `onNodeWithContentDescription`, `onNodeWithTag`
- Sincronização: `composeTestRule.waitUntil { … }` (encapsulado em `waitUntilTextDisplayed` para reduzir boilerplate)
- Asserções explícitas após cada wait (`assertIsDisplayed()`)
- Ordem de regras via `RuleChain` (MockWebServer → Koin → Activity)

**Padrão de fakes:** uso de `fun interface` permite criar fakes via lambda em vez de mocks. Exemplo:
```kotlin
val getPopular = GetPopularMoviesUseCase { emptyFlow() }
```

ViewModels usam `MainDispatcherRule` (`UnconfinedTestDispatcher`) para controle de coroutines em testes. Testes que dependem de inicialização chamam `viewModel.onViewCreated()` explicitamente após criar a instância.
