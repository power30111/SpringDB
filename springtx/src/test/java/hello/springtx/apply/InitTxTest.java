package hello.springtx.apply;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringBootTest
public class InitTxTest {

    @Test
    void go(){
        //초기화 코드는 스프링이 초기화 시점에 실행한다.    (직접호출시에는 트랜잭션 적용)
    }
    @TestConfiguration
    static class InitTxTestConfig{
        @Bean
        Hello hello(){
            return new Hello();
        }
    }

    @Slf4j
    static class Hello{
        @PostConstruct
        @Transactional
        public void initV1(){
            boolean active = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("hello init @PostConstruct tx active={}",active);
        }
        @EventListener(ApplicationReadyEvent.class) //스프링 컨테이너가 준비완료된 뒤 호출
        @Transactional
        public void initV2(){
            boolean active = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("hello init ApplicationReadyEvent tx active={}",active);
        }
    }
}
