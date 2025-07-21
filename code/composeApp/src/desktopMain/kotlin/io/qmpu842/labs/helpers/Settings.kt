package io.qmpu842.labs.helpers

data class Settings(
    val isAutoPlayActive: Boolean = true,
    val isAutoAutoPlayActive: Boolean = true,
) {
    fun toggleAutoPlay() = this.copy(isAutoPlayActive = !isAutoPlayActive)

    fun toggleAutoAutoPlay() = this.copy(isAutoAutoPlayActive = !isAutoAutoPlayActive)
}
