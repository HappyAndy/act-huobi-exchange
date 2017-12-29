package com.achain.service.impl;

import com.achain.conf.Config;
import com.achain.domain.entity.ActTransaction;
import com.achain.service.IBlockchainService;
import com.achain.service.ITransactionService;
import com.achain.utils.ACTAddressUtils;
import com.achain.utils.NumberUtils;
import com.achain.utils.ResultUtils;
import com.achain.utils.SDKHttpClient;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ms.data.ACTPrivateKey;
import com.ms.data.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author fyk
 * @since 2017-12-28 22:44
 */
@Service
@Slf4j
public class TransactionServiceImpl implements ITransactionService {

    @Autowired
    private IBlockchainService blockchainService;

    @Autowired
    private Config config;

    @Autowired
    private SDKHttpClient httpClient;

    /**
     * 根据块号查询块上与交易所相关交易
     *
     * @param blockNum 块号
     * @return 查询结果
     * <p>
     * 返回码对应：
     * 0   查询成功
     * 101 未到达查询块号
     * 102 块上没有交易发生
     * 103 当前块没有交易所相关交易
     */
    @Override
    public Map<String, Object> getBlockTransaction(long blockNum) {
        log.info("getBlockTransaction|start|query blockNum=[{}]", blockNum);
        long headerBlockCount = blockchainService.getBlockCount();
        if (headerBlockCount < blockNum) {
            log.error("getBlockTransaction|query blockNum=[{}]|current headerBlockNum=[{}]|don't get to the current block", blockNum, headerBlockCount);
            return ResultUtils.resultFailMap("101", "don't get to the current block");
        }
        Map<String, JSONArray> transactionIds = blockchainService.queryActBlock(String.valueOf(blockNum));
        if (transactionIds.size() == 0) {
            log.info("getBlockTransaction|query blockNum=[{}]|No transaction", blockNum);
            return ResultUtils.resultFailMap("102", "No transaction on block");
        }
        List<ActTransaction> transactions = blockchainService.queryTransactions(transactionIds);
        if (transactions.size() == 0) {
            log.info("getBlockTransaction|query blockNum=[{}]|No relative transaction", blockNum);
            return ResultUtils.resultFailMap("103", "No relative transaction");
        }
        log.info("getBlockTransaction|end|result is [{}]", transactions);
        return ResultUtils.resultSuccessMap("0", "success", transactions);
    }

    /**
     * 广播接口
     *
     * @param offlineSign 离线签名生成的json串
     * @return 广播后的交易ID
     * <p>
     * 错误码对应
     * 0    成功
     * 301  服务器异常或传入参数错误
     * 302  广播失败
     */
    @Override
    public Map<String, Object> broadcastTransaction(String offlineSign) {
        log.info("offlineSign|Start broadcast transaction|offline sign json=[{}]", offlineSign);
        String result = httpClient.post(config.walletUrl, config.rpcUser, "network_broadcast_transaction", offlineSign);
        if (StringUtils.isEmpty(result)) {
            log.error("offlineSign|broadcast fail!|No http response OR parameter illegal");
            return ResultUtils.resultFailMap("301", "No http response OR parameter illegal");
        }
        JSONObject createTaskJSON = JSONObject.parseObject(result);
        String res = createTaskJSON.getString("result");
        if (StringUtils.isEmpty(res)) {
            log.error("offlineSign|broadcast fail!");
            return ResultUtils.resultFailMap("302", "fail");
        }
        log.info("offlineSign|broadcast success. transaction id=[{}]", res);
        return ResultUtils.resultSuccessMap("0", "success", res);
    }

    /**
     * 离线签名
     *
     * @param privateKey 账户私钥
     * @param amount     转账数量
     * @param toAddress  转到地址
     * @param contractId 合约id(转合约币，为空则转ACT)
     * @return 离线签名json
     * <p>
     * 错误码对应
     * 0    成功
     * 201  私钥，地址或金额为空
     * 202  转账金额不是数字
     * 203  转账金额小数位数超过5
     * 204  地址非法
     * 205  签名失败
     */
    @Override
    public Map<String, Object> offlineSign(String privateKey, String amount, String toAddress, String contractId) {
        log.info("offlineSign|Start offline sign");
        if (StringUtils.isEmpty(privateKey) || StringUtils.isEmpty(toAddress) || StringUtils.isEmpty(amount)) {
            log.error("offlineSign|private key, address or amount is empty");
            return ResultUtils.resultFailMap("201", "private key, address or amount is empty");
        }
        privateKey = privateKey.trim();
        amount = amount.trim();
        if (!NumberUtils.isNumber(amount)) {
            log.error("offlineSign|transfer amount=[{}]，transfer amount is not number", amount);
            return ResultUtils.resultFailMap("202", "transfer amount is not number");
        }
        if (((amount.contains(".")) && (amount.length() - (amount.indexOf(".") + 1) > 5))) {
            log.error("offlineSign|transfer amount=[{}]，decimal precision greater than 5", amount);
            return ResultUtils.resultFailMap("203", "decimal precision greater than 5");
        }
        toAddress = toAddress.trim();
        String checkAddress = toAddress;
        if (checkAddress.length() > 50) {
            checkAddress = checkAddress.substring(0, checkAddress.length() - 32);
        }
        if (!ACTAddressUtils.checkActAddress(checkAddress)) {
            log.error("offlineSign|transfer to address=[{}]，illegal address", checkAddress);
            return ResultUtils.resultFailMap("204", "illegal address");
        }
        try {
            Transaction trx;
            if (StringUtils.isEmpty(contractId)) {
                trx = new Transaction(
                        new ACTPrivateKey(privateKey),
                        (long) (Double.parseDouble(amount) * 100000),
                        toAddress,
                        ""
                );
            } else {
                contractId = contractId.trim();
                trx = new Transaction(
                        new ACTPrivateKey(privateKey),
                        contractId,
                        toAddress,
                        amount,
                        5000L
                );
            }
            log.info("offlineSign|offline sign success.result=[{}]", trx.toJSONString());
            return ResultUtils.resultSuccessMap("0", "success", trx.toJSONString());
        } catch (Exception e) {
            log.error("offlineSign|offline sign fail|fail reason is[{}]", e);
            return ResultUtils.resultFailMap("205", "offline sign fail");
        }
    }
}
