package com.bankinc.pruebadev.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import com.bankinc.pruebadev.service.IssuingService;

@SpringBootTest
@RunWith(SpringRunner.class)
class IssuingControllerTest {

    @InjectMocks
    private IssuingController issuingController;
    @Autowired
    private IssuingService    issuingService;

    @BeforeEach
    void setUp() throws Exception {
        this.issuingController = new IssuingController(this.issuingService);
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        this.issuingController = null;
    }

    @Test
    void processGenerateCardCorrectly() {
        Assertions.assertEquals(HttpStatus.OK, this.issuingController.processGenerateCard(999999).getStatusCode());
    }

    @Test
    void processGenerateCardWithProductIdIncorrect() {
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, this.issuingController.processGenerateCard(999).getStatusCode());
    }

}
