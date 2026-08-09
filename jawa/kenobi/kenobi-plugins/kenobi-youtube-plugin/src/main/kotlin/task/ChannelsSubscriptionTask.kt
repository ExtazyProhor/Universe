package ru.prohor.universe.kenobi.plugin.youtube.task

import org.springframework.stereotype.Service
import ru.prohor.universe.jocasta.core.features.fieldref.FR
import ru.prohor.universe.jocasta.morphia.MongoRepository
import ru.prohor.universe.jocasta.morphia.filter.MongoFilter
import ru.prohor.universe.jocasta.morphia.filter.MongoFilters
import ru.prohor.universe.kenobi.core.Task
import ru.prohor.universe.kenobi.plugin.youtube.api.ChannelInfoProvider
import ru.prohor.universe.kenobi.plugin.youtube.api.LatestChannelVideosProvider
import ru.prohor.universe.kenobi.plugin.youtube.api.NewVideosHandler
import ru.prohor.universe.kenobi.plugin.youtube.model.Subscriber

@Service
class ChannelsSubscriptionTask(
    private val subscribersRepository: MongoRepository<Subscriber>,
    private val channelInfoProvider: ChannelInfoProvider,
    private val latestChannelVideosProvider: LatestChannelVideosProvider,
    private val newVideosHandler: NewVideosHandler,
) : Task {
    override fun execute() {
        // TODO нормально сделать
        // TODO сделать чтобы одинаковые каналы не вытягивались по несколько раз
        val subscribers = subscribersRepository.find(enabledFilter)
        val updated = mutableListOf<Subscriber>()

        subscribers.forEach { subscriber ->
            var changed = false
            val channels = subscriber.channels.map { channel ->
                if (channel.name == null || channel.channelId == null) {
                    changed = true
                    channelInfoProvider.getChannelInfo(channel.link)
                } else {
                    channel
                }
            }

            val presentVideoIds = subscriber.processedVideos.map { it.videoId }.toHashSet()
            val latestVideos = channels.flatMap { channel ->
                val id = channel.channelId
                if (id == null) {
                    System.err.println("channelId of channel '${channel.link}' is null")
                    emptyList()
                } else {
                    latestChannelVideosProvider.getLatestChannelVideos(id)
                }
            }

            val newVideos = latestVideos.filter { !presentVideoIds.contains(it.videoId) }
            if (newVideos.isNotEmpty()) {
                changed = true
                newVideosHandler.onNewVideos(subscriber, newVideos)
            }

            if (changed) {
                val videos = subscriber.processedVideos + newVideos
                updated.add(
                    subscriber.copy(
                        channels = channels,
                        processedVideos = videos
                    )
                )
            }
        }

        if (updated.isNotEmpty()) {
            subscribersRepository.save(updated)
        }
    }

    companion object {
        private val enabledFilter: MongoFilter<Subscriber> = MongoFilters.eq(FR.wrap { it.enabled }, true)
    }
}
