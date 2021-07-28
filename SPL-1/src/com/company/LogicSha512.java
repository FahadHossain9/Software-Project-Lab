package com.company;

import java.math.BigInteger;

public class LogicSha512 {
    //Compression function Logic for sha512
    public static long conditionFunction(long e, long f, long g) {
        return (e & f) ^ (~e & g);
    }

    //Majority Function takes 3 buffers
    public static long majorityFunction(long a, long b, long c) {
        return (a & b) ^ (a & c) ^ (b & c);
    }

    //does circular right shift by n bits
    public static long ROTR(long x, int n) {
        return (x >>> n) | (x << (Long.SIZE - 1));
    }

    //will be used in round function(0-512)
    public static long summation0To512(long x) {
        return ROTR(x, 28) ^ ROTR(x, 34) ^ ROTR(x, 39);
    }

    //will be used in round function(1-512)
    public static long summation1To512(long x) {
        return ROTR(x, 14) ^ ROTR(x, 18) ^ ROTR(x, 41);
    }

    //will be used in generating words function(0-512)
    public static long sigma0To512(long x) {
        return ROTR(x, 1) ^ ROTR(x, 8) ^ (x >>> 7);
    }

    //will be used in generating words function(1-512)
    public static long sigma1To512(long x) {
        return ROTR(x, 19) ^ ROTR(x, 61) ^ (x >>> 6);
    }


    //Does the padding to make the message size = 896 mod 1024

    public static byte[] padding(byte[] input) {
        //no matter what it is necessary to append at least 17 bytes
        //128 bit=16 byte for the message size
        //1byte=8bits needed to add 1
        //rests should be filled with zeros
        int size = input.length + 17;
        while (size % 128 != 0) {
            size += 1;
        }

        // The padded byte array will be stored here
        byte[] storageOfPaddedMessage = new byte[size];

        // Copy over the old stuff
        for (int i = 0; i < input.length; i++) {
            storageOfPaddedMessage[i] = input[i];
        }

        // Add the '1' bit as in hexa 1000 0000 is equivalent to  x80
        storageOfPaddedMessage[input.length] = (byte) 0x80;
        //Converting the original length of the input into byte
        byte[] lenghtInByte = BigInteger.valueOf(input.length * 8).toByteArray();

        //Add this to the end of our padded input message
        for (int i = lenghtInByte.length; i > 0; i--) {
            storageOfPaddedMessage[size - i] = lenghtInByte[lenghtInByte.length - i];
        }
        //Before padding the input message size
        System.out.printf("Total message length in bits before padding: %d\n", input.length * 8);
        //After padding the input message size
        System.out.printf("Total message length in bits after padding: %d\n", storageOfPaddedMessage.length * 8);

        return storageOfPaddedMessage;
    }

    //Converts the byte array input starting at index j into a long
    public static long arrayToLong(byte[] input, int j) {
        long v = 0;
        for (int i = 0; i < 8; i++) {
            v = (v << 8) + (input[i + j] & 0xff);
        }
        return v;
    }

    //now we have bytes array after padding
    //we will convert it to blocks longs
    public static long[][] convertToBlocks(byte[] input) {
        // a block has 1024 bits =128 bytes = 16 longs
        long[][] blocks = new long[input.length / 128][16];

        //in each block
        for (int i = 0; i < input.length / 128; i++) {
            //for each long in every block
            for (int j = 0; j < 16; j++) {
                //setting up block value
                blocks[i][j] = arrayToLong(input, i * 128 + j * 8);
            }
        }
        return blocks;
    }

    //Generating words for each blocks
    public static long[][] Message(long[][] M) {
        long[][] W = new long[M.length][80];

        // For each block in the input
        for (int i = 0; i < M.length; i++) {

            System.out.printf("W for block %d\n", i);

            // For each long in the block
            for (int j = 0; j < 16; j++) {
                // Set the initial values of W to be the value of the input directly
                W[i][j] = M[i][j];

                System.out.printf("W(%d): %016x\n", j, W[i][j]);

            }

            // For the rest of the values
            for (int j = 16; j < 80; j++) {
                // Do some math from the SHA512 algorithm
                W[i][j] = sigma1To512(W[i][j - 2]) + W[i][j - 7] + sigma0To512(W[i][j - 15]) + W[i][j - 16];

                System.out.printf("W(%d): %016x\n", j, W[i][j]);

            }

            System.out.println("=====================================");



        }
        return W;
    }
}
