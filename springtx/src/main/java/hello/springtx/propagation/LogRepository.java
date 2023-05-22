package hello.springtx.propagation;


import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LogRepository {

    private final EntityManager em;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(Log logMessage){
        log.info("log 저장");
        em.persist(logMessage);
        
        //만일 로그메세지중에서 로그예외라는 문자열이 있다면
        if(logMessage.getMessage().contains("로그예외")){
            log.info("log 저장시 예외 발생");
            throw new RuntimeException("예외 발생");
        }
    }
    public Optional<Log> find(String message){
        //만일 결과가 두개라면 그중 하나만 반환. 만약 값이 없으면 Optional 이니 ㄱㅊㅊ
        return em.createQuery("select l from Log l where l.message = :message",
                        Log.class)
                .setParameter("message",message)
                .getResultList().stream().findAny();
    }
}
