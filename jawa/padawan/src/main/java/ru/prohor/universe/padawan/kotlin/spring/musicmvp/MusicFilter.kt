package ru.prohor.universe.padawan.kotlin.spring.musicmvp

fun main() {

}

fun stats(name: String, list: List<Track>) {
    val duration = list.sumOf { it.duration } / 60
    val hours = (duration / 60).toInt()
    val minutes = (duration - hours * 60).toInt()
    println("$name: ${list.size} шт, $hours ч $minutes мин")
}

fun MutableList<Track>.fullTitle(artist: String, titles: Set<String>, target: MutableList<Track>): MutableList<Track> {
    return filter(target, "fullTitle '$artist' - $titles") { it.authors.contains(artist) && titles.contains(it.name) }
}

fun MutableList<Track>.artistAndSubstring(artist: String, str: String, target: MutableList<Track>): MutableList<Track> {
    return filter(target, "artistAndSubstring '$artist' + '$str'") {
        it.authors.contains(artist) && it.name.contains(str)
    }
}

fun MutableList<Track>.artist(artist: String, target: MutableList<Track>): MutableList<Track> {
    return filter(target, "artist '$artist'") { it.authors.contains(artist) }
}

fun MutableList<Track>.filter(
    target: MutableList<Track>,
    filterName: String,
    predicate: (Track) -> Boolean
): MutableList<Track> {
    var count = 0;
    val iterator = iterator()
    while (iterator.hasNext()) {
        val item = iterator.next()
        if (predicate.invoke(item)) {
            count++
            target.add(item)
            iterator.remove()
        }
    }
    if (count == 0) throw RuntimeException("0: $filterName")
    return this
}

fun printArtistTracks(tracks: List<Track>, artist: String) {
    println("### $artist ###")
    tracks.filter { it.authors.contains(artist) }.forEach { println(it.name) }
}

fun printArtists(tracks: List<Track>) {
    tracks.flatMap { it.authors }
        .groupingBy { it }
        .eachCount()
        .toList()
        .sortedByDescending { it.second }
        .forEach { println("${it.first} - ${it.second}") }
}
