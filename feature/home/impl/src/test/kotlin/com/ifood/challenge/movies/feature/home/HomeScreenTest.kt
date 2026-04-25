package com.ifood.challenge.movies.feature.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.ifood.challenge.movies.core.designsystem.component.FilterChipRowTestTags
import com.ifood.challenge.movies.core.network.BackdropSize
import com.ifood.challenge.movies.core.network.ImageUrlBuilder
import com.ifood.challenge.movies.core.network.PosterSize
import com.ifood.challenge.movies.domain.movies.model.Genre
import com.ifood.challenge.movies.domain.movies.model.Movie
import com.ifood.challenge.movies.feature.home.internal.HomeFilter
import com.ifood.challenge.movies.feature.home.internal.HomeScreen
import com.ifood.challenge.movies.feature.home.internal.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val pagingFlow = MutableStateFlow(PagingData.empty<Movie>())
    private val fakeImageUrlBuilder = object : ImageUrlBuilder {
        override fun poster(path: String?, size: PosterSize) = ""
        override fun backdrop(path: String?, size: BackdropSize) = ""
    }

    private fun setContent(
        uiState: HomeUiState = HomeUiState(),
        onMovieClick: (Int) -> Unit = {},
        onFilterSelect: (HomeFilter) -> Unit = {},
        onSearchQueryChange: (String) -> Unit = {},
        onSearchToggle: () -> Unit = {},
        onFavoriteToggle: (Movie) -> Unit = {},
        onShuffle: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            val movies = pagingFlow.collectAsLazyPagingItems()
            HomeScreen(
                uiState = uiState,
                movies = movies,
                onMovieClick = onMovieClick,
                onFilterSelect = onFilterSelect,
                onSearchQueryChange = onSearchQueryChange,
                onSearchToggle = onSearchToggle,
                onFavoriteToggle = onFavoriteToggle,
                onShuffle = onShuffle,
                imageUrlBuilder = fakeImageUrlBuilder,
            )
        }
    }

    @Test
    fun title_isDisplayed() {
        setContent()
        composeTestRule.onNodeWithText("Filmes").assertIsDisplayed()
    }

    @Test
    fun searchIcon_isDisplayed() {
        setContent()
        composeTestRule.onNodeWithContentDescription("Buscar").assertIsDisplayed()
    }

    @Test
    fun filterChips_showPopularAndFavoritos() {
        setContent(uiState = HomeUiState(genres = TEST_GENRES))

        composeTestRule.onNodeWithText("Popular").assertIsDisplayed()
        composeTestRule.onNodeWithText("Favoritos").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mais Recentes").assertIsDisplayed()
    }

    @Test
    fun filterChips_showFavoritosCount_whenHasFavorites() {
        setContent(uiState = HomeUiState(favoriteIds = setOf(1, 2)))
        composeTestRule.onNodeWithText("Favoritos · 2").assertIsDisplayed()
    }

    @Test
    fun filterChips_showGenresFromApi() {
        setContent(uiState = HomeUiState(genres = TEST_GENRES))
        composeTestRule.onNodeWithText("Ação").assertIsDisplayed()
        composeTestRule.onNodeWithText("Aventura").assertIsDisplayed()
    }

    @Test
    fun filterChipRow_isVisible() {
        setContent(uiState = HomeUiState(genres = TEST_GENRES))
        composeTestRule.onNodeWithTag(FilterChipRowTestTags.root).assertIsDisplayed()
    }

    @Test
    fun searchBar_hidden_whenSearchNotActive() {
        setContent(uiState = HomeUiState(isSearchActive = false))
        composeTestRule.onNodeWithTag("home_search_bar").assertDoesNotExist()
    }

    @Test
    fun searchBar_visible_whenSearchActive() {
        setContent(uiState = HomeUiState(isSearchActive = true))
        composeTestRule.onNodeWithTag("home_search_bar").assertIsDisplayed()
    }

    @Test
    fun searchToggle_callsCallback() {
        var toggled = false
        setContent(onSearchToggle = { toggled = true })
        composeTestRule.onNodeWithContentDescription("Buscar").performClick()
        assertTrue(toggled)
    }

    @Test
    fun favoritesEmpty_showsEmptyState() {
        setContent(
            uiState = HomeUiState(
                filter = HomeFilter.Favorites,
                favoriteMovies = emptyList(),
            ),
        )
        composeTestRule.onNodeWithText("Nenhum favorito ainda").assertIsDisplayed()
    }

    @Test
    fun favoritesWithMovies_showsMovieCards() {
        setContent(
            uiState = HomeUiState(
                filter = HomeFilter.Favorites,
                favoriteMovies = listOf(TEST_MOVIE),
                favoriteIds = setOf(TEST_MOVIE.id),
            ),
        )
        composeTestRule.onNodeWithText("Inception").assertIsDisplayed()
    }

    @Test
    fun fab_visible_whenHasFavorites() {
        setContent(uiState = HomeUiState(favoriteIds = setOf(1)))
        composeTestRule.onNodeWithContentDescription("Filme aleatório dos favoritos").assertIsDisplayed()
    }

    @Test
    fun fab_hidden_whenNoFavorites() {
        setContent(uiState = HomeUiState(favoriteIds = emptySet()))
        composeTestRule.onNodeWithContentDescription("Filme aleatório dos favoritos").assertDoesNotExist()
    }

    @Test
    fun fab_click_callsShuffle() {
        var shuffled = false
        setContent(
            uiState = HomeUiState(favoriteIds = setOf(1)),
            onShuffle = { shuffled = true },
        )
        composeTestRule.onNodeWithContentDescription("Filme aleatório dos favoritos").performClick()
        assertTrue(shuffled)
    }

    @Test
    fun filterChip_click_callsOnFilterSelect() {
        var selectedFilter: HomeFilter? = null
        setContent(
            uiState = HomeUiState(genres = TEST_GENRES),
            onFilterSelect = { selectedFilter = it },
        )
        composeTestRule.onNodeWithText("Favoritos").performClick()
        assertEquals(HomeFilter.Favorites, selectedFilter)
    }

    companion object {
        private val TEST_GENRES = listOf(
            Genre(28, "Ação"),
            Genre(12, "Aventura"),
        )

        private val TEST_MOVIE = Movie(
            id = 1,
            title = "Inception",
            posterPath = "/poster.jpg",
            backdropPath = null,
            overview = "A thief",
            voteAverage = 8.8,
            releaseDate = "2010-07-16",
            popularity = 100.0,
        )
    }
}
