package ru.prohor.universe.padawan.scripts.parsers;

import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;
import ru.prohor.universe.padawan.Padawan;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.PriorityQueue;

public class HuffmanCodesForFile {
    public static void main(String[] args) {
        int[] arr = getBytesQuantity(Padawan.resolve("txt.txt").toAbsolutePath().toString());
        LeafTreeNode node = Huffman(arr);

        PriorityQueue<ByteAndBinaryCode> queue = new PriorityQueue<>();
        getBinaryCodes(queue, node, "");
    }

    private static void getBinaryCodes(PriorityQueue<ByteAndBinaryCode> queue, LeafTreeNode node, String code) {
        Object left = node.left;
        Object right = node.right;
        if (left instanceof LeafTreeNode)
            getBinaryCodes(queue, (LeafTreeNode) left, code + "0");
        else
            queue.add(new ByteAndBinaryCode((int) left, code + "0"));
        if (right instanceof LeafTreeNode)
            getBinaryCodes(queue, (LeafTreeNode) right, code + "1");
        else
            queue.add(new ByteAndBinaryCode((int) right, code + "1"));
    }

    private static int[] getBytesQuantity(String path) {
        int[] arrBytes = new int[256];
        try (FileInputStream inputStream = new FileInputStream(path)) {
            int bufferSize = 64_000;
            byte[] buffer = new byte[bufferSize];

            while (inputStream.available() > 0) {
                if (inputStream.available() < bufferSize)
                    bufferSize = inputStream.available();
                int readBytes = inputStream.read(buffer, 0, bufferSize);
                assert readBytes != -1;
                for (int i = 0; i < bufferSize; ++i) {
                    arrBytes[buffer[i] & 0b11111111]++;
                }
            }
        } catch (IOException e) {
            Sneaky.throwUnchecked(e);
        }
        return arrBytes;
    }

    private static LeafTreeNode Huffman(int[] bytesCount) {
        PriorityQueue<HuffmanPair> queue = new PriorityQueue<>(bytesCount.length);
        for (int i = 0; i < bytesCount.length; ++i)
            queue.add(new HuffmanPair(bytesCount[i], i));
        while (queue.size() > 1) {
            HuffmanPair p1 = queue.poll();
            HuffmanPair p2 = queue.poll();
            assert p2 != null;
            queue.add(new HuffmanPair(p1.weight + p2.weight, new LeafTreeNode(p1.value, p2.value)));
        }
        assert queue.size() == 1;
        return (LeafTreeNode) queue.poll().value;
    }

    private static class HuffmanPair implements Comparable<HuffmanPair> {
        int weight;
        Object value;

        public HuffmanPair(int weight, Object value) {
            this.weight = weight;
            this.value = value;
        }

        @Override
        public int compareTo(HuffmanPair o) {
            return Integer.compare(this.weight, o.weight);
        }
    }

    private static class ByteAndBinaryCode implements Comparable<ByteAndBinaryCode> {
        int byteValue;
        String code;

        public ByteAndBinaryCode(int byteValue, String code) {
            this.byteValue = byteValue;
            this.code = code;
        }

        @Override
        public int compareTo(ByteAndBinaryCode o) {
            return Integer.compare(byteValue, o.byteValue);
        }

        @Override
        public String toString() {
            return "(" + byteValue + ", " + code + ")";
        }
    }

    private static class LeafTreeNode {
        public Object left;
        public Object right;

        public LeafTreeNode(Object left, Object right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            LeafTreeNode that = (LeafTreeNode) o;
            return Objects.equals(left, that.left) && Objects.equals(right, that.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(left, right);
        }

        @Override
        public String toString() {
            return "Node{\n" +
                    "left=" + left +
                    ",\nright=" + right +
                    "\n}";
        }
    }
}
