package ru.prohor.universe.padawan.scripts.games;

import ru.prohor.universe.jocasta.core.collections.graph.Digraph;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class ScubaCrafts {
    public static void main(String[] args) {
        int dirt = getDirt();
        System.out.println("dirt=" + dirt);
        System.out.println("stacks=" + dirt / 64.0);
    }

    static int getDirt() {
        TopologicalQueue queue = new TopologicalQueue();
        int dirt = 0;
        queue.add(new MutablePair(Item.ENGINE, 1));

        while (!queue.isEmpty()) {
            MutablePair pair = queue.poll();
            if (pair.item == Item.DIRT) {
                dirt += pair.count;
                continue;
            }
            for (Map.Entry<Item, Integer> entry : pair.item.getMap().entrySet())
                queue.add(new MutablePair(entry.getKey(), entry.getValue() * pair.count));
        }
        return dirt;
    }

    private static class MutablePair {
        private final Item item;
        private int count;

        public MutablePair(Item item, int count) {
            this.item = item;
            this.count = count;
        }
    }

    private static class TopologicalQueue {
        private final Map<Item, MutablePair> map;
        private final PriorityQueue<MutablePair> queue;

        private TopologicalQueue() {
            this.map = new HashMap<>();
            this.queue = new PriorityQueue<>(Comparator.comparing(pair -> pair.item, Item.comparator));
        }

        private boolean isEmpty() {
            return queue.isEmpty();
        }

        private void add(MutablePair pair) {
            if (map.containsKey(pair.item)) {
                MutablePair previous = map.get(pair.item);
                previous.count = previous.count + pair.count;
                return;
            }
            map.put(pair.item, pair);
            queue.add(pair);
        }

        private MutablePair poll() {
            if (queue.isEmpty())
                throw new RuntimeException("queue is empty");
            MutablePair pair = queue.poll();
            map.remove(pair.item);
            return pair;
        }
    }

    private enum Item {
        DIRT(Collections.emptyMap()),
        SEAWEED(Map.of(Item.DIRT, 5)),
        FLOWER(Map.of(Item.DIRT, 4)),
        MUSHROOM(Map.of(Item.SEAWEED, 5)),
        GAS(Map.of(Item.MUSHROOM, 4)),
        STONE(Map.of(Item.DIRT, 2)),
        WOOD(Map.of(Item.SEAWEED, 3, Item.FLOWER, 1)),
        ENERGY(Map.of(Item.STONE, 4, Item.FLOWER, 1, Item.MUSHROOM, 1)),
        COAL(Map.of(Item.WOOD, 3, Item.DIRT, 2, Item.ENERGY, 1)),
        IRON(Map.of(Item.STONE, 3, Item.COAL, 1, Item.DIRT, 1, Item.ENERGY, 1)),
        BOLT(Map.of(Item.STONE, 3, Item.IRON, 1, Item.ENERGY, 1)),
        GOLD(Map.of(Item.IRON, 4, Item.ENERGY, 1, Item.FLOWER, 1)),
        DIAMOND(Map.of(Item.GAS, 2, Item.GOLD, 2, Item.ENERGY, 1)),

        AQUALUNG_1(Map.of(Item.GAS, 4, Item.COAL, 4, Item.ENERGY, 1)),
        AQUALUNG_2(Map.of(Item.AQUALUNG_1, 2, Item.IRON, 4, Item.ENERGY, 1)),
        AQUALUNG_3(Map.of(Item.AQUALUNG_2, 2, Item.IRON, 2, Item.GOLD, 2, Item.ENERGY, 1)),
        AQUALUNG_4(Map.of(Item.AQUALUNG_3, 2, Item.IRON, 2, Item.DIAMOND, 1, Item.BOLT, 1, Item.ENERGY, 1)),
        HELMET_1(Map.of(Item.DIRT, 4, Item.COAL, 4, Item.ENERGY, 1)),
        HELMET_2(Map.of(Item.HELMET_1, 1, Item.BOLT, 2, Item.IRON, 2)),
        HELMET_3(Map.of(Item.HELMET_2, 1, Item.IRON, 2, Item.GOLD, 2)),
        HELMET_4(Map.of(Item.HELMET_3, 1, Item.ENERGY, 2, Item.DIAMOND, 2)),
        FINS_1(Map.of(Item.SEAWEED, 2, Item.COAL, 2, Item.ENERGY, 1)),
        FINS_2(Map.of(Item.FINS_1, 1, Item.SEAWEED, 3, Item.ENERGY, 1)),
        FINS_3(Map.of(Item.FINS_2, 1, Item.SEAWEED, 2, Item.GOLD, 2, Item.ENERGY, 1)),
        FINS_4(Map.of(Item.FINS_3, 1, Item.SEAWEED, 2, Item.DIAMOND, 2, Item.ENERGY, 1)),
        GUN_1(Map.of(Item.ENERGY, 3)),
        GUN_2(Map.of(Item.GUN_1, 3)),
        GUN_3(Map.of(Item.GUN_2, 1, Item.GOLD, 2)),
        GUN_4(Map.of(Item.GUN_3, 1, Item.DIAMOND, 2, Item.FLOWER, 1)),
        FLASHLIGHT_1(Map.of(Item.FLOWER, 4, Item.ENERGY, 1)),
        FLASHLIGHT_2(Map.of(Item.FLASHLIGHT_1, 1, Item.ENERGY, 4)),
        FLASHLIGHT_3(Map.of(Item.FLASHLIGHT_2, 1, Item.GOLD, 4)),
        FLASHLIGHT_4(Map.of(Item.FLASHLIGHT_1, 8, Item.FLASHLIGHT_3, 1)),
        ENGINE(Map.of(
                Item.BOLT, 3, Item.DIAMOND, 2, Item.FLASHLIGHT_4, 1, Item.GUN_4, 1, Item.FINS_4, 1,
                Item.HELMET_4, 1, Item.AQUALUNG_4, 1
        ));

        private final Map<Item, Integer> map;
        private int topologicalOrder;

        Item(Map<Item, Integer> map) {
            this.map = map;
        }

        public Map<Item, Integer> getMap() {
            return map;
        }

        public int getTopologicalOrder() {
            return topologicalOrder;
        }

        public static final Comparator<Item> comparator = Comparator.comparingInt(Item::getTopologicalOrder);

        static {
            Digraph<Item> graph = new Digraph<>();
            for (Item item : values())
                for (Map.Entry<Item, Integer> entry : item.map.entrySet())
                    graph.addEdge(item, entry.getKey());

            List<Item> topologicalSorted = graph.topologicalSortingByKahn();
            for (int i = 0; i < topologicalSorted.size(); ++i)
                topologicalSorted.get(i).topologicalOrder = i;
        }
    }
}
