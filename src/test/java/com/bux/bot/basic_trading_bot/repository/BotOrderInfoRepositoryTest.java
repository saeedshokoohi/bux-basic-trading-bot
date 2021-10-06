package com.bux.bot.basic_trading_bot.repository;

import com.bux.bot.basic_trading_bot.entity.BotOrderInfo;
import com.bux.bot.basic_trading_bot.entity.enums.BotOrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
class BotOrderInfoRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BotOrderInfoRepository botOrderInfoRepository;

    @Test
    void findByStatus() {
        //given
        BotOrderStatus statusToFind = BotOrderStatus.OPEN;
        List<BotOrderInfo> sampleData= getSampleData();
        sampleData.forEach(this.entityManager::persist);
        this.entityManager.flush();
        //when
        List<BotOrderInfo> actualResult = botOrderInfoRepository.findByStatus(statusToFind);
        //then
        assertThat(actualResult).matches(elements -> elements.stream().allMatch(bo->bo.getStatus().equals(statusToFind)));


    }
    @Test
    void findByStatusIn() {
        //given
        List<BotOrderStatus> statusesToFind = new ArrayList<>();
        statusesToFind.add(BotOrderStatus.OPEN);
        statusesToFind.add(BotOrderStatus.ACTIVE);

        List<BotOrderInfo> sampleData= getSampleData();
        sampleData.forEach(this.entityManager::persist);
        this.entityManager.flush();
        //when
        List<BotOrderInfo> actualResult = botOrderInfoRepository.findByStatusIn(statusesToFind);
        //then
        assertThat(actualResult).matches(elements -> elements.stream().allMatch(bo->{
            return statusesToFind.contains(bo.getStatus());
        }));


    }

    private List<BotOrderInfo> getSampleData() {
        List<BotOrderInfo> sampleData=new ArrayList<>();
        sampleData.add(new BotOrderInfo(null,"oldOrder", "ab23423k","1.0", 12.4, 14.1, 11.1, BotOrderStatus.ACTIVE,"p1",""));
        sampleData.add(new BotOrderInfo(null,"oldOrder2", "ab23423k","1.0", 12.4, 14.1, 11.1, BotOrderStatus.OPEN,"p2",""));
        sampleData.add(new BotOrderInfo(null,"oldOrder3", "ab23423k","1.0", 12.4, 14.1, 11.1, BotOrderStatus.CLOSED,"p3",""));
        sampleData.add(new BotOrderInfo(null,"oldOrder4", "23234","1.0", 12.4, 14.1, 11.1, BotOrderStatus.ACTIVE,"p4",""));
        return sampleData;
    }
}