package com.nothinggesture.launcher.ui.theme

object AccentPresets {

    data class Shade(val name: String, val color: Long)
    data class Family(val name: String, val shades: List<Shade>)

    val Red = Family(
        name = "Red",
        shades = listOf(
            Shade("Nothing",   0xFFFF0022),
            Shade("Crimson",   0xFFE10600),
            Shade("Scarlet",   0xFFFF1744),
            Shade("Ruby",      0xFFD50000),
            Shade("Coral",     0xFFFF5252)
        )
    )

    val Blue = Family(
        name = "Blue",
        shades = listOf(
            Shade("Electric",  0xFF2979FF),
            Shade("Azure",     0xFF00B0FF),
            Shade("Cyan",      0xFF00E5FF),
            Shade("Indigo",    0xFF3D5AFE),
            Shade("Ocean",     0xFF0091EA)
        )
    )

    val Yellow = Family(
        name = "Yellow",
        shades = listOf(
            Shade("Amber",     0xFFFFD600),
            Shade("Gold",      0xFFFFC400),
            Shade("Lemon",     0xFFFFEA00),
            Shade("Mustard",   0xFFFFAB00),
            Shade("Sun",       0xFFFFE57F)
        )
    )

    val Green = Family(
        name = "Green",
        shades = listOf(
            Shade("Lime",      0xFF76FF03),
            Shade("Emerald",   0xFF00E676),
            Shade("Mint",      0xFF69F0AE),
            Shade("Forest",    0xFF00C853),
            Shade("Neon",      0xFFB2FF59)
        )
    )

    val Purple = Family(
        name = "Purple",
        shades = listOf(
            Shade("Violet",    0xFFE040FB),
            Shade("Orchid",    0xFFD500F9),
            Shade("Lavender",  0xFFEA80FC),
            Shade("Deep",      0xFFAA00FF),
            Shade("Grape",     0xFF7C4DFF)
        )
    )

    val Orange = Family(
        name = "Orange",
        shades = listOf(
            Shade("Tangerine", 0xFFFF6D00),
            Shade("Flame",     0xFFFF3D00),
            Shade("Peach",     0xFFFF9100),
            Shade("Amberglow", 0xFFFFAB40),
            Shade("Sunset",    0xFFFF6E40)
        )
    )

    val Neutral = Family(
        name = "Neutral",
        shades = listOf(
            Shade("White",     0xFFFFFFFF),
            Shade("Silver",    0xFFE0E0E0),
            Shade("Grey",      0xFFBDBDBD),
            Shade("Graphite",  0xFF9E9E9E),
            Shade("Smoke",     0xFF757575)
        )
    )

    val allFamilies = listOf(Red, Blue, Yellow, Green, Purple, Orange, Neutral)

    fun familyOf(color: Long): Family {
        return allFamilies.firstOrNull { family ->
            family.shades.any { it.color == color }
        } ?: Red
    }
}
