package com.bux.bot.basic_trading_bot.web.rest;

import com.bux.bot.basic_trading_bot.exception.EntityValidationException;
import com.bux.bot.basic_trading_bot.entity.BotOrderInfo;
import com.bux.bot.basic_trading_bot.entity.enums.BotOrderStatus;
import com.bux.bot.basic_trading_bot.service.BotOrderInfoService;
import com.bux.bot.basic_trading_bot.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@WebMvcTest(BotOrderInfoResource.class)
class BotOrderInfoResourceTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BotOrderInfoService botOrderInfoService;

    @Test
    void testAddSuccessful() throws Exception {
        //given
        BotOrderInfo botOrderInfo = new BotOrderInfo(21l,"oldOrder", "ab23423k","1.0", 12.4, 14.1, 11.1, BotOrderStatus.ACTIVE,"p1","");
        when(this.botOrderInfoService.addNewBotOrderInfo(any()))
                .thenReturn(Mono.just(botOrderInfo));
        //when
        MvcResult result = mvc.perform(
                post("/api/bot-order/add")
                        .content(JsonUtil.toJsonFormat(botOrderInfo))
                        .contentType(MediaType.APPLICATION_JSON)).andReturn();
        //then
        assertThat(result).isNotNull();
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).isEqualTo(botOrderInfo.getId().toString());

    }
    @Test
    void testAddHasError() throws Exception {
        //given
        BotOrderInfo botOrderInfo = new BotOrderInfo(null,"oldOrder", "ab23423k","1.0", 12.4, 14.1, 11.1, BotOrderStatus.ACTIVE,"p1","");
        when(this.botOrderInfoService.addNewBotOrderInfo(any()))
                .thenReturn(Mono.error(new EntityValidationException("botOrder","ERROR in entity")));
        //when
        MvcResult result = mvc.perform(
                post("/api/bot-order/add")
                        .content(JsonUtil.toJsonFormat(botOrderInfo))
                        .contentType(MediaType.APPLICATION_JSON)).andReturn();
        //then
        assertThat(result).isNotNull();
       System.out.println(result.getResponse().getHeader("error"));
        assertThat(result.getResponse().getStatus()).isNotEqualTo(200);


    }
    @Test
    void testClearSuccessful() throws Exception {
        //given
        Long countOfDeletedRows=10l;
        BotOrderInfo botOrderInfo = new BotOrderInfo(21l,"oldOrder", "ab23423k","1.0", 12.4, 14.1, 11.1, BotOrderStatus.ACTIVE,"p1","");
        when(this.botOrderInfoService.clear())
                .thenReturn(Mono.just(countOfDeletedRows));
        //when
        MvcResult result = mvc.perform(
                get("/api/bot-order/clear")
                        .contentType(MediaType.APPLICATION_JSON)).andReturn();
        //then
        assertThat(result).isNotNull();
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).isEqualTo(countOfDeletedRows+" records deleted.");

    }
    @Test
    void testListSuccessful() throws Exception {
        //given
        List<BotOrderInfo> records=new ArrayList<>();
        records.add(new BotOrderInfo());
        records.add(new BotOrderInfo());
    when(this.botOrderInfoService.findAll()).thenReturn(Mono.just(records));
        //when
        MvcResult result = mvc.perform(
                get("/api/bot-order/list")
                        .contentType(MediaType.APPLICATION_JSON)).andReturn();
        //then
        assertThat(result).isNotNull();
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).isNotNull();

    }
}