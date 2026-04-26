# Handoff: Movie App (iFood Android Challenge)

## Overview

App Android nativo de listagem de filmes (TMDB) — desafio técnico iFood.
**Escopo: 2 telas** (Home + Detail) com foco em arquitetura multi-módulo
(Clean Architecture, Compose, Koin, offline-first, Paging 3 + RemoteMediator).

## Sobre os arquivos de design

Os arquivos deste bundle são **referências de design criadas em HTML + React**
— protótipos interativos que mostram o look & feel e o comportamento
pretendidos. **Não são código de produção para copiar direto.**

A tarefa do Claude Code é **recriar esses designs em Jetpack Compose (Material 3)**,
seguindo o plano multi-módulo documentado em `PLANO.md` (ou equivalente no
projeto Android). Use Compose `MaterialTheme`, `Material3` componentes oficiais,
e tokens M3 — não tente portar estilos inline CSS.

## Fidelidade

**Hi-fi** — cores, tipografia, spacing, comportamentos e motion estão definidos.
Implementar pixel-perfect (dentro do razoável para Compose M3).

---

## Telas

### 1. Home — Listagem

**Propósito:** grid paginado de filmes populares com filtro (Popular/Favoritos),
busca inline e ações rápidas de favoritar.

**Layout:**
- Top App Bar M3 (64dp, `smallTopAppBar`), título "Filmes" + ícone de busca
- Filter chip row (32dp) abaixo da app bar, padding 16dp horizontal, gap 8dp
- Offline banner (quando `NetworkStatus == Unavailable`) — tonal warning
- Grid 2 colunas, gap 12dp, padding 16dp lateral + 12dp top + 96dp bottom (FAB)
- FAB primary container (56dp, radius 16dp) no canto inferior direito, ação "shuffle"

**Estados:**
- `Loading` → 6 skeleton cards com shimmer
- `Success` → grid de MovieCards
- `Empty (Favoritos vazio)` → icon container 80dp + `titleMedium` + `bodyMedium`
- `Empty (busca sem resultado)` → idem, ícone `search_off`
- `Error (network)` → `ErrorState variant="network"` — ícone `signal_wifi_connected_no_internet_4`
- `Error (generic)` → `variant="generic"` — ícone `error`
- `Error (timeout)` → `variant="timeout"` — ícone `schedule`
- `Error (server)` → `variant="server"` — ícone `cloud_off`

**Comportamentos:**
- Busca com debounce **300ms** (conforme plano)
- Ícone de busca expande inline na app bar; ESC/back fecha
- App bar ganha elevação tonal (`surfaceContainer`) ao scrollar (M3 spec)
- Favoritar inline no card — animação spring scale 1.0→1.25→1.0, 280ms
  cubic-bezier(0.34, 1.56, 0.64, 1)
- Tap no card → navega para Detail com shared element transition (ou fade+slide)

### 2. Detail

**Propósito:** informações completas do filme + ação de favoritar.

**Layout:**
- Hero image 360dp (poster w500 TMDB, `ContentScale.Crop`)
- Scrim gradient top (0.55 alpha → transparent) para legibilidade do back button
- Scrim gradient bottom fade para `surface`
- Back button (48dp) top-left com background `rgba(0,0,0,0.35)` sobre poster
- Favorite button (48dp) top-right — mesmo tratamento
- Content padding horizontal 24dp, bottom 120dp
- Título `headlineSmall` → meta row (ano · rating) → assist chips de gênero (32dp, outlined)
- Section "Sinopse" (`titleMedium`) + overview (`bodyMedium`)
- Primary CTA full-width 56dp, corner-pill (radius 28dp), Filled Button M3

**Comportamentos:**
- Collapsing top app bar: título aparece ao scrollar > 140px; background fade para `surfaceContainer`
- Back preserva scroll position da Home (ver plano — NavController `savedStateHandle`)
- Favoritar: mesmo spring scale; botão CTA alterna estilo filled → outlined quando `isFavorite`

---

## Design Tokens (Material 3)

### Color — Dark (default)
Usar `dynamicDarkColorScheme` (Android 12+) com fallback para paleta seed:

| Token | Valor base (hue=255°) |
|---|---|
| `primary` | oklch(0.80 0.12 255) ≈ `#B8C4FF` |
| `onPrimary` | oklch(0.22 0.10 255) ≈ `#1F2A5C` |
| `primaryContainer` | oklch(0.33 0.12 255) ≈ `#3A4478` |
| `onPrimaryContainer` | oklch(0.90 0.08 255) ≈ `#DDE1FF` |
| `surface` | oklch(0.18 0.01 255) ≈ `#1A1B20` |
| `surfaceContainerLow` | oklch(0.20 0.01 255) |
| `surfaceContainer` | oklch(0.22 0.01 255) |
| `surfaceContainerHigh` | oklch(0.25 0.01 255) |
| `surfaceContainerHighest` | oklch(0.28 0.01 255) |
| `onSurface` | oklch(0.92 0.01 255) |
| `onSurfaceVariant` | oklch(0.75 0.01 255) |
| `outline` | oklch(0.55 0.01 255) |
| `outlineVariant` | oklch(0.32 0.01 255) |

