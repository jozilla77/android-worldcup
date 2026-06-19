package com.jozilla.worldcup.model

data class Player(
    val name: String,
    val number: Int,
    val position: String,
    val rating: Double,
    val x: Double, // Percentage 0-100 on tactical field
    val y: Double, // Percentage 0-100 on tactical field
    val avatar: String = ""
)

data class Goal(
    val scorer: String,
    val minute: Int,
    val team: String // "home" or "away"
)

data class Card(
    val player: String,
    val minute: Int,
    val type: String, // "yellow" or "red"
    val team: String // "home" or "away"
)

data class Match(
    val id: String,
    val teamHome: String,
    val teamAway: String,
    val flagHome: String,
    val flagAway: String,
    val kickoffUtc: String,
    val originalTime: String,
    var status: String, // "UPCOMING", "LIVE", "COMPLETED"
    var scoreHome: Int,
    var scoreAway: Int,
    var playTime: Int,
    var halfTime: Boolean,
    val rosterHome: List<Player> = emptyList(),
    val rosterAway: List<Player> = emptyList(),
    var goals: List<Goal> = emptyList(),
    var cards: List<Card> = emptyList()
)

data class PlayerStats(
    val name: String,
    val number: Int,
    val position: String,
    var minutesPlayed: Int = 0,
    var goals: Int = 0,
    var assists: Int = 0,
    var yellowCards: Int = 0,
    var redCards: Int = 0
)

data class Country(
    val name: String,
    val flag: String,
    val group: String,
    val rank: Int = 0,
    var played: Int = 0,
    var wins: Int = 0,
    var draws: Int = 0,
    var losses: Int = 0,
    var points: Int = 0,
    val roster: List<PlayerStats> = emptyList()
)
