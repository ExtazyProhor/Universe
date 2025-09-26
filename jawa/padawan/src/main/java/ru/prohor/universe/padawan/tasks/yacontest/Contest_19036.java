package ru.prohor.universe.padawan.tasks.yacontest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ContestRound("Бэкенд — Пробный раунд")
public class Contest_19036 {
    @ContestTask("Камни и украшения")
    private static void TaskA() throws Exception {
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
    private static void TaskB() throws Exception {
        throw new Exception();
    }

    @ContestTask("Интересная игра")
    private static void TaskC() throws Exception {
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
    private static void TaskD() throws Exception {
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
    private static void TaskE() throws Exception {
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
    private static void TaskF() throws Exception {
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
    private static void TaskG() {
        String query = "SELECT COUNT(DISTINCT(ID)) as res FROM tmp;";
        System.out.println(query);
    }

    @ContestTask("Сервис валидации телефонных номеров")
    private static void TaskH() throws Exception {
        throw new Exception();
    }
}
