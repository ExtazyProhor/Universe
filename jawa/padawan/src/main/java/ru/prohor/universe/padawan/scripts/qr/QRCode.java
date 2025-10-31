package ru.prohor.universe.padawan.scripts.qr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class QRCode {
    private static final Map<Character, Integer> SPEC_SYMBOLS;
    private static final long[] EXTEND_BYTES = {0B11101100L, 0B10001L};

    static {
        SPEC_SYMBOLS = new HashMap<>();
        int i = 36;
        for (char ch : " $%*+-./:".toCharArray()) {
            SPEC_SYMBOLS.put(ch, i++);
        }
    }

    private TwoColorImage image;

    private int version;
    private int maskIndex;
    private QRCorrectionLevel level;
    private QRDataType type;

    private static long charCode(char ch) {
        ch = Character.toUpperCase(ch);
        if (ch >= 'A' && ch <= 'Z')
            return ch - 'A' + 10;
        if (ch >= '0' && ch <= '9')
            return ch - '0';
        if (SPEC_SYMBOLS.containsKey(ch))
            return SPEC_SYMBOLS.get(ch);
        return 36;
    }

    private static BitList encodeString(String message) {
        BitList list = new BitList();
        boolean firstMember = true;
        long num = 1;
        for (char ch : message.toCharArray()) {
            if (firstMember) {
                num = charCode(ch);
            } else {
                num *= 45;
                num += charCode(ch);
                list.addAll(num, 11);
            }
            firstMember = !firstMember;
        }
        if (!firstMember)
            list.addAll(num, 6);
        return list;
    }

    private static BitList encodeBytes(byte[] bytes) {
        BitList list = new BitList();
        for (byte b : bytes)
            list.addAll(b, 8);
        return list;
    }

    private void extendToBytes(BitList list) {
        int targetLength = QRTables.getMaxBitsAvailable(version, level);
        while (list.length() % 8 != 0)
            list.add(false);
        for (int i = 0; list.length() < targetLength; i ^= 1) {
            list.addAll(EXTEND_BYTES[i], 8);
        }
    }

    private int getQRCodeSize() {
        return version * 4 + 17;
    }

    private static List<List<Byte>> calculateCorrectionBytesBlocks(List<List<Byte>> blocks, int correctionBytes) {
        List<List<Byte>> correctionBlocks = new ArrayList<>(blocks.size());
        int[] generatingPolynomial = QRTables.getGeneratingPolynomial(correctionBytes);
        byte nul = 0;

        for (List<Byte> block : blocks) {
            LinkedList<Byte> correctionBlock = new LinkedList<>();
            while (correctionBlock.size() < block.size())
                correctionBlock.add(block.get(correctionBlock.size()));
            int bytesInThisBlock = correctionBlock.size();
            while (correctionBlock.size() < correctionBytes)
                correctionBlock.add(nul);
            for (int j = 0; j < bytesInThisBlock; ++j) {
                byte a = correctionBlock.removeFirst();
                correctionBlock.add(nul);
                if (a == 0)
                    continue;
                int b = QRTables.getFromInverseGaloisField(a & 0xff);

                int k = 0;
                ListIterator<Byte> iterator = correctionBlock.listIterator();
                while (iterator.hasNext()) {
                    int c = (generatingPolynomial[k] + b) % 255;
                    iterator.set((byte) (iterator.next() ^ QRTables.getFromGaloisField(c)));
                    if (++k >= correctionBytes)
                        break;
                }
            }
            correctionBlocks.add(correctionBlock);
        }
        return correctionBlocks;
    }

    private static List<List<Byte>> getBlocks(BitList list, int blocksQuantity) {
        byte[] bytes = list.toByteArray();
        List<List<Byte>> blocks = new ArrayList<>(blocksQuantity);
        int bytesInBlock = bytes.length / blocksQuantity;
        int simpleBlocks = blocksQuantity - (bytes.length % blocksQuantity);
        for (int i = 0; i < blocksQuantity; ++i) {
            List<Byte> block = new ArrayList<>(bytesInBlock + 1);
            int beginIndex = bytesInBlock * i;
            if (i > simpleBlocks)
                beginIndex += (simpleBlocks - i);
            int endIndex = beginIndex + (i >= simpleBlocks ? bytesInBlock + 1 : bytesInBlock);
            while (beginIndex < endIndex)
                block.add(bytes[beginIndex++]);
            blocks.add(block);
        }
        return blocks;
    }

    private byte[] toByteArray(BitList list) {
        int blocksQuantity = QRTables.getBlocksQuantity(version, level);
        int correctionBytes = QRTables.getCorrectionBytesQuantity(version, level);
        List<List<Byte>> blocks = getBlocks(list, blocksQuantity);
        List<List<Byte>> correctionBytesBlocks = calculateCorrectionBytesBlocks(blocks, correctionBytes);
        byte[] arr = new byte[list.length() / 8 + correctionBytesBlocks.size() * correctionBytes];
        int pointer = 0;
        int bytesInBlock = blocks.getFirst().size();
        for (int i = 0; i < bytesInBlock; ++i) {
            for (List<Byte> block : blocks) {
                arr[pointer++] = block.get(i);
            }
        }
        for (List<Byte> block : blocks) {
            if (block.size() == bytesInBlock)
                continue;
            arr[pointer++] = block.getLast();
        }
        for (int i = 0; i < correctionBytes; ++i) {
            for (List<Byte> block : correctionBytesBlocks) {
                arr[pointer++] = block.get(i);
            }
        }
        return arr;
    }

    private void rect(int x, int y, int sizeX, int sizeY, boolean color) {
        for (int i = 0; i < sizeX; ++i)
            for (int j = 0; j < sizeY; ++j)
                image.setBit(i + x, j + y, color);
    }

    private static int getHash(int x, int y) {
        return 191 * x + 193 * y;
    }

    private void addSearchPattern(int x, int y) {
        rect(x, y, 7, 7, true);
        rect(x + 1, y + 1, 5, 5, false);
        rect(x + 2, y + 2, 3, 3, true);
    }

    private void addAlignmentPattern(int x, int y, Set<Integer> occupiedPositions) {
        x -= 2;
        y -= 2;
        rect(x, y, 5, 5, true);
        rect(x + 1, y + 1, 3, 3, false);
        fillModule(x + 2, y + 2);
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                occupiedPositions.add(getHash(i + x, j + y));
            }
        }
    }

    private void addSpecialInformation(Set<Integer> occupiedPositions) {
        addSearchPattern(0, 0);
        addSearchPattern(image.size() - 7, 0);
        addSearchPattern(0, image.size() - 7);
        int[] alignmentPatterns = QRTables.getAlignmentPatterns(version);
        if (version < 7 && version > 1)
            addAlignmentPattern(alignmentPatterns[0], alignmentPatterns[0], occupiedPositions);
        else if (version >= 7) {
            for (int i = 0; i < alignmentPatterns.length; ++i) {
                for (int j = 0; j < alignmentPatterns.length; ++j) {
                    if (i == 0 && j == 0)
                        continue;
                    if (i == alignmentPatterns.length - 1 && j == 0)
                        continue;
                    if (i == 0 && j == alignmentPatterns.length - 1)
                        continue;
                    addAlignmentPattern(alignmentPatterns[i], alignmentPatterns[j], occupiedPositions);
                }
            }
        }
        int endIndex = image.size() - 8;
        boolean isBlack = true;
        for (int i = 8; i < endIndex; ++i) {
            if (isBlack) {
                fillModule(i, 6);
                fillModule(6, i);
            }
            occupiedPositions.add(getHash(i, 6));
            occupiedPositions.add(getHash(6, i));
            isBlack = !isBlack;
        }
        if (version < 7)
            return;
        int[] versionCodes = QRTables.getVersionCodes(version);
        int indexOfOffset = image.size() - 11;
        for (int i = 0; i < 3; ++i) {
            for (int j = 5; j >= 0; --j) {
                if ((versionCodes[i] & 1) == 1) {
                    fillModule(indexOfOffset + i, j);
                    fillModule(j, indexOfOffset + i);
                }
                versionCodes[i] >>= 1;
                occupiedPositions.add(getHash(indexOfOffset + i, j));
                occupiedPositions.add(getHash(j, indexOfOffset + i));
            }
        }
    }

    private void fillModule(int x, int y) {
        image.setBit(x, y, true);
    }

    private void addMaskCode() {
        int maskCode = QRTables.getMaskCode(maskIndex, level);
        int lastIndex = image.size() - 1;
        for (int i = 0; i < 6; ++i) {
            if ((maskCode & 1) == 1) {
                fillModule(8, i);
                fillModule(lastIndex - i, 8);
            }
            maskCode >>= 1;
        }
        if ((maskCode & 1) == 1) {
            fillModule(8, 7);
            fillModule(lastIndex - 6, 8);
        }
        maskCode >>= 1;
        if ((maskCode & 1) == 1) {
            fillModule(8, 8);
            fillModule(lastIndex - 7, 8);
        }
        maskCode >>= 1;
        lastIndex = image.size() - 8;
        fillModule(8, lastIndex++);
        if ((maskCode & 1) == 1) {
            fillModule(7, 8);
            fillModule(8, lastIndex);
        }
        lastIndex++;
        maskCode >>= 1;
        for (int i = 0; i < 6; ++i) {
            if ((maskCode & 1) == 1) {
                fillModule(5 - i, 8);
                fillModule(8, lastIndex + i);
            }
            maskCode >>= 1;
        }
    }

    private boolean setOneDataBit(byte[] data, Set<Integer> set, int x, int y, QRMask mask, int bitPointer) {
        if (!set.contains(getHash(x, y))) {
            boolean isOne = ((data[bitPointer / 8] >>> (7 - bitPointer % 8)) & 1) == 1;
            if (mask.isNeedToInverse(x, y) ^ isOne)
                fillModule(x, y);
            return true;
        }
        return false;
    }

    private void addData(byte[] data, Set<Integer> occupiedPositions) {
        addMaskCode();
        int bitPointer = 0;
        QRMask mask = QRMask.values()[maskIndex];
        int columIndex = image.size() - 1;
        while (columIndex > 0) {
            int begin = (columIndex < 9 ? image.size() - 9 : image.size() - 1);
            int end = (columIndex > image.size() - 8 || columIndex < 9 ? 9 : 0);
            for (int i = begin; i >= end; --i) {
                if (setOneDataBit(data, occupiedPositions, columIndex, i, mask, bitPointer))
                    bitPointer++;
                if (setOneDataBit(data, occupiedPositions, columIndex - 1, i, mask, bitPointer))
                    bitPointer++;
            }
            columIndex -= 2;
            if (columIndex == 6)
                columIndex--;
            for (int i = end; i <= begin; ++i) {
                if (setOneDataBit(data, occupiedPositions, columIndex, i, mask, bitPointer))
                    bitPointer++;
                if (bitPointer / 8 >= data.length)
                    break;
                if (setOneDataBit(data, occupiedPositions, columIndex - 1, i, mask, bitPointer))
                    bitPointer++;
                if (bitPointer / 8 >= data.length)
                    break;
            }
            columIndex -= 2;
        }
    }

    private BitList getList(String message) {
        switch (type) {
            case DIGITS -> throw new UnsupportedOperationException();
            case CHARACTERS -> {
                BitList list = encodeString(message);
                version = QRTables.getOptimalVersion(list.length(), level, type);
                list.addAll(message.length(), QRTables.getLengthOfTheDataQuantityField(version, type), 0);
                list.addAll(0B10L, 4, 0);
                return list;
            }
            case BYTES -> {
                byte[] bytes = message.getBytes();
                BitList list = encodeBytes(bytes);
                version = QRTables.getOptimalVersion(list.length(), level, type);
                list.addAll(bytes.length, QRTables.getLengthOfTheDataQuantityField(version, type), 0);
                list.addAll(0B100L, 4, 0);
                return list;
            }
        }
        throw new IllegalArgumentException();
    }

    public TwoColorImage getQRCode(String message, QRCorrectionLevel level, QRDataType type) {
        this.level = level;
        this.type = type;
        BitList list = getList(message);
        extendToBytes(list);
        byte[] data = toByteArray(list);
        int QRCodeSize = getQRCodeSize();
        image = new TwoColorImage(QRCodeSize);
        Set<Integer> occupiedPositions = new HashSet<>();
        addSpecialInformation(occupiedPositions);
        this.maskIndex = 2;
        addData(data, occupiedPositions);
        return image
                .setOffset(4)
                .setTargetSize(image.size() + 8);
    }

    public TwoColorImage getQRCode(String message, QRCorrectionLevel level) {
        return getQRCode(message, level, QRDataType.BYTES);
    }

    public static void main(String[] args) throws IOException {
        QRCode qrCode = new QRCode();
        BufferedImage image = qrCode
                .getQRCode("https://www.google.com", QRCorrectionLevel.L)
                .setScale(10)
                .toImage();

        File imageFile = new File("QRCode2.png");
        ImageIO.write(image, "png", imageFile);
    }
}
