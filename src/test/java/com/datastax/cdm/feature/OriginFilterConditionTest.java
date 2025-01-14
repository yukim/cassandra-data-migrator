package com.datastax.cdm.feature;

import com.datastax.cdm.properties.IPropertyHelper;
import com.datastax.cdm.properties.KnownProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OriginFilterConditionTest {

    OriginFilterCondition feature;

    @Mock
    IPropertyHelper propertyHelper;

    @BeforeEach
    public void setup() {
        feature = new OriginFilterCondition();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void smokeTest() {
        String conditionIn = "AND a > 1";
        when(propertyHelper.getString(KnownProperties.FILTER_CQL_WHERE_CONDITION)).thenReturn(conditionIn);

        feature.loadProperties(propertyHelper);
        assertEquals(conditionIn, feature.getFilterCondition());
    }

    @Test
    public void smokeTest_featureDisabled() {
        when(propertyHelper.getString(KnownProperties.FILTER_CQL_WHERE_CONDITION)).thenReturn(null);

        assertAll(
                () -> assertTrue(feature.loadProperties(propertyHelper), "loadProperties"),
                () -> assertTrue(feature.initializeAndValidate(null,null), "initializeAndValidate"),
                () -> assertFalse(feature.isEnabled(), "feature should be disabled"),
                () -> assertEquals("", feature.getFilterCondition(), "empty string condition")
        );
    }

    @Test
    public void andIsPrepended() {
        String conditionIn = "a > 1";
        when(propertyHelper.getString(KnownProperties.FILTER_CQL_WHERE_CONDITION)).thenReturn(conditionIn);

        assertAll(
                () -> assertTrue(feature.loadProperties(propertyHelper), "loadProperties"),
                () -> assertTrue(feature.initializeAndValidate(null,null), "initializeAndValidate"),
                () -> assertTrue(feature.isEnabled(), "feature should be disabled"),
                () -> assertEquals(" AND " + conditionIn, feature.getFilterCondition(), "and is prepended")
        );
    }

    @Test
    public void whitespaceOnly() {
        String whitespaceString = " \t ";
        when(propertyHelper.getString(KnownProperties.FILTER_CQL_WHERE_CONDITION)).thenReturn(whitespaceString);

        assertAll(
                () -> assertFalse(feature.loadProperties(propertyHelper), "loadProperties"),
                () -> assertFalse(feature.initializeAndValidate(null,null), "initializeAndValidate"),
                () -> assertFalse(feature.isEnabled(), "feature should be disabled"),
                () -> assertEquals(whitespaceString, feature.getFilterCondition(), "whitespace hurts no one")
        );
    }
}