### Color — Light
Mesma fórmula com lightness invertida — ver `makeTheme()` em `Movie App v2.html`.

### Typography — Material 3 Type Scale (Roboto Flex)

| Token | Size / Line / Weight / Tracking |
|---|---|
| `headlineSmall` | 24sp / 32sp / 400 / 0 |
| `titleLarge` | 22sp / 28sp / 400 / 0 |
| `titleMedium` | 16sp / 24sp / 500 / 0.15 |
| `titleSmall` | 14sp / 20sp / 500 / 0.1 |
| `bodyLarge` | 16sp / 24sp / 400 / 0.5 |
| `bodyMedium` | 14sp / 20sp / 400 / 0.25 |
| `bodySmall` | 12sp / 16sp / 400 / 0.4 |
| `labelLarge` | 14sp / 20sp / 500 / 0.1 |
| `labelMedium` | 12sp / 16sp / 500 / 0.5 |

### Shape

| Token | Radius |
|---|---|
| small | 8dp (filter chips, assist chips) |
| medium | 12dp (cards) |
| large | 16dp (FAB) |
| extraLarge | 28dp (full-width CTAs) |
| full | 50% (icon buttons, avatars) |

### Motion

- **Emphasized easing:** `cubic-bezier(0.2, 0, 0, 1)` — para transições standard
- **Spring (favorite anim):** `cubic-bezier(0.34, 1.56, 0.64, 1)`, duração 280ms
- **Screen transition:** fade + translateX 24px, 220ms
- **Shimmer loading:** 1.6s linear infinite

### Spacing

8dp base grid: 4, 8, 12, 16, 24, 32, 48, 64.

---

## Componentes (inventário M3)

| Componente Compose M3 | Uso |
|---|---|
| `TopAppBar` (small) | Home + Detail |
| `FilterChip` (com `leadingIcon` check) | Popular / Favoritos |
| `AssistChip` | Gêneros no Detail |
| `Card` (filled) | MovieCard |
| `FloatingActionButton` | Shuffle na Home |
| `FilledIconButton` / `IconButton` (48dp) | Back, favoritar, search |
| `Button` (filled + outlined) | CTA no Detail |
| `LinearProgressIndicator` (não usado aqui, mas padrão) | — |

Para o shimmer, usar [accompanist placeholder](https://google.github.io/accompanist/placeholder/)
ou a versão Compose nativa (`Modifier.placeholder` do Material 3).

---

## Acessibilidade

- **Touch targets ≥ 48dp** em todos os `IconButton` (spec do plano).
- **Content descriptions** em todos os ícones via `contentDescription` param.
- **Dynamic type** — usar `sp` em todas as tipografias (não `dp`).
- **Contraste AA** — paleta M3 já garante quando usada como tokens.
- **FilterChip selecionado** deve ter `selectedIcon = Icons.Default.Check` (já é o default M3).
- **aria-pressed equivalente:** `Modifier.semantics { toggleableState = ... }` nos botões de favoritar.

---

## Dados (TMDB)

- `GET /movie/popular?page=N` → grid paginado
- `GET /search/movie?query=X&page=N` → busca (online-only, cache de entidades)
- `GET /movie/{id}` → detalhe (stale-while-revalidate)

Poster URLs: `https://image.tmdb.org/t/p/w500/{poster_path}`.

Ver plano multi-módulo — Room como SSOT, Paging 3 `RemoteMediator`, favoritos
em tabela separada `favorite_movies`.

---

## Arquivos de referência neste bundle

- `Movie App v2.html` — protótipo completo das 2 telas + estados de erro
- `android-frame.jsx`, `tweaks-panel.jsx` — componentes de preview (**não portar**)

Para explorar os estados no protótipo, abra o painel de Tweaks:
- Dark / Light toggle
- Primary hue slider (Material You seed)
- Offline banner toggle
- FAB toggle
- Tela de erro (none / network / generic / timeout / server)

---

## O que NÃO está no design (confirmar antes de implementar)

- Transições compartilhadas (shared element) entre Home → Detail — ver
  `SharedTransitionLayout` (Compose 1.7+).
- Swipe-to-refresh na Home — não mockei; plano não menciona, confirmar com PM.
- Haptic feedback no favoritar — sugerido, não obrigatório.
- Tema claro (light) — mockei no protótipo via Tweaks, mas priorize dark para o desafio.
