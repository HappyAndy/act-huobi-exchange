package com.achain.service;

import java.util.Map;

/**
 * @author fyk
 * @since 2017-12-28 22:43
 */
public interface ITransactionService {
    Map<String, Object> getBlockTransaction(long blockNum);

    Map<String, Object> offlineSign(String privateKey, String amount, String toAddress, String contractId);

    Map<String, Object> broadcastTransaction(String offlineSign);
}
