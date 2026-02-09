package ru.prohor.universe.padawan.scripts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FFMpegVideoRecoding {
    private static final String DOWNLOADS = "C:\\Users\\User\\Downloads";
    private static final Path DOWNLOADS_PATH = Path.of(DOWNLOADS);

    public static void main(String[] args) throws Exception {
        renameAndCut("my", "video");
    }

    private static Float parseResult(String line) {
        try {
            line = line.substring(line.indexOf("time=") + "time=".length());
            line = line.substring(0, line.indexOf(' '));
            String[] parts = line.split(":");
            return Integer.parseInt(parts[0]) * 60 * 60 + Integer.parseInt(parts[1]) * 60 + Float.parseFloat(parts[2]);
        } catch (Exception e) {
            return null;
        }
    }

    private static Float parseDuration(String line) {
        float secs;
        try {
            line = line.substring(line.indexOf("Duration: ") + "Duration: ".length(), line.indexOf(','));
            String[] parts = line.split(":");
            secs = Integer.parseInt(parts[0]) * 60 * 60 + Integer.parseInt(parts[1]) * 60 + Float.parseFloat(parts[2]);
        } catch (Exception e) {
            return null;
        }
        return secs;
    }

    /**
     * <pre>
     * {@code
     * ffmpeg -i input.mkv \
     *     -map 0:v:0 -map 0:a:0 \
     *     -c:v libx264 -profile:v high -level 4.1 -pix_fmt yuv420p \
     *     -c:a aac -b:a 192k -ac 2 \
     *     -movflags +faststart \
     *     output.mp4
     * }
     * </pre>
     */
    private static void mkvToMp4(String input, String output) throws IOException {
        String[] cmd = {
                "ffmpeg", "-i", input, "-map", "0:v:0", "-map", "0:a:0",
                "-c:v", "libx264", "-profile:v", "high", "-level", "4.1", "-pix_fmt", "yuv420p",
                "-c:a", "aac", "-b:a", "192k", "-ac", "2",
                "-movflags", "+faststart",
                output
        };
        ProcessBuilder pb = new ProcessBuilder(cmd).redirectErrorStream(true);
        pb.start();
    }

    private static void recodeVideos() throws IOException {
        File sourceDir = DOWNLOADS_PATH.resolve("source").toFile();
        File destinationDir = DOWNLOADS_PATH.resolve("destination").toFile();
        File[] files = sourceDir.listFiles();
        if (files == null)
            return;

        float total = 0;
        float already = 0;

        external:
        for (File video : files) {
            String[] cmd = {"ffmpeg", "-i", video.getAbsolutePath()};
            Float duration = null;

            ProcessBuilder pb = new ProcessBuilder(cmd).redirectErrorStream(true);
            Process process = pb.start();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while (true) {
                    String line = in.readLine();
                    if (line == null)
                        break;
                    if (line.contains("Duration: "))
                        duration = parseDuration(line);
                    if (duration != null) {
                        total += duration;
                        continue external;
                    }
                }
            }
        }

        int totalVideos = files.length;
        int current = 0;

        for (File video : files) {
            current++;
            File out = new File(destinationDir, video.getName().replace(".MOV", ".mp4"));
            String[] cmd = {
                    "ffmpeg", "-i", video.getAbsolutePath(), "-c:v", "libx264", "-pix_fmt", "yuv420p", "-profile:v",
                    "high", "-preset", "fast", "-crf", "23", "-c:a", "aac", "-b:a", "192k", out.getAbsolutePath()
            };
            Float duration = null;

            ProcessBuilder pb = new ProcessBuilder(cmd).redirectErrorStream(true);
            Process process = pb.start();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while (true) {
                    String line = in.readLine();
                    if (line == null)
                        break;
                    if (duration == null && line.contains("Duration: "))
                        duration = parseDuration(line);
                    if (duration != null && line.contains("time=")) {
                        Float result = parseResult(line);
                        if (result != null) {
                            String s = String.valueOf(result / duration * 100);
                            String ss = String.valueOf((already + result) / total * 100);
                            if (s.length() > 5)
                                s = s.substring(0, 5);
                            if (ss.length() > 5)
                                ss = ss.substring(0, 5);
                            System.out.println(current + "/" + totalVideos + "\t" + s + " %" + "\t" + ss + " %");
                        }
                    }
                }
            }

            already += duration == null ? 0 : duration;
        }
    }

    private static void generateQuadraticEquations() {
        List<QuadraticEquation> D_EQ_0 = new ArrayList<>();
        List<QuadraticEquation> D_NEGATIVE = new ArrayList<>();
        List<QuadraticEquation> D_POSITIVE_RATIONAL = new ArrayList<>();
        List<QuadraticEquation> D_POSITIVE_IRRATIONAL = new ArrayList<>();

        List<QuadraticEquation> INCOMPLETE_ONLY_B = new ArrayList<>();
        List<QuadraticEquation> INCOMPLETE_ONLY_C_NO_ANSWER = new ArrayList<>();
        List<QuadraticEquation> INCOMPLETE_ONLY_C_RATIONAL_ANSWER = new ArrayList<>();
        List<QuadraticEquation> INCOMPLETE_ONLY_C_IRRATIONAL_ANSWER = new ArrayList<>();

        int limit = 9;

        for (int i = -limit; i <= limit; ++i) {
            if (i == 0)
                continue;
            for (int j = -limit; j <= limit; ++j) {
                for (int k = -limit; k <= limit; ++k) {
                    var equation = new QuadraticEquation(i, j, k);
                    if (j == 0) {
                        if (k == 0)
                            continue;
                        if (i * k < 0)
                            if (equation.hasIntegerRoot())
                                INCOMPLETE_ONLY_C_RATIONAL_ANSWER.add(equation);
                            else
                                INCOMPLETE_ONLY_C_IRRATIONAL_ANSWER.add(equation);
                        else
                            INCOMPLETE_ONLY_C_NO_ANSWER.add(equation);
                        continue;
                    }
                    if (k == 0) {
                        INCOMPLETE_ONLY_B.add(equation);
                        continue;
                    }
                    if (equation.discriminant < 0)
                        D_NEGATIVE.add(equation);
                    else if (equation.discriminant == 0)
                        D_EQ_0.add(equation);
                    else {
                        if (equation.hasIntegerRoot())
                            D_POSITIVE_RATIONAL.add(equation);
                        else
                            D_POSITIVE_IRRATIONAL.add(equation);
                    }
                }
            }
        }
        printEquations(INCOMPLETE_ONLY_B, "Неполные (без C)");
        printEquations(INCOMPLETE_ONLY_C_RATIONAL_ANSWER, "Неполные (без B), рациональный ответ");
        printEquations(INCOMPLETE_ONLY_C_IRRATIONAL_ANSWER, "Неполные (без B), иррациональный ответ");
        printEquations(INCOMPLETE_ONLY_C_NO_ANSWER, "Неполные (без B), нет ответа");
        printEquations(D_POSITIVE_RATIONAL, "Два рациональных корня");
        printEquations(D_POSITIVE_IRRATIONAL, "Два иррациональных корня");
        printEquations(D_EQ_0, "Один корень");
        printEquations(D_NEGATIVE, "Нет корней");
    }

    private static final boolean ONLY_SIZE = false;

    static void printEquations(List<QuadraticEquation> list, String header) {
        if (ONLY_SIZE) {
            System.out.println(header + " " + list.size());
            return;
        }
        System.out.println("# " + header);
        Collections.shuffle(list);
        if (list.size() > 150)
            list = list.subList(0, 150);
        list.forEach(x -> System.out.println(x.toMarkdown()));
    }

    private static class QuadraticEquation {
        private final int a, b, c, discriminant;

        public QuadraticEquation(int a, int b, int c) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.discriminant = b * b - 4 * a * c;
        }

        public boolean hasIntegerRoot() {
            var sqrt = Math.round(Math.sqrt(discriminant));
            return sqrt * sqrt == discriminant;
        }

        public String toMarkdown() {
            StringBuilder md = new StringBuilder("$");
            if (a == -1)
                md.append("-");
            else if (a != 1)
                md.append(a);
            md.append("x^2 ");
            if (b != 0) {
                if (b < 0) {
                    md.append("- ");
                    if (b != -1)
                        md.append(-b);
                } else {
                    md.append("+ ");
                    if (b != 1)
                        md.append(b);
                }
                md.append("x ");
            }
            if (c != 0) {
                if (c < 0)
                    md.append("- ").append(-c);
                else
                    md.append("+ ").append(c);
                md.append(" ");
            }
            return md.append("= 0$").toString();
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b, c);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!(obj instanceof QuadraticEquation q))
                return false;
            return this.a == q.a && this.b == q.b && this.c == q.c;
        }
    }

    private static void renameAndCut(String folder, String pre) throws IOException {
        Path folderPath = DOWNLOADS_PATH.resolve(folder);
        String begin = pre + "-";
        String end = ".mp4";
        String[] secs = Files.readString(Paths.get("output.txt")).split(" ");

        try (BufferedReader reader = new BufferedReader(new FileReader("input.txt"))) {
            for (int i = 0; i < 19; ++i) {
                String input = reader.readLine();
                int index = i + 700;
                System.out.println(run("ffmpeg", "-ss", secs[i], "-i", input, "-c", "copy", folderPath.resolve(begin + index + end).toAbsolutePath().toString()));
                // System.out.println(run("ffmpeg", "-i", input, "-ss", secs[i], "-c:v", "libx264", "-c:a", "aac", "-preset", "veryfast", "-crf", "23", begin + index + end));
                // System.out.println(run("ffmpeg", "-ss", secs[i], "-i", input, "-c:v", "libx264", "-preset", "fast", "-crf", "23", "-c:a", "aac", begin + index + end));
                System.out.println();
            }
        }
    }

    private static void recode() throws IOException {
        String begin = DOWNLOADS_PATH.resolve("pre-").toString();
        String end = ".mp4";

        try (BufferedReader reader = new BufferedReader(new FileReader("input.txt"))) {
            for (int i = 0; i < 7; ++i) {
                String input = reader.readLine();
                int index = i + 407;
                System.out.println(run(
                        "ffmpeg",
                        "-i",
                        input,
                        "-c:v",
                        "libx264",
                        "-c:a",
                        "aac",
                        "-strict",
                        "experimental",
                        "-preset",
                        "fast",
                        "-crf",
                        "23",
                        begin + index + end
                ));
                System.out.println();
            }
        }
    }

    private static String run(String... command) throws IOException {
        Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
        StringBuilder result = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null)
                result.append(line).append(System.lineSeparator());
        }
        return result.toString();
    }
}
