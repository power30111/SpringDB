package hello.springtx.propagation;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;

@Slf4j
@SpringBootTest
public class BasicTxTest {

    @Autowired
    PlatformTransactionManager txManager;
    @TestConfiguration
    static class Config{
        //원래는 스프링부트가 트랜잭션 매니저도 등록해주지만 이렇게 수동으로 등록시 수동등록한것을 사용한다
        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource){
            return new DataSourceTransactionManager(dataSource);
        }
    }

    @Test
    void commit(){
        log.info("트랜잭션 시작");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());    // 가져오는순간부터 트랜잭션시작

        log.info("트랜잭션 커밋 시작");
        txManager.commit(status);   //트랜잭션 커밋
        log.info("트랜잭션 커밋 완료");
    }
    @Test
    void rollback(){
        log.info("트랜잭션 시작");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());    // 가져오는순간부터 트랜잭션시작

        log.info("트랜잭션 롤백 시작");
        txManager.rollback(status);   //트랜잭션 커밋
        log.info("트랜잭션 롤백 완료");
    }

    @Test
    void doubleCommit(){
        //같은 물리적 커넥션(conn0)을 사용하지만, 다른 가상 커넥션으로 가져가기떄문에 서로 다르다
        // (HikariProxyConnection@8812039)<--이 부분이 다르다
        log.info("트랜잭션1 시작");
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionDefinition());    // 가져오는순간부터 트랜잭션시작

        log.info("트랜잭션1 커밋");
        txManager.commit(tx1);   //트랜잭션 커밋

        log.info("트랜잭션2 시작");
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionDefinition());    // 가져오는순간부터 트랜잭션시작

        log.info("트랜잭션2 커밋");
        txManager.commit(tx2);   //트랜잭션 커밋
    }
    @Test
    void doubleCommitRollback(){
        //첫번째는 커밋하고 두번째는 롤백한다. 결국 다른 커넥션을 사용하므로 잘 동작한다.
        log.info("트랜잭션1 시작");
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionDefinition());    // 가져오는순간부터 트랜잭션시작

        log.info("트랜잭션1 커밋");
        txManager.commit(tx1);   //트랜잭션 커밋

        log.info("트랜잭션2 시작");
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionDefinition());    // 가져오는순간부터 트랜잭션시작

        log.info("트랜잭션2 커밋");
        txManager.rollback(tx2);   //트랜잭션 커밋
    }

    @Test
    void inner_commit(){
        //트랜잭션안에서 트랜잭션이 호출될경우
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionDefinition());
        log.info("outer.isNewTransaction()={}",outer.isNewTransaction());   //처음 수행된 트랜잭션인지 확인

        //트랜잭션이 이미 있는데, 내부에서 다시 트랜잭션 호출한 상황(commit까지 완료) 내부트랜잭션이 커밋되어야 외부 트랜잭션이 커밋될수있다.
        inner();

        log.info("외부 트랜잭션 커밋");
        txManager.commit(outer);

        //외부 트랜잭션과 내부 트랜잭션이 하나의 물리적 트랜잭션으로 묶인다.
        //내부 트랜잭션이 커밋되었을텐데 왜 로그가 안뜨고 외부 트랜잭션이 커밋되어야만 커밋되었다는 로그가 남을까?
        // --> 내부트랜잭션이 commit 을 해도 일을 하지않는다.
    }
    @Test
    void outer_rollback(){
        //내부 트랜잭션은 커밋했으나, 외부트랜잭션이 롤백한경우? 
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionDefinition());

        inner();

        log.info("외부 트랜잭션 롤백");
        txManager.rollback(outer);

        //결국 외부 트랜잭션이 마지막엔 rollback 을하면 내부에서 commit해도 결론적으론 rollback된다.
    }

    private void inner() {
        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionDefinition());
        log.info("inner.isNewTransaction()={}",inner.isNewTransaction());   //처음 수행된 트랜잭션인지 확인
        log.info("내부 트랜잭션 커밋");
        txManager.commit(inner);
    }

}
