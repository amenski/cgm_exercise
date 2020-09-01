package com.docomodigital.exerciseapi.mock;

import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mock")
public class ExternalApiMock {

    @RequestMapping(value = "purchase", method = RequestMethod.POST)
    public ResponseEntity<ResponseWrapper<ResponseModelMock>> purchaseMock(@RequestBody RequestModelMock request) {
        ResponseModelMock entityResult = new ResponseModelMock();
        entityResult.setOrderId(generateRandomCode());
        entityResult.setMsisdn(generateRandomCode());
        entityResult.setResult("SUCCESS");
        return new ResponseEntity<>(new ResponseWrapper<>(entityResult), HttpStatus.OK);
    }

    @RequestMapping(value = "refund", method = RequestMethod.POST)
    public ResponseEntity<Object> refundMock(@RequestBody String orderId) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPRSTUVWXYZ0123456789";
    private static Random random = new Random();
    private static String generateRandomCode() {
        char[] charList = ALLOWED_CHARS.toCharArray();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 15; i++) {
            sb.append(charList[random.nextInt(ALLOWED_CHARS.length())]);
        }
        
        return sb.toString();
    }
}
