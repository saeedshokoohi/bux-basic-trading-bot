package com.bux.bot.basic_trading_bot.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

class JsonUtilTest {
    @Test
    void testToJsonFormat() throws JsonProcessingException {
        //given
        SampleClass sample=new SampleClass(1,"myName");
        String expected="{\"id\":1,\"name\":\"myName\"}";
        //when
        String jsonFormat=JsonUtil.toJsonFormat(sample);
        //then
        assertThat(jsonFormat).isNotNull();
        assertThat(jsonFormat).contains(sample.name);
        assertThat(jsonFormat).isEqualTo(expected);



    }

    @Test
    void testJsonToObject() throws JsonProcessingException {
    //given
        String jsonFormat="{\"id\":1,\"name\":\"myName\"}";
   //when
        SampleClass sample=JsonUtil.jsonToObject(jsonFormat,SampleClass.class);
        //then

        assertThat(sample.name).isEqualTo("myName");
        assertThat(sample.id).isEqualTo(1);
    }

    @Test
    void testGetFieldValue() {
        //given
        String jsonFormat="{\"id\":1,\"name\":\"myName\"}";
        //when
        String name=JsonUtil.getFieldValue(jsonFormat,"name");
        //then

        assertThat(name).isEqualTo("myName");


    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
   static class SampleClass{
        int id;
        String name;

    }
}

