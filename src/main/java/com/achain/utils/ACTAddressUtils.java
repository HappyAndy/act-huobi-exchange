package com.achain.utils;

import com.google.common.base.Joiner;
import com.ms.data.ACTPrivateKey;
import com.ms.util.RIPEMD160;

import java.util.Base64;

/**
 * @author fyk
 * @since 2017-12-28 11:39
 */
public class ACTAddressUtils {

    private String addressStr;
    private byte[] encoded;

    private static final int checksum_len = 4;

    public enum Type {
        ADDRESS(20),
        CONTRACT(20),
        BALANCE_ID(20),
        PUBLIC_KEY(33),;
        private int length;

        Type(int length) {
            this.length = length;
        }

        public boolean checkLen(int len) {
            return this.length == len;
        }
    }


    public ACTAddressUtils(byte[] encoded, Type type) {
        if (!type.checkLen(encoded.length)) {
            throw new RuntimeException("地址错误");
        }
        this.encoded = encoded;
    }

    private static boolean check(byte[] addressDecode, Type type) {
        int coreLen = addressDecode.length - checksum_len;
        if (!type.checkLen(coreLen)) {
            return false;
        }
        byte[] checksum = RIPEMD160.hash(addressDecode, coreLen);
        for (int i = 0; i < checksum_len; i++) {
            if (addressDecode[coreLen + i] != checksum[i]) {
                return false;
            }
        }
        return true;
    }


    public static boolean check(String address, Type type) {
        return check(Base58.decode(address), type);
    }


    /**
     * 判断是不是act地址
     */
    public static boolean checkActAddress(String address) {
        if (null == address || address.length() < 32) {
            return false;
        }
        if (address.length() > 40) {
            address = address.substring(0, address.length() - 32);
        }
        String aa = address.substring(3, address.length());
        return ACTAddressUtils.check(aa, ACTAddressUtils.Type.ADDRESS);
    }


    public byte[] getEncoded() {
        return encoded;
    }

    public static String generateAddress() {
        ACTPrivateKey actPrivateKey = new ACTPrivateKey();
        return Joiner.on(" ").join("ACT" + actPrivateKey.getAddress().getAddressStr(),
                Base64.getEncoder().encodeToString(actPrivateKey.getKeyStr().getBytes()));
    }
}
