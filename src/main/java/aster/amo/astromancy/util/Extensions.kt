package aster.amo.astromancy.util

fun Enum<*>.humanize(): String {
    return this.name
        .replace('_', ' ') // Replace underscores with spaces
        .split(" ") // Split into words
        .joinToString(" ") { it.lowercase().replaceFirstChar { char -> char.uppercase() } } // Capitalize each word
}