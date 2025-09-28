package ru.prohor.universe.padawan.tasks.yacontest;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// https://contest.yandex.ru/contest/19036/problems/
@ContestRound("Бэкенд — Пробный раунд")
public class Contest_19036 {
    @ContestTask("Камни и украшения")
    private static void taskA() throws Exception {
        try (
                BufferedReader reader = new BufferedReader(new FileReader("input.txt"));
                BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))
        ) {
            Set<Character> jewelry = reader.readLine()
                    .chars()
                    .mapToObj(c -> (char) c)
                    .collect(Collectors.toCollection(HashSet::new));
            writer.write(String.valueOf(
                    reader.readLine().chars().mapToObj(c -> (char) c).filter(jewelry::contains).count()
            ));
        }
    }

    @ContestTask("Будильники")
    private static void taskB() throws Exception {
        try (
                BufferedReader reader = new BufferedReader(new FileReader("input.txt"));
                BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))
        ) {
            String[] parts = reader.readLine().split(" ");
            int period = Integer.parseInt(parts[1]);
            int alarmsToWakeUp = Integer.parseInt(parts[2]);

            Map<Integer, Integer> offsets = new HashMap<>();
            for (int time : Arrays.stream(reader.readLine().split(" ")).mapToInt(Integer::parseInt).toArray())
                offsets.compute(time % period, (offset, v) -> v == null ? time : v > time ? time : v);

            int[] sortedOffsets = offsets.values().stream().sorted().mapToInt(i -> i).toArray();
            int lastPeriod = 0;
            int currentAlarms = 0;

            List<Integer> usedOffsets = new ArrayList<>(sortedOffsets.length);

            for (int offset : sortedOffsets) {
                int currentPeriod = offset / period;
                int newAlarms = currentAlarms + (currentPeriod - lastPeriod) * usedOffsets.size() + 1;
                if (newAlarms > alarmsToWakeUp) {
                    int currentOffset = offset % period;
                    int before = (int) usedOffsets.stream().filter(o -> o < currentOffset).count();
                    newAlarms = currentAlarms + (currentPeriod - lastPeriod - 1) * usedOffsets.size() + 1 + before;
                    if (newAlarms <= alarmsToWakeUp) {
                        if (newAlarms == alarmsToWakeUp)
                            writer.write(String.valueOf(offset));
                        else {
                            int[] after = usedOffsets.stream()
                                    .filter(o -> o > currentOffset)
                                    .mapToInt(t -> t)
                                    .sorted()
                                    .toArray();
                            int result = currentPeriod * period + after[alarmsToWakeUp - newAlarms - 1];
                            writer.write(String.valueOf(result));
                        }
                        return;
                    }
                    break;
                }
                lastPeriod = currentPeriod;
                currentAlarms = newAlarms;
                usedOffsets.add(offset % period);
            }
            int result;

            int alarmsDiff = alarmsToWakeUp - currentAlarms;
            int[] sortedUsedOffsets = usedOffsets.stream().mapToInt(i -> i).sorted().toArray();
            lastPeriod = lastPeriod + alarmsDiff / sortedUsedOffsets.length;
            if (alarmsDiff % sortedUsedOffsets.length == 0)
                result = lastPeriod * period + sortedUsedOffsets[sortedUsedOffsets.length - 1];
            else
                result = (lastPeriod + 1) * period + sortedUsedOffsets[alarmsDiff % sortedUsedOffsets.length - 1];
            writer.write(String.valueOf(result));
        }
    }

    @ContestTask("Интересная игра")
    private static void taskC() throws Exception {
        try (
                BufferedReader reader = new BufferedReader(new FileReader("input.txt"));
                BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))
        ) {
            int pet = 0;
            int vas = 0;
            int targetScores = Integer.parseInt(reader.readLine().split(" ")[0]);
            for (int card : Arrays.stream(reader.readLine().split(" ")).mapToInt(Integer::parseInt).toArray()) {
                boolean div3 = card % 3 == 0;
                boolean div5 = card % 5 == 0;
                if (div3 && !div5) {
                    pet++;
                    if (pet == targetScores) {
                        writer.write("Petya");
                        return;
                    }
                }
                if (!div3 && div5) {
                    vas++;
                    if (vas == targetScores) {
                        writer.write("Vasya");
                        return;
                    }
                }
            }
            if (pet > vas)
                writer.write("Petya");
            else if (pet < vas)
                writer.write("Vasya");
            else
                writer.write("Draw");
        }
    }

    @ContestTask("Поиск ломающего коммита")
    private static void taskD() throws Exception {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out))
        ) {
            // commits from 1 inclusive to n inclusive
            int left = 1;
            int right = Integer.parseInt(reader.readLine());

            while (left < right) {
                int middle = (left + right) / 2;
                writer.write(String.valueOf(middle));
                writer.newLine();
                writer.flush(); // must be here
                boolean broken = reader.readLine().equals("0");
                if (broken)
                    right = middle;
                else
                    left = middle + 1;
            }
            writer.write("! " + left);
            writer.newLine();
        }
    }

    @ContestTask("Скачивание ресурсов в дата-центре")
    private static void taskE() throws Exception {
        try (
                BufferedReader reader = new BufferedReader(new FileReader("input.txt"));
                BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))
        ) {
            int nextClusterIndex = 0;
            Map<Integer, Integer> servers = new HashMap<>(); // servers to cluster
            Map<Integer, Set<Integer>> clusters = new HashMap<>();

            int n = Integer.parseInt(reader.readLine());
            for (int i = 0; i < n; ++i) {
                String[] parts = reader.readLine().split(" ");
                int s1 = Integer.parseInt(parts[0]);
                int s2 = Integer.parseInt(parts[1]);

                Optional<Integer> c1 = Optional.ofNullable(servers.get(s1));
                Optional<Integer> c2 = Optional.ofNullable(servers.get(s2));

                if (c1.isPresent()) {
                    if (c2.isPresent()) {
                        if (!c1.get().equals(c2.get())) {
                            int index = c1.get();
                            Set<Integer> cluster = clusters.get(index);
                            Set<Integer> oldCluster = clusters.remove(c2.get());
                            cluster.addAll(oldCluster);
                            for (Integer server : oldCluster)
                                servers.put(server, index);
                        }
                    } else {
                        servers.put(s2, c1.get());
                        clusters.get(c1.get()).add(s2);
                    }
                } else {
                    if (c2.isPresent()) {
                        servers.put(s1, c2.get());
                        clusters.get(c2.get()).add(s1);
                    } else {
                        int clusterIndex = nextClusterIndex++;
                        Set<Integer> clusterServers = new HashSet<>();
                        clusterServers.add(s1);
                        clusterServers.add(s2);
                        clusters.put(clusterIndex, clusterServers);
                        servers.put(s1, clusterIndex);
                        servers.put(s2, clusterIndex);
                    }
                }
            }

            int q = Integer.parseInt(reader.readLine());
            for (int i = 0; i < q; ++i) {
                Integer target = Integer.parseInt(reader.readLine().split(" ")[0]);
                String[] fileCarries = reader.readLine().split(" ");
                List<String> result = new ArrayList<>();
                for (String fileCarry : fileCarries) {
                    if (servers.get(target).equals(servers.get(Integer.parseInt(fileCarry))))
                        result.add(fileCarry);
                }
                writer.write(String.valueOf(result.size()));
                writer.write(" ");
                writer.write(String.join(" ", result));
                writer.newLine();
            }
        }
    }

    @ContestTask("Сложение чисел")
    private static void taskF() throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader("input.txt"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))
        ) {
            String urlString = reader.readLine() + ":" + reader.readLine() +
                    "?a=" + reader.readLine() + "&b=" + reader.readLine();
            URL url = URI.create(urlString).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
            StringBuilder response = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null)
                response.append(output);
            connection.disconnect();

            long num = Arrays.stream(response.toString().replace('[', ' ').replace(']', ' ')
                    .replaceAll(" ", "").split(",")).mapToLong(Long::parseLong).sum();
            writer.write(String.valueOf(num));
        }
    }

    @ContestTask("SQL")
    private static void taskG() {
        String query = "SELECT COUNT(DISTINCT(ID)) as res FROM tmp;";
        System.out.println(query);
    }

    @ContestTask("Сервис валидации телефонных номеров")
    private static void taskH() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(7777), 0);

        server.createContext("/ping", new PingHandler());
        server.createContext(
                "/shutdown", exchange -> {
                    if (!exchange.getRequestURI().getPath().equals("/shutdown")) {
                        notFound(exchange);
                        return;
                    }
                    success(exchange);
                    server.stop(0);
                    System.exit(0);
                }
        );
        server.createContext("/validatePhoneNumber", new ValidateHandler());
        server.createContext("/", new RootHandler());

        ExecutorService executor = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()
        );
        server.setExecutor(executor);
        server.start();
    }

    @ContestTask("Сервис валидации телефонных номеров. ServerSocket")
    private static void taskHV2() throws Exception {
        ServerSocket serverSocket = new ServerSocket(7777);
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                executor.submit(() -> runHandler(clientSocket));
            } catch (IOException e) {
                if (running) {
                    throw new RuntimeException(e);
                }
            }
        }

        serverSocket.close();
        executor.shutdown();
    }

    private static volatile boolean running = true;
    private static ExecutorService executor;

    private static void runHandler(Socket socket) {
        try (
                socket;
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                OutputStream out = socket.getOutputStream()
        ) {
            try {

                String requestLine = in.readLine();
                if (requestLine == null)
                    return;
                String line = in.readLine();
                while (line != null && !line.isEmpty())
                    line = in.readLine();

                String[] parts = requestLine.split(" ");
                if (parts.length < 2) {
                    sendResponse(out, 400, "Bad Request", "text/plain", "");
                    return;
                }
                String fullPath = parts[1];

                String path;
                String query = "";
                int queryIndex = fullPath.indexOf('?');
                if (queryIndex != -1) {
                    path = fullPath.substring(0, queryIndex);
                    query = fullPath.substring(queryIndex + 1);
                } else {
                    path = fullPath;
                }

                handleRequest(out, path, query);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleRequest(OutputStream out, String path, String query) throws IOException {
        switch (path) {
            case "/ping":
                sendResponse(out, 200, "OK", "text/plain", "");
                break;
            case "/shutdown":
                sendResponse(out, 200, "OK", "text/plain", "");
                running = false;
                new Thread(() -> {
                    try {
                        Thread.sleep(100);
                        executor.shutdown();
                        System.exit(0);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
                break;
            case "/validatePhoneNumber":
                Map<String, String> params = queryToMap(query);
                if (!params.containsKey("phone_number")) {
                    sendResponse(out, 400, "Bad Request", "text/plain", "");
                    return;
                }

                String result = validatePhone(params.get("phone_number"));
                sendResponse(out, 200, "OK", "application/json", result);
                break;
            default:
                sendResponse(out, 404, "Not Found", "text/plain", "");
                break;
        }
    }

    private static final String INVALID_JSON = "{\"status\": false}";

    private static String validatePhone(String phone) {
        if (phone.contains("\f") || phone.contains("\n") || phone.contains("\r") || phone.contains("\t")) {
            return INVALID_JSON;
        }
        if (phone.startsWith(" ") || phone.endsWith(" ")) {
            return INVALID_JSON;
        }
        try {
            phone = URLDecoder.decode(phone, StandardCharsets.UTF_8.displayName());
        } catch (Exception ignored) {
            return INVALID_JSON;
        }

        if (phone.startsWith(" 7") || phone.startsWith("+7"))
            phone = phone.substring(2);
        else if (phone.startsWith("8"))
            phone = phone.substring(1);
        else {
            return INVALID_JSON;
        }

        String code;
        if (PATTERNS[0].matcher(phone).matches()) {
            code = phone.substring(1, 4);
            phone = phone.substring(4);
        } else if (PATTERNS[1].matcher(phone).matches()) {
            code = phone.substring(1, 4);
            phone = phone.substring(4);
        } else if (PATTERNS[2].matcher(phone).matches()) {
            code = phone.substring(2, 5);
            phone = phone.substring(6);
        } else if (PATTERNS[3].matcher(phone).matches()) {
            code = phone.substring(0, 3);
            phone = phone.substring(3);
        } else {
            return INVALID_JSON;
        }

        if (!CODES.contains(code)) {
            return INVALID_JSON;
        }
        phone = phone.replace('-', ' ').replaceAll(" ", "");
        phone = "+7-" + code + "-" + phone.substring(0, 3) + "-" + phone.substring(3);
        return "{\"status\":true,\"normalized\":\"" + phone + "\"}";
    }

    private static void sendResponse(
            OutputStream out, int statusCode, String statusText,
            String contentType, String body
    ) throws IOException {
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

        String response = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + bodyBytes.length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";

        out.write(response.getBytes(StandardCharsets.UTF_8));
        out.write(bodyBytes);
        out.flush();
    }

    private static class PingHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestURI().getPath().equals("/ping")) {
                notFound(exchange);
                return;
            }
            success(exchange);
        }
    }

    private static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            notFound(exchange);
        }
    }

    private static class ValidateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestURI().getPath().equals("/validatePhoneNumber")) {
                notFound(exchange);
                return;
            }
            Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
            if (!params.containsKey("phone_number")) {
                sendCode(exchange, 400);
                return;
            }

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            validatePhone(exchange, params.get("phone_number"));
        }
    }

    private static void validatePhone(HttpExchange exchange, String phone) throws IOException {
        if (phone.contains("\f") || phone.contains("\n") || phone.contains("\r") || phone.contains("\t")) {
            invalidFormat(exchange);
            return;
        }
        if (phone.startsWith(" ") || phone.endsWith(" ")) {
            invalidFormat(exchange);
            return;
        }
        try {
            phone = URLDecoder.decode(phone, StandardCharsets.UTF_8.displayName());
        } catch (Exception ignored) {
            invalidFormat(exchange);
            return;
        }

        if (phone.startsWith(" 7") || phone.startsWith("+7"))
            phone = phone.substring(2);
        else if (phone.startsWith("8"))
            phone = phone.substring(1);
        else {
            invalidFormat(exchange);
            return;
        }

        String code;
        if (PATTERNS[0].matcher(phone).matches()) {
            code = phone.substring(1, 4);
            phone = phone.substring(4);
        } else if (PATTERNS[1].matcher(phone).matches()) {
            code = phone.substring(1, 4);
            phone = phone.substring(4);
        } else if (PATTERNS[2].matcher(phone).matches()) {
            code = phone.substring(2, 5);
            phone = phone.substring(6);
        } else if (PATTERNS[3].matcher(phone).matches()) {
            code = phone.substring(0, 3);
            phone = phone.substring(3);
        } else {
            invalidFormat(exchange);
            return;
        }

        if (!CODES.contains(code)) {
            invalidFormat(exchange);
            return;
        }
        phone = phone.replace('-', ' ').replaceAll(" ", "");
        phone = "+7-" + code + "-" + phone.substring(0, 3) + "-" + phone.substring(3);
        byte[] bytes = ("{\"status\":true,\"normalized\":\"" + phone + "\"}").getBytes();
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
            os.flush();
        }
    }

    private static final byte[] INVALID = "{\"status\":false}".getBytes();

    private static void invalidFormat(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, INVALID.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(INVALID);
            os.flush();
        }
    }

    private static final Set<String> CODES = new HashSet<>(Set.of("982", "986", "912", "934"));

    private static final Pattern[] PATTERNS = {
            Pattern.compile("\\s\\d{3}\\s\\d{3}\\s\\d{4}"),
            Pattern.compile("\\s\\d{3}\\s\\d{3}\\s\\d{2}\\s\\d{2}"),
            Pattern.compile("\\s\\(\\d{3}\\)\\s\\d{3}-\\d{4}"),
            Pattern.compile("\\d{10}")
    };

    private static void notFound(HttpExchange exchange) throws IOException {
        sendCode(exchange, 404);
    }

    private static void success(HttpExchange exchange) throws IOException {
        sendCode(exchange, 200);
    }

    private static void sendCode(HttpExchange exchange, int code) throws IOException {
        exchange.sendResponseHeaders(code, -1);
        exchange.getResponseBody().close();
    }

    private static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null || query.isEmpty())
            return result;
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length > 1)
                result.put(pair[0], pair[1]);
            else
                result.put(pair[0], "");
        }
        return result;
    }
}
