package ru.prohor.universe.padawan.college.des;

import java.security.SecureRandom;

public class DesImpl extends DesBase implements Des {
    @Override
    public long generateKey() {
        return (new SecureRandom().nextLong() << (BLOCK_SIZE - KEY_SIZE)) >>> (BLOCK_SIZE - KEY_SIZE);
    }

    @Override
    public String encode(String message, long key) {
        long[] messageBlocks = getBlocks(message);
        long[] encodedBlocks = new long[messageBlocks.length];
        long[] roundedKeys = getRoundKeys(key);
        for (int i = 0; i < messageBlocks.length; ++i) {
            encodedBlocks[i] = encodeBlock(messageBlocks[i], roundedKeys);
        }
        return getText(encodedBlocks);
    }

    @Override
    public String decode(String encodedMessage, long key) {
        long[] messageBlocks = getBlocks(encodedMessage);
        long[] decodedBlocks = new long[messageBlocks.length];
        long[] roundedKeys = getRoundKeys(key);
        for (int i = 0; i < messageBlocks.length; ++i) {
            decodedBlocks[i] = decodeBlock(messageBlocks[i], roundedKeys);
        }
        return getText(decodedBlocks).trim();
    }
}
