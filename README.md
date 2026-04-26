# iFood Android Challenge — Movies App

App Android nativo de listagem de filmes usando a API do TMDB.

## Setup

1. Copie `local.properties.example` para `local.properties`
2. Insira seu bearer token TMDB:
   ```
   TMDB_API_KEY=seu_bearer_token_aqui
   ```
3. Abra no Android Studio (Gradle JDK: openjdk-21) e rode o app

> Bearer token (não a API key v3): obtenha em https://www.themoviedb.org/settings/api

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

**Padrão public/impl:** cada módulo expõe apenas interfaces e modelos via `:public`; a implementação fica em `:impl` e não vaza dependências de framework.

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
| Feature ViewModels | `HomeViewModel` (15 cenários), `DetailViewModel` (8 cenários) |
| Compose UI | `HomeScreenTest`, `DetailScreenTest` (Robolectric + `createComposeRule`) |

**Padrão de fakes:** uso de `fun interface` permite criar fakes via lambda em vez de mocks. Exemplo:
```kotlin
val getPopular = GetPopularMoviesUseCase { emptyFlow() }
```

ViewModels usam `MainDispatcherRule` (`UnconfinedTestDispatcher`) para controle de coroutines em testes.
