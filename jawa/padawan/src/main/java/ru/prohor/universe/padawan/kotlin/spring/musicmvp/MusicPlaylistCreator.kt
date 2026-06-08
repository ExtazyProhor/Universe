package ru.prohor.universe.padawan.kotlin.spring.musicmvp

fun main() {

}

fun makePlaylist2(genres: List<List<Track>>): List<Track> {
    return balanceAuthors(genres.flatten())
}

fun makePlaylist(genres: List<List<Track>>): List<Track> {
    return balanceGroups(genres.map { balanceAuthors(it) })
}

private fun balanceAuthors(tracks: List<Track>): List<Track> {
    val counts = tracks.flatMap { it.authors }.groupingBy { it }.eachCount()
    val byAuthor = tracks
        .groupBy { track -> track.authors.maxBy { counts[it] ?: 0 } }
        .values
        .map { it.shuffled().toMutableList() }
    return balanceGroups(byAuthor)
}

private fun <T> balanceGroups(groups: List<List<T>>): List<T> {
    val queues = groups
        .shuffled()
        .filter { it.isNotEmpty() }
        .map { it.toMutableList() }

    val initialSizes = queues.map { it.size }
    val result = ArrayList<T>(initialSizes.sum())

    while (queues.any { it.isNotEmpty() }) {
        var bestIndex = -1
        var bestScore = Double.NEGATIVE_INFINITY

        for (i in queues.indices) {
            val queue = queues[i]
            if (queue.isEmpty()) continue

            val score = queue.size.toDouble() / initialSizes[i]
            if (score > bestScore || score == bestScore && initialSizes[i] > initialSizes[bestIndex]) {
                bestScore = score
                bestIndex = i
            }
        }
        result += queues[bestIndex].removeFirst()
    }
    return result
}
