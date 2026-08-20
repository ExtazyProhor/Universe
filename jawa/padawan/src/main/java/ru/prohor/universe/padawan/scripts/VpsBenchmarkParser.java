package ru.prohor.universe.padawan.scripts;

import ru.prohor.universe.jocasta.core.utils.FileSystemUtils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Для запуска бенчмарка на новом VPS:
 *
 * <pre>{@code
 * sudo -i
 * apt update && apt install -y curl wget screen
 * screen -S benchmark
 *
 * echo "=== START ===" > vps_test_results.txt && echo "=== Bench.sh ===" >> vps_test_results.txt && wget -qO- bench.sh | bash >> vps_test_results.txt && echo "=== YABS ===" >> vps_test_results.txt && curl -sL yabs.sh | bash >> vps_test_results.txt && echo "=== END ===" >> vps_test_results.txt
 * cat vps_test_results.txt
 * }</pre>
 */
public class VpsBenchmarkParser {
    static class Run {
        String name;
        String provider;

        String cpuModel;
        int cores;
        double cpuMHz;

        double ramGB;
        double diskGB;
        double ioAverageMBs;

        String os;
        String kernel;
        String virtualization;
        String location;
        String region;
        String organization;

        double geekbenchSingle = Double.NaN;
        double geekbenchMulti = Double.NaN;

        Map<String, Speedtest> speedtests = new LinkedHashMap<>();
        Map<String, Iperf> iperf4 = new LinkedHashMap<>();
        Map<String, Iperf> iperf6 = new LinkedHashMap<>();
        Map<String, Fio> fio = new LinkedHashMap<>();
    }

    static class Speedtest {
        double upload = Double.NaN;
        double download = Double.NaN;
        double ping = Double.NaN;
    }

    static class Iperf {
        double send = Double.NaN;
        double recv = Double.NaN;
        double ping = Double.NaN;
    }

    static class Fio {
        double readMBs = Double.NaN;
        double writeMBs = Double.NaN;
        double totalMBs = Double.NaN;

        double readIOPS = Double.NaN;
        double writeIOPS = Double.NaN;
        double totalIOPS = Double.NaN;
    }

    static void main() throws Exception {
        Path benchmarkDir = FileSystemUtils.downloads().asPath().resolve("benchmark");
        Path input = benchmarkDir.resolve("benchmark-files");
        Path output = benchmarkDir.resolve("vps_benchmark_report.html");
        if (!Files.exists(input))
            throw new IllegalArgumentException("Папка не существует: " + input);

        List<Run> runs = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(input, "*.txt")) {
            for (Path file : stream) {
                runs.add(parse(file));
            }
        }

        if (runs.isEmpty()) {
            System.out.println("TXT-файлы с результатами не найдены.");
            return;
        }

        Comparator<Run> comparator = Comparator.comparing((Run r) -> r.provider.toLowerCase())
                .thenComparing(r -> r.name.toLowerCase());
        runs.sort(comparator);

