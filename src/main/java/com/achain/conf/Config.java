package com.achain.conf;


import com.achain.utils.ACTAddressUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author yujianjian
 * @since 2017-11-29 下午5:22
 */
@Component
@Slf4j
public class Config {

    @Value("${wallet_url}")
    public String walletUrl;

    @Value("${rpc_user}")
    public String rpcUser;

    @Value("${contract_id}")
    public String contractId;

    @Value("${act_addresses}")
    public String actAddresses;

    @Value("${address_number}")
    public int addressNumber;

    @Value("${key_dir}")
    public String keyDir;

    public List<String> checkActAddress;

    public List<String> contractIds;

    @PostConstruct
    public void getHeaderBlockCount() {
        checkActAddress = Arrays.asList(actAddresses.split(","));
        contractIds = Arrays.asList(contractId.split(","));
    }

    @PostConstruct
    public void generatePrivateKeyAndACTAddress() {
        List<String> resultList = new ArrayList<>();
        for (int i = 0; i < addressNumber; i++) {
            resultList.add(ACTAddressUtils.generateAddress());
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(keyDir);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            resultList.forEach(address -> {
                try {
                    outputStreamWriter.write(address + "\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
