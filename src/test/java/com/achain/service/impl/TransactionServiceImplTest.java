package com.achain.service.impl;

import com.achain.service.ITransactionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@SpringBootTest(classes = TransactionServiceImplTest.class)
@ComponentScan("com.achain.*")
@RunWith(SpringRunner.class)
public class TransactionServiceImplTest {

    @Autowired
    private ITransactionService transactionService;

    @Test
    public void getBlockTransaction() {
        Map<String, Object> result = transactionService.getBlockTransaction(195022L);
        System.out.println(result.get("data"));
    }

    @Test
    public void broadcastTransaction() {
    }

    @Test
    public void offlineSign() {
        Map<String, Object> result = transactionService.offlineSign("5KE8GLrGoCsCKS7PQiyFcGQkMncVCYw9r6TKwBreHvFRdFWDX9p",
                "1.0", "ACTBk37yzeFcJLqWG4s5Bp9WJqMMLhYvYHop", "");
        System.out.println(result.get("data"));
        String offlineSign = (String) result.get("data");
        result = transactionService.broadcastTransaction(offlineSign);
        System.out.println(result.get("data"));
    }
}