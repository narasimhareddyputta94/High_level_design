package consisting_hashing;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

public class MurmurHash3Example {
    public static int murmurhash3_x86_32(byte[] data, int offset, int len, int seed) {
        final int c1 = 0xcc9e2d51;
        final int c2 = 0x1b873593;
        final int r1 = 15;
        final int r2 = 13;
        final int m = 5;
        final int n = 0xe6546b64;

        int hash = seed;
        int i = 0;
        for (i = 0; i < len / 4; i++) {
            int k = ByteBuffer.wrap(data, offset + (i * 4), 4).getInt();
            k *= c1;
            k = Integer.rotateLeft(k, r1);
            k *= c2;

            hash ^= k;
            hash = Integer.rotateLeft(hash, r2) * m + n;
        }

        int k1 = 0;
        switch (len & 3) {
            case 3:
                k1 ^= data[offset + (i * 4) + 2] << 16;
            case 2:
                k1 ^= data[offset + (i * 4) + 1] << 8;
            case 1:
                k1 ^= data[offset + (i * 4)];
                k1 *= c1;
                k1 = Integer.rotateLeft(k1, r1);
                k1 *= c2;
                hash ^= k1;
        }

        hash ^= len;
        hash ^= (hash >>> 16);
        hash *= 0x85ebca6b;
        hash ^= (hash >>> 13);
        hash *= 0xc2b2ae35;
        hash ^= (hash >>> 16);

        return hash;
    }

    public static void main(String[] args) {
        String input = "example";
        int seed = 0;
        byte[] data = input.getBytes(StandardCharsets.UTF_8);
        int hash = murmurhash3_x86_32(data, 0, data.length, seed);
        System.out.println(hash);
    }
}
