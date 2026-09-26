package com.ryannguyxn.smartshareexpensehub.group.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyCodeTest {

    @Test
    void shouldCreateValidCurrencyCode() {
        CurrencyCode currencyCode = new CurrencyCode("VND");

        assertEquals("VND", currencyCode.value());
        assertEquals("VND", currencyCode.toString());
    }

    @Test
    void shouldRejectNullCurrencyCode() {
        assertThrows(
                NullPointerException.class,
                () -> new CurrencyCode(null)
        );
    }

    @Test
    void shouldRejectLowercaseCurrencyCode() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CurrencyCode("vnd")
        );
    }

    @Test
    void shouldRejectCurrencyCodeShorterThanThreeCharacters() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CurrencyCode("VN")
        );
    }

    @Test
    void shouldRejectCurrencyCodeLongerThanThreeCharacters() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CurrencyCode("VND1")
        );
    }
}