        String html = buildHtml(runs);
        Files.writeString(output, html);
    }

    static Run parse(Path file) throws IOException {
        String content = Files.readString(file);
        Run run = new Run();

        run.name = removeExtension(file.getFileName().toString());
        run.provider = run.name.replaceFirst("_\\d+$", "");
        run.cpuModel = find(content, "(?m)^\\s*CPU Model\\s*:\\s*(.+)$");
        if (run.cpuModel == null)
            run.cpuModel = find(content, "(?m)^\\s*Processor\\s*:\\s*(.+)$");

        String cores = find(content, "(?m)^\\s*CPU Cores\\s*:\\s*(\\d+)\\s*@\\s*([\\d.]+)");
        if (cores != null) {
            Matcher m = Pattern.compile("(\\d+)\\s*@\\s*([\\d.]+)").matcher(cores);
            if (m.find()) {
                run.cores = Integer.parseInt(m.group(1));
                run.cpuMHz = Double.parseDouble(m.group(2));
            }
        }

        if (run.cores == 0) {
            String yabsCores = find(content, "(?m)^\\s*CPU cores\\s*:\\s*(\\d+)\\s*@\\s*([\\d.]+)");
            if (yabsCores != null) {
                Matcher m = Pattern.compile("(\\d+)\\s*@\\s*([\\d.]+)").matcher(yabsCores);
                if (m.find()) {
                    run.cores = Integer.parseInt(m.group(1));
                    run.cpuMHz = Double.parseDouble(m.group(2));
                }
            }
        }

        String ram = find(content, "(?m)^\\s*Total RAM\\s*:\\s*([\\d.]+)\\s*(GB|GiB)");
        if (ram == null)
            ram = find(content, "(?m)^\\s*RAM\\s*:\\s*([\\d.]+)\\s*(GB|GiB)");
        if (ram != null)
            run.ramGB = number(ram);

        String disk = find(content, "(?m)^\\s*Total Disk\\s*:\\s*([\\d.]+)\\s*GB");
        if (disk == null)
            disk = find(content, "(?m)^\\s*Disk\\s*:\\s*([\\d.]+)\\s*Gi?B");
        if (disk != null)
            run.diskGB = number(disk);

        String io = find(content, "(?m)^\\s*I/O Speed\\(average\\)\\s*:\\s*([\\d.]+)\\s*MB/s");
        if (io != null)
            run.ioAverageMBs = number(io);

        run.os = firstNonNull(
                find(content, "(?m)^\\s*OS\\s*:\\s*(.+)$"),
                find(content, "(?m)^\\s*Distro\\s*:\\s*(.+)$")
        );
        run.kernel = find(content, "(?m)^\\s*Kernel\\s*:\\s*(.+)$");
        run.virtualization = firstNonNull(
                find(content, "(?m)^\\s*Virtualization\\s*:\\s*(.+)$"),
                find(content, "(?m)^\\s*VM Type\\s*:\\s*(.+)$")
        );
        run.location = find(content, "(?m)^\\s*Location\\s*:\\s*(.+)$");
        run.region = find(content, "(?m)^\\s*Region\\s*:\\s*(.+)$");
        run.organization = firstNonNull(
                find(content, "(?m)^\\s*Organization\\s*:\\s*(.+)$"),
                find(content, "(?m)^\\s*ASN\\s*:\\s*(.+)$")
        );

        String gbSingle = find(content, "(?m)^\\s*Single Core\\s*\\|\\s*(\\d+)");
        String gbMulti = find(content, "(?m)^\\s*Multi Core\\s*\\|\\s*(\\d+)");
        if (gbSingle != null)
            run.geekbenchSingle = number(gbSingle);
        if (gbMulti != null)
            run.geekbenchMulti = number(gbMulti);

        parseFio(content, run);
        parseSpeedtest(content, run);
        parseIperf(content, run);
        return run;
    }

    static void parseFio(String content, Run r) {
        String currentBlock = null;
        String[] lines = content.split("\\R");
        for (String line : lines) {
            Matcher block = Pattern.compile("Block Size\\s*\\|\\s*(4k|64k|512k|1m)").matcher(line);
            if (block.find()) {
                currentBlock = block.group(1);
                r.fio.putIfAbsent(currentBlock, new Fio());
            }

            Matcher row = Pattern.compile(
                    "^\\s*(Read|Write|Total)\\s*\\|\\s*([\\d.]+)\\s*(KB/s|MB/s|GB/s)\\s*\\((\\d+)\\)"
            ).matcher(line);
            if (row.find() && currentBlock != null) {
                String type = row.group(1);
                double value = Double.parseDouble(row.group(2));
                String unit = row.group(3);
                double iops = Double.parseDouble(row.group(4));

                value = toMBs(value, unit);
                Fio fio = r.fio.get(currentBlock);

                switch (type) {
                    case "Read" -> {
                        fio.readMBs = value;
                        fio.readIOPS = iops;
                    }
                    case "Write" -> {
                        fio.writeMBs = value;
                        fio.writeIOPS = iops;
                    }
                    case "Total" -> {
                        fio.totalMBs = value;
                        fio.totalIOPS = iops;
                    }
                }
            }
        }
    }

    static void parseSpeedtest(String content, Run r) {
        Pattern p = Pattern.compile(
                "(?m)^\\s*([^\\r\\n|]+?)\\s+([\\d.]+)\\s*Mbps\\s+([\\d.]+)\\s*Mbps\\s+([\\d.]+)\\s*ms\\s*$"
        );
        Matcher m = p.matcher(content);
        while (m.find()) {
            String location = m.group(1).trim();
            if (location.equalsIgnoreCase("Node Name"))
                continue;
            Speedtest s = new Speedtest();
            s.upload = Double.parseDouble(m.group(2));
            s.download = Double.parseDouble(m.group(3));
            s.ping = Double.parseDouble(m.group(4));
            r.speedtests.put(location, s);
        }
    }

    static void parseIperf(String content, Run r) {
        boolean ipv6 = false;
        String[] lines = content.split("\\R");
        for (String line : lines) {
            if (line.contains("iperf3 Network Speed Tests (IPv6)")) {
                ipv6 = true;
                continue;
            }
            if (line.contains("iperf3 Network Speed Tests (IPv4)")) {
                ipv6 = false;
                continue;
            }

            Matcher m = Pattern.compile(
                    "^\\s*([^|]+?)\\s*\\|\\s*([^|]+?)\\s*\\|\\s*([\\d.]+)\\s*(Gbits/sec|Mbits/sec)\\s*\\|\\s*" +
                            "([\\d.]+)\\s*(Gbits/sec|Mbits/sec)\\s*\\|\\s*([\\d.]+)\\s*ms\\s*$",
                    Pattern.CASE_INSENSITIVE
            ).matcher(line);
            if (!m.find())
                continue;

            String provider = m.group(1).trim();
            String location = m.group(2).trim();
            String key = provider + " — " + location;
            Iperf speed = new Iperf();
            speed.send = toMbps(Double.parseDouble(m.group(3)), m.group(4));
            speed.recv = toMbps(Double.parseDouble(m.group(5)), m.group(6));
            speed.ping = Double.parseDouble(m.group(7));

            if (ipv6) {
                r.iperf6.put(key, speed);
            } else {
                r.iperf4.put(key, speed);
            }
        }
    }

    static String buildHtml(List<Run> runs) {
        StringBuilder html = new StringBuilder();
        html.append("""
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>VPS Benchmark Report</title>
                
                <style>
                
                * {
                    box-sizing: border-box;
                }
                
                body {
                    margin: 0;
                    background: #0b1020;
                    color: #e8ecf7;
                    font-family:
                        -apple-system,
                        BlinkMacSystemFont,
                        "Segoe UI",
                        Roboto,
                        Arial,
                        sans-serif;
                }
                
                .container {
                    max-width: 1800px;
                    margin: auto;
                    padding: 30px;
                }
                
                h1 {
                    margin: 0 0 8px 0;
                    font-size: 32px;
                }
                
                h2 {
                    margin-top: 42px;
                    border-bottom: 1px solid #28314a;
                    padding-bottom: 10px;
                }
                
                h3 {
                    color: #b9c4df;
                }
                
                .subtitle {
                    color: #8995b2;
                    margin-bottom: 30px;
                }
                
                .grid {
                    display: grid;
                    grid-template-columns:
                        repeat(auto-fit, minmax(360px, 1fr));
                    gap: 20px;
                }
                
                .card {
                    background: #131a2d;
                    border: 1px solid #26304a;
                    border-radius: 14px;
                    padding: 18px;
                    box-shadow: 0 8px 30px rgba(0,0,0,.18);
                }
                
                .card h3 {
                    margin-top: 0;
                    color: white;
                }
                
                .metric {
                    display: flex;
                    justify-content: space-between;
                    border-bottom: 1px solid #222b40;
                    padding: 8px 0;
                }
                
                .metric:last-child {
                    border-bottom: none;
                }
                
                .metric-name {
                    color: #9da9c4;
                }
                
                .metric-value {
                    color: #fff;
                    font-weight: 600;
                    text-align: right;
                }
                
                table {
                    width: 100%;
                    border-collapse: collapse;
                    margin-top: 15px;
                    background: #131a2d;
                    border-radius: 12px;
                    overflow: hidden;
                }
                
                th {
                    background: #1b243b;
                    color: #b9c4df;
                    text-align: left;
                    padding: 10px;
                    white-space: nowrap;
                }
                
                td {
                    padding: 9px 10px;
                    border-top: 1px solid #222b40;
                    white-space: nowrap;
                }
                
                tr:hover td {
                    background: #19223a;
                }
                
                .chart-card {
                    background: #131a2d;
                    border: 1px solid #26304a;
                    border-radius: 14px;
                    padding: 18px;
                    overflow-x: auto;
                }
                
                .chart-card h3 {
                    margin-top: 0;
                    color: white;
                }
                
                .legend {
                    display: flex;
                    flex-wrap: wrap;
                    gap: 12px;
                    margin: 8px 0 15px;
                }
                
                .legend-item {
                    display: flex;
                    align-items: center;
                    gap: 6px;
                    color: #aab5cc;
                    font-size: 13px;
                }
                
                .legend-color {
                    width: 12px;
                    height: 12px;
                    border-radius: 3px;
                }
                
                .tag {
                    display: inline-block;
                    padding: 4px 8px;
                    border-radius: 6px;
                    background: #202a45;
                    color: #bdc8df;
                    font-size: 12px;
                    margin: 2px;
                }
                
                .good {
                    color: #50e3a4;
                }
                
                .warning {
                    color: #ffca5c;
                }
                
                .bad {
                    color: #ff6b7a;
                }
                
                .note {
                    color: #7986a3;
                    font-size: 13px;
                    margin-top: 10px;
                }
                
                @media(max-width: 700px) {
                    .container {
                        padding: 15px;
                    }
                
                    h1 {
                        font-size: 25px;
                    }
                }
                
                </style>
                </head>
                <body>
                <div class="container">
                """);

        html.append("<h1>VPS Benchmark Report</h1>");
        html.append("<div class=\"subtitle\">")
                .append(runs.size())
                .append(" отдельных прогонов · без усреднения</div>");
        html.append("<h2>Серверы</h2>");
        html.append(buildServersTable(runs));
        html.append("<h2>CPU</h2>");
        html.append(chartCard(
                "Geekbench 6 — Single Core",
                "points",
                collect(runs, "Geekbench Single"),
                false
        ));
        html.append(chartCard(
                "Geekbench 6 — Multi Core",
                "points",
                collect(runs, "Geekbench Multi"),
                false
        ));
        html.append(chartCard(
                "Количество CPU cores",
                "cores",
                collect(runs, "Cores"),
                false
        ));
        html.append(chartCard(
                "Частота CPU",
                "MHz",
                collect(runs, "CPU MHz"),
                false
        ));
        html.append("<h2>RAM / Disk</h2>");
        html.append(chartCard(
                "RAM",
                "GB",
                collect(runs, "RAM"),
                false
        ));
        html.append(chartCard(
                "Размер диска",
                "GB",
                collect(runs, "Disk"),
                false
        ));
        html.append(chartCard(
                "bench.sh I/O Average",
                "MB/s",
                collect(runs, "IO Average"),
                false
        ));
        html.append("<h2>Disk — fio MB/s</h2>");

        for (String block : List.of("4k", "64k", "512k", "1m")) {
            html.append(chartCard(
                    "fio " + block + " — Read",
                    "MB/s",
                    collectFio(runs, block, "read"),
                    false
            ));
            html.append(chartCard(
                    "fio " + block + " — Write",
                    "MB/s",
                    collectFio(runs, block, "write"),
                    false
            ));
            html.append(chartCard(
                    "fio " + block + " — Total",
                    "MB/s",
                    collectFio(runs, block, "total"),
                    false
            ));
        }
        html.append("<h2>Disk — fio IOPS</h2>");

        for (String block : List.of("4k", "64k", "512k", "1m")) {
            html.append(chartCard(
                    "fio " + block + " — Read IOPS",
                    "IOPS",
                    collectFio(runs, block, "readIOPS"),
                    false
            ));
            html.append(chartCard(
                    "fio " + block + " — Write IOPS",
                    "IOPS",
                    collectFio(runs, block, "writeIOPS"),
                    false
            ));
            html.append(chartCard(
                    "fio " + block + " — Total IOPS",
                    "IOPS",
                    collectFio(runs, block, "totalIOPS"),
                    false
            ));
        }

        html.append("<h2>bench.sh — Speedtest</h2>");
        Set<String> speedLocations = runs.stream()
                .flatMap(r -> r.speedtests.keySet().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        for (String location : speedLocations) {
            html.append(chartCard(
                    "Speedtest " + location + " — Upload",
                    "Mbps",
                    collectSpeedtest(runs, location, "upload"),
                    false
            ));
            html.append(chartCard(
                    "Speedtest " + location + " — Download",
                    "Mbps",
                    collectSpeedtest(runs, location, "download"),
                    false
            ));
            html.append(chartCard(
                    "Speedtest " + location + " — Ping",
                    "ms",
                    collectSpeedtest(runs, location, "ping"),
                    true
            ));
        }

        html.append("<h2>iperf3 IPv4</h2>");
        Set<String> iperf4Locations = runs.stream()
                .flatMap(r -> r.iperf4.keySet().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        for (String location : iperf4Locations) {
            html.append(chartCard(
                    "IPv4 — " + location + " — Send",
                    "Mbps",
                    collectIperf(runs, location, "send"),
                    false
            ));
            html.append(chartCard(
                    "IPv4 — " + location + " — Receive",
                    "Mbps",
                    collectIperf(runs, location, "recv"),
                    false
            ));
            html.append(chartCard(
                    "IPv4 — " + location + " — Ping",
                    "ms",
                    collectIperf(runs, location, "ping"),
                    true
            ));
        }
        html.append("</div>\n</body>\n</html>");
        return html.toString();
    }

    static String buildServersTable(List<Run> runs) {
        StringBuilder s = new StringBuilder();
        s.append("""
                <div class="chart-card">
                <table>
                <thead>
                <tr>
                    <th>Provider</th>
                    <th>Run</th>
                    <th>CPU</th>
                    <th>Cores</th>
                    <th>MHz</th>
                    <th>RAM</th>
                    <th>Disk</th>
                    <th>OS</th>
                    <th>VM</th>
                    <th>Location</th>
                </tr>
                </thead>
                <tbody>
                """);

        for (Run r : runs) {
            s.append("<tr>");
            s.append("<td><b>").append(escape(r.provider)).append("</b></td>");
            s.append("<td>").append(escape(r.name)).append("</td>");
            s.append("<td>").append(escape(nvl(r.cpuModel))).append("</td>");
            s.append("<td>").append(r.cores).append("</td>");
            s.append("<td>").append(fmt(r.cpuMHz)).append("</td>");
            s.append("<td>").append(fmt(r.ramGB)).append(" GB</td>");
            s.append("<td>").append(fmt(r.diskGB)).append(" GB</td>");
            s.append("<td>").append(escape(nvl(r.os))).append("</td>");
            s.append("<td>").append(escape(nvl(r.virtualization))).append("</td>");
            s.append("<td>").append(escape(nvl(r.location))).append("</td>");
            s.append("</tr>");
        }
        s.append("</tbody>\n</table>\n</div>");
        return s.toString();
    }

    record ChartValue(String label, String provider, double value) {}

    static String chartCard(String title, String unit, List<ChartValue> values, boolean lowerBetter) {
        values = values.stream()
                .filter(v -> !Double.isNaN(v.value))
                .collect(Collectors.toList());
        if (values.isEmpty())
            return "";

        StringBuilder s = new StringBuilder();
        s.append("<div class=\"chart-card\">");
        s.append("<h3>").append(escape(title)).append("</h3>");
        s.append("<div class=\"legend\">");

        Set<String> providers = values.stream()
                .map(v -> v.provider)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        for (String provider : providers) {
            s.append("<div class=\"legend-item\">");
            s.append("<div class=\"legend-color\" style=\"background:").append(color(provider)).append("\"></div>");
            s.append(escape(provider));
            s.append("</div>");
        }
        s.append("</div>");
        s.append(svgChart(values, unit));
        s.append("""
                <div class="note">
                Каждый запуск отображается отдельно.
                Одинаковый цвет означает одного провайдера.
                """);
        if (lowerBetter) {
            s.append(" Меньше — лучше.");
        } else {
            s.append(" Больше — лучше.");
        }
        s.append("</div>");
        s.append("</div>");
        return s.toString();
    }

    static String svgChart(List<ChartValue> values, String unit) {
        int width = 1050;
        int rowHeight = 48;
        int left = 260;
        int right = 100;
        int top = 30;
        int height = top + values.size() * rowHeight + 40;
        double max = values.stream().mapToDouble(v -> v.value).max().orElse(1);
        if (max <= 0)
            max = 1;

        StringBuilder svg = new StringBuilder();
        svg.append("<svg ").append("width=\"100%\" ").append("viewBox=\"0 0 ").append(width).append(" ")
                .append(height).append("\" ").append("xmlns=\"http://www.w3.org/2000/svg\">");

        for (int i = 0; i < values.size(); i++) {
            ChartValue v = values.get(i);
            int y = top + i * rowHeight;
            double barWidth = (width - left - right) * v.value / max;
            String c = color(v.provider);

            svg.append("<text ").append("x=\"10\" ").append("y=\"").append(y + 21).append("\" ")
                    .append("fill=\"#aeb8d0\" ").append("font-size=\"13\">").append(escape(v.label)).append("</text>")
                    .append("<rect ").append("x=\"").append(left).append("\" ").append("y=\"").append(y).append("\" ")
                    .append("width=\"").append(width - left - right).append("\" ").append("height=\"25\" ")
                    .append("rx=\"5\" ").append("fill=\"#202940\"/>").append("<rect ").append("x=\"").append(left)
                    .append("\" ").append("y=\"").append(y).append("\" ").append("width=\"")
                    .append(Math.max(barWidth, 2)).append("\" ").append("height=\"25\" ").append("rx=\"5\" ")
                    .append("fill=\"").append(c).append("\"/>").append("<text ").append("x=\"")
                    .append(left + barWidth + 8).append("\" ").append("y=\"").append(y + 18).append("\" ")
                    .append("fill=\"#ffffff\" ").append("font-size=\"13\" ").append("font-weight=\"600\">")
                    .append(fmt(v.value)).append(" ").append(escape(unit)).append("</text>");
        }
        svg.append("</svg>");
        return svg.toString();
    }

    static List<ChartValue> collect(List<Run> runs, String metric) {
        List<ChartValue> result = new ArrayList<>();
        for (Run r : runs) {
            double value = switch (metric) {
                case "Geekbench Single" -> r.geekbenchSingle;
                case "Geekbench Multi" -> r.geekbenchMulti;
                case "Cores" -> r.cores > 0 ? r.cores : Double.NaN;
                case "CPU MHz" -> r.cpuMHz;
                case "RAM" -> r.ramGB;
                case "Disk" -> r.diskGB;
                case "IO Average" -> r.ioAverageMBs;
                default -> Double.NaN;
            };
            result.add(new ChartValue(r.name, r.provider, value));
        }
        return result;
    }

    static List<ChartValue> collectFio(List<Run> runs, String block, String metric) {
        List<ChartValue> result = new ArrayList<>();
        for (Run r : runs) {
            Fio fio = r.fio.get(block);
            if (fio == null)
                continue;
            double value = switch (metric) {
                case "read" -> fio.readMBs;
                case "write" -> fio.writeMBs;
                case "total" -> fio.totalMBs;
                case "readIOPS" -> fio.readIOPS;
                case "writeIOPS" -> fio.writeIOPS;
                case "totalIOPS" -> fio.totalIOPS;
                default -> Double.NaN;
            };
            result.add(new ChartValue(r.name, r.provider, value));
        }
        return result;
    }

    static List<ChartValue> collectSpeedtest(List<Run> runs, String location, String metric) {
        List<ChartValue> result = new ArrayList<>();
        for (Run r : runs) {
            Speedtest st = r.speedtests.get(location);
            if (st == null)
                continue;
            double value = switch (metric) {
                case "upload" -> st.upload;
                case "download" -> st.download;
                case "ping" -> st.ping;
                default -> Double.NaN;
            };
            result.add(new ChartValue(r.name, r.provider, value));
        }
        return result;
    }

    static List<ChartValue> collectIperf(List<Run> runs, String location, String metric) {
        List<ChartValue> result = new ArrayList<>();
        for (Run r : runs) {
            Map<String, Iperf> map = r.iperf4;
            Iperf ip = map.get(location);
            if (ip == null)
                continue;
            double value = switch (metric) {
                case "send" -> ip.send;
                case "recv" -> ip.recv;
                case "ping" -> ip.ping;
                default -> Double.NaN;
            };
            result.add(new ChartValue(r.name, r.provider, value));
        }
        return result;
    }

    static String[] colors = {
            "#186dff", "#50d890", "#ffa112", "#ff5f7a", "#893cff", "#22c7d6",
            "#f36cff", "#ffca28", "#8bc34a", "#ff7043", "#26a69a", "#29b6f6"
    };
    static Map<String, String> colorsByProvider = new HashMap<>();
    static int colorIndex = 0;

    static String color(String provider) {
        if (colorsByProvider.containsKey(provider))
            return colorsByProvider.get(provider);

        String color = colors[colorIndex++];
        if (colorIndex == colors.length)
            colorIndex = 0;

        colorsByProvider.put(provider, color);
        return color;
    }

    static String find(String text, String regex) {
        Matcher m = Pattern.compile(regex).matcher(text);
        if (!m.find())
            return null;
        return m.group(0).replaceFirst("^[^:|]*[:|]\\s*", "").trim();
    }

    static String firstNonNull(String... values) {
        for (String v : values)
            if (v != null && !v.isBlank())
                return v.trim();
        return null;
    }

    static double number(String text) {
        Matcher m = Pattern.compile("[-+]?\\d+(?:\\.\\d+)?").matcher(text);
        if (m.find())
            return Double.parseDouble(m.group());
        return Double.NaN;
    }

    static double toMBs(double value, String unit) {
        return switch (unit.toUpperCase()) {
            case "KB/S" -> value / 1024.0;
            case "GB/S" -> value * 1024.0;
            default -> value;
        };
    }

    static double toMbps(double value, String unit) {
        if (unit.toLowerCase().startsWith("g"))
            return value * 1000.0;
        return value;
    }

    static String removeExtension(String name) {
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }

    static String nvl(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }

    static String fmt(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value))
            return "—";
        if (Math.abs(value - Math.round(value)) < 0.00001)
            return String.valueOf(Math.round(value));
        return String.format(Locale.US, "%.2f", value);
    }

    static String escape(String value) {
        if (value == null)
            return "";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
