package com.ifood.challenge.movies.core.common.result

sealed interface DomainResult<out T> {
    data class Success<T>(val data: T) : DomainResult<T>
    data class Failure(val error: Throwable) : DomainResult<Nothing>
}

inline fun <T, R> DomainResult<T>.map(transform: (T) -> R): DomainResult<R> =
    when (this) {
        is DomainResult.Success -> DomainResult.Success(transform(data))
        is DomainResult.Failure -> this
    }

inline fun <T> DomainResult<T>.onSuccess(block: (T) -> Unit): DomainResult<T> {
    if (this is DomainResult.Success) block(data)
    return this
}

inline fun <T> DomainResult<T>.onFailure(block: (Throwable) -> Unit): DomainResult<T> {
    if (this is DomainResult.Failure) block(error)
    return this
}

fun <T> T.asSuccess(): DomainResult<T> = DomainResult.Success(this)

fun Throwable.asFailure(): DomainResult<Nothing> = DomainResult.Failure(this)
