package ru.prohor.universe.padawan.scripts.images;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;
import ru.prohor.universe.jocasta.core.utils.FileSystemUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TelegramStickersAndEmoji {
    public static void main(String[] args) throws Exception {
        new TelegramMediaCreatorBuilder(
                FileSystemUtils.userDir().asPath().resolve("Downloads/image.png").toString(),
                FileSystemUtils.userDir().asPath().resolve("Downloads/image.webm").toString(),
                MediaType.EMOJI,
                true
        );
    }

    public static class TelegramMediaCreatorBuilder {
        private static final FFmpeg FFMPEG = Sneaky.execute(() -> new FFmpeg());
        private static final FFprobe FFPROBE = Sneaky.execute(() -> new FFprobe());
        private static final Set<String> VIDEO_EXTENSIONS = new HashSet<>(Set.of(
                "mp4",
                "mkv",
                "avi",
                "ts",
                "mpg",
                "flv",
                "wmv",
                "mov",
                "webm"
        ));
        private static final Set<String> IMAGE_EXTENSIONS = new HashSet<>(Set.of(
                "jpg",
                "jpeg",
                "png",
                "bmp",
                "gif",
                "tiff",
                "webp"
        ));
        private static final String WEBM = ".webm";

        private final String inputFile;
        private final String outputFile;
        private final MediaType mediaType;
        private final boolean cropToSquare;

        private final boolean isImage;

        public TelegramMediaCreatorBuilder(
                String inputFile,
                String outputFile,
                MediaType mediaType,
                boolean cropToSquare
        ) {
            int index = inputFile.lastIndexOf('.');
            if (index == -1)
                throw new IllegalArgumentException("input file must have extension");
            String extension = inputFile.substring(index + 1);
            if (VIDEO_EXTENSIONS.contains(extension))
                isImage = false;
            else if (IMAGE_EXTENSIONS.contains(extension))
                isImage = true;
            else
                throw new IllegalArgumentException("illegal input file extension");
            if (!outputFile.endsWith(WEBM))
                throw new IllegalArgumentException("output file must ends with .webm extension");
            this.inputFile = inputFile;
            this.outputFile = outputFile;
            this.mediaType = mediaType;
            this.cropToSquare = cropToSquare;
        }

        public void create() {
            FFmpegProbeResult probeResult = Sneaky.execute(() -> FFPROBE.probe(inputFile));
            FFmpegOutputBuilder builder = new FFmpegBuilder()
                    .setInput(probeResult)
                    .overrideOutputFiles(true)
                    .addOutput(outputFile)
                    .disableSubtitle()
                    .disableAudio()
                    .setVideoCodec("vp9")
                    .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL);

            if (isImage)
                builder = builder.setVideoFrameRate(1, 1).setDuration(1, TimeUnit.SECONDS);
            else
                builder = builder.setVideoFrameRate(30, 1);

            int resolution = mediaType.resolution;
            int size = mediaType.size;

            if (cropToSquare)
                builder.setVideoFilter(String.format("crop=%d:%d,scale=%d:%d", size, size, size, size));
            else {
                int originalWidth = probeResult.getStreams().getFirst().width;
                int originalHeight = probeResult.getStreams().getFirst().height;
                String scale = (originalWidth > originalHeight ?
                        "scale=" + resolution + ":-1" :
                        "scale=-1:" + resolution);
                builder = builder.addExtraArgs("-vf", scale);
            }
            builder = builder.setVideoResolution(resolution, resolution)
                    .setTargetSize(size * 800L)
                    .addExtraArgs("-bufsize", size + "k");

            FFmpegExecutor executor = new FFmpegExecutor(FFMPEG, FFPROBE);
            executor.createJob(builder.done()).run();
        }
    }

    public enum MediaType {
        STICKER(512, 256),
        EMOJI(100, 64),
        COVER(100, 32);

        private final int resolution;
        private final int size;

        MediaType(int resolution, int size) {
            this.resolution = resolution;
            this.size = size;
        }
    }
}
