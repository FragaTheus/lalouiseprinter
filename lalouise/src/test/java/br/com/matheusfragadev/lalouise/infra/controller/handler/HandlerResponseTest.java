package br.com.matheusfragadev.lalouise.infra.controller.handler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HandlerResponseTest {

    @Test
    void shouldExposeErrorField() {
        HandlerResponse response = new HandlerResponse("erro");

        assertEquals("erro", response.error());
    }
}

