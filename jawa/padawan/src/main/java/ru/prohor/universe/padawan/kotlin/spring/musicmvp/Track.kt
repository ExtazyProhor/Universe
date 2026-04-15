package ru.prohor.universe.padawan.kotlin.spring.musicmvp

import com.fasterxml.jackson.annotation.JsonProperty

data class RawMetadata(
    val duration: String?, // Double
    val size: String?, // Int

    val title: String?,
    val artist: String?,
    val album: String?,
    val lyrics: String?,
    val genre: String?,
    val date: String?
)

data class Cover(
    val file: String,
    val width: Int,
    val height: Int,
)

data class Lyrics(
    val lyrics: String?,
    val attempt: String?
)

data class Track(
    val name: String,
    val authors: List<String>,
    val track: String,
    val duration: Double,

    val cover: Cover?,
    val meta: RawMetadata,
    val lyrics: Lyrics
)

data class RawMetadataV2(
    val format: RawMetadataFormatV2
)

data class RawMetadataFormatV2(
    val duration: String,
    val size: String,
    val tags: RawMetadataTagsV2?
)

data class RawMetadataTagsV2(
    val title: String?,
    val artist: String?,
    val album: String?,
    @field:JsonProperty("lyrics-vknext.net-eng")
    val vkNextLyrics: String?,
    @field:JsonProperty("lyrics-XXX")
    val lyricsFinderLyrics: String?,
    val genre: String?,
    val date: String?
)
