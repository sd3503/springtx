package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.sql.DataSource;

@Slf4j
@SpringBootTest
public class BasicTxTest {

    @Autowired
    PlatformTransactionManager txManager;

    @TestConfiguration
    static class Config {
        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }

    @Test
    void commit() {
        log.info("트랜젝션 시작");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜젝션 커밋 시작");
        txManager.commit(status);
        log.info("트랜젝션 커밋 완료");
    }
    @Test
    void rollback() {
        log.info("트랜젝션 시작");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜젝션 롤백 시작");
        txManager.rollback(status);
        log.info("트랜젝션 롤백 완료");
    }
    @Test
    void double_Commit () {
        log.info("트랜젝션1 시작");
        TransactionStatus status1 = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜젝션1 커밋 시작");
        txManager.commit(status1);
        log.info("트랜젝션1 커밋 완료");

        log.info("트랜젝션2 시작");
        TransactionStatus status2 = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜젝션2 커밋 시작");
        txManager.commit(status2);
        log.info("트랜젝션2 커밋 완료");
    }
    @Test
    void double_Commit_rollback () {
        log.info("트랜젝션1 시작");
        TransactionStatus status1 = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜젝션1 커밋 시작");
        txManager.commit(status1);
        log.info("트랜젝션1 커밋 완료");

        log.info("트랜젝션2 시작");
        TransactionStatus status2 = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜젝션2 롤백 시작");
        txManager.rollback(status2);
        log.info("트랜젝션2 롤백 완료");
    }

    @Test
    void inner_Commit () {
        log.info("외부 트렌잭션 시작");
        TransactionStatus outerStatus = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.inNewTransaction()={}", outerStatus.isNewTransaction());

        log.info("내부 트렌잭션 시작");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("inner.inNewTransaction()={}", inner.isNewTransaction());
        log.info("내부 트렌잭션 커밋 시작");
        txManager.commit(inner);

        log.info("외부 트렌잭션 커밋 시작");
        txManager.commit(outerStatus);
    }
    @Test
    void outer_rollback () {
        log.info("외부 트렌잭션 시작");
        TransactionStatus outerStatus = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("내부 트렌잭션 시작");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("내부 트렌잭션 커밋 시작");
        txManager.commit(inner);

        log.info("외부 트렌잭션 롤백 시작");
        txManager.rollback(outerStatus);
    }
    @Test
    void inner_rollback () {
        log.info("외부 트렌잭션 시작");
        TransactionStatus outerStatus = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("내부 트렌잭션 시작");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("내부 트렌잭션 롤백 시작");
        txManager.rollback(inner);

        log.info("외부 트렌잭션 롤백 시작");
//        txManager.commit(outerStatus);

        Assertions.assertThatThrownBy(() -> txManager.commit(outerStatus))
                .isInstanceOf(UnexpectedRollbackException.class);
    }
    @Test
    void inner_rollback_requires_new() {
        log.info("외부 트렌잭션 시작");
        TransactionStatus outerStatus = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.inNewTransaction()={}", outerStatus.isNewTransaction());

        log.info("내부 트렌잭션 시작");
        DefaultTransactionAttribute definition = new DefaultTransactionAttribute();
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus inner = txManager.getTransaction(definition);
        log.info("inner.inNewTransaction()={}", inner.isNewTransaction());

        log.info("내부 트렌잭션 롤백 시작");
        txManager.rollback(inner);
        log.info("외부 트렌잭션 커밋 시작");
        txManager.commit(outerStatus);
    }
}
