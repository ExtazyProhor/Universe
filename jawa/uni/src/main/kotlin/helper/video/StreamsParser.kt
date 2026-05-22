package ru.prohor.universe.uni.cli.helper.video

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.ajalt.mordant.rendering.TextColors.green
import com.github.ajalt.mordant.rendering.TextColors.yellow
import tools.jackson.module.kotlin.jacksonObjectMapper
import tools.jackson.module.kotlin.readValue

class StreamsParser {
    private val knownCodecTypes = setOf("video", "audio", "subtitle")
    private val objectMapper = jacksonObjectMapper()

    fun parse(json: String): StreamsInfo {
        val streams = objectMapper.readValue<FFprobeResult>(json).streams
        return StreamsInfo(
            videos = parseVideo(streams),
            audios = parseAudio(streams),
            subtitles = parseSubtitles(streams),
            unknownStreams = parseUnknown(streams)
        )
    }

    private fun parseVideo(streams: List<Stream>): List<VideoInfo> {
        return streams
            .filter { it.codecType == "video" }
            .map {
                VideoInfo(
                    resolution = if (it.width != null && it.height != null) "${it.width}x${it.height}" else null,
                    duration = it.tags?.duration,
                    codec = it.codecName,
                )
            }
    }

    private fun parseAudio(streams: List<Stream>): List<AudioInfo> {
        return streams
            .filter { it.codecType == "audio" }
            .map {
                AudioInfo(
                    lang = it.tags?.language,
                    title = it.tags?.title,
                    bitrate = it.bitRate?.toIntOrNull(),
                    codec = it.codecName
                )
            }
    }

    private fun parseSubtitles(streams: List<Stream>): List<SubtitleInfo> {
        fun buildFlags(d: Disposition?): List<String> {
            if (d == null) return listOf()
            val flags = mutableListOf<String>()
            if (d.default == 1) flags += "default"
            if (d.forced == 1) flags += "forced"
            if (d.hearingImpaired == 1) flags += "SDH"
            return flags
        }

        return streams.filter { it.codecType == "subtitle" }
            .map {
                SubtitleInfo(
                    lang = it.tags?.language,
                    title = it.tags?.title,
                    flags = buildFlags(it.disposition),
                    codec = it.codecName
                )
            }
    }

    private fun parseUnknown(streams: List<Stream>): List<UnknownStream> {
        return streams.filterNot { knownCodecTypes.contains(it.codecType) }
            .map {
                UnknownStream(
                    codecType = it.codecType,
                    codecName = it.codecName
                )
            }
    }
}

data class FFprobeResult(
    val streams: List<Stream>
)

data class Stream(
    val index: Int,
    @field:JsonProperty("codec_name")
    val codecName: String?,
    @field:JsonProperty("codec_type")
    val codecType: String?,
    val width: Int?,
    val height: Int?,
    @field:JsonProperty("bit_rate")
    val bitRate: String?,
    val tags: Tags?,
    val disposition: Disposition?
)

data class Tags(
    val language: String?,
    val title: String?,
    @field:JsonProperty("DURATION")
    val duration: String?
)

data class Disposition(
    val default: Int?,
    val forced: Int?,
    @field:JsonProperty("hearing_impaired")
    val hearingImpaired: Int?
)

data class StreamsInfo(
    val videos: List<VideoInfo>,
    val audios: List<AudioInfo>,
    val subtitles: List<SubtitleInfo>,
    val unknownStreams: List<UnknownStream>
)

data class VideoInfo(
    val resolution: String?,
    val duration: String?,
    val codec: String?,
) {
    override fun toString(): String {
        return listOfNotNull(
            resolution?.let { yellow("resolution: ") + green(it) },
            duration?.let { yellow("duration: ") + green(it) },
            codec?.let { yellow("codec: ") + green(it) }
        ).joinToString()
    }
}

data class AudioInfo(
    val lang: String?,
    val title: String?,
    val bitrate: Int?,
    val codec: String?
) {
    override fun toString(): String {
        val bitrate = this.bitrate?.div(1000)?.toString()?.plus(" kbps")
        return listOfNotNull(
            title?.let { yellow("title: ") + green(it) },
            lang?.let { yellow("lang: ") + green(it) },
            bitrate?.let { yellow("bitrate: ") + green(it) },
            codec?.let { yellow("codec: ") + green(it) }
        ).joinToString()
    }
}

data class SubtitleInfo(
    val lang: String?,
    val title: String?,
    val flags: List<String>,
    val codec: String?
) {
    override fun toString(): String {
        val flags = if (this.flags.isEmpty()) null else this.flags.joinToString(prefix = "(", postfix = ")")
        return listOfNotNull(
            title?.let { yellow("title: ") + green(it) },
            lang?.let { yellow("lang: ") + green(it) },
            flags?.let { yellow("flags: ") + green(it) },
            codec?.let { yellow("codec: ") + green(it) }
        ).joinToString()
    }
}

data class UnknownStream(
    val codecType: String?,
    val codecName: String?
) {
    override fun toString() = yellow("${codecType ?: "unknown"} codec - ${codecName ?: "unknown name"}")
}
