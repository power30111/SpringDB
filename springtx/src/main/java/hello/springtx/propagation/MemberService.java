package hello.springtx.propagation;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final LogRepository logRepository;

    public void joinV1(String username){
        Member member = new Member(username);
        Log logMessage = new Log(username);

        //서로 save메서드에는 @Transaction이 적용되고있다.
        log.info("== memberRepository 호출 시작 ==");
        memberRepository.save(member);
        log.info("== memberRepository 호출 종료 ==");
        
        log.info("== logRepository 호출 시작 ==");
        logRepository.save(logMessage);
        log.info("== logRepository 호출 종료 ==");

        //회원과 DB로그를 함께 남기는 비지니스 로직
        //log에서 예외가 터지면 Runtime예외가 올라오게된다.
        
    }
    public void joinV2(String username){
        //
        Member member = new Member(username);
        Log logMessage = new Log(username);

        //서로 save메서드에는 @Transaction이 적용되고있다.
        log.info("== memberRepository 호출 시작 ==");
        memberRepository.save(member);
        log.info("== memberRepository 호출 종료 ==");

        log.info("== logRepository 호출 시작 ==");
        try {
            logRepository.save(logMessage);
        }catch (RuntimeException e){
            log.info("로그 저장에 실패했습니다. logMessage = {}",logMessage.getMessage());
            log.info("정상 흐름 반환");
        }
        log.info("== logRepository 호출 종료 ==");

        //DB로그 저장시 예외가 발생하면 예외를 복구한다.   (그냥 log저장실패 출력하고 넘기는듯)
        //V2에서는 log에서 Runtime예외가 올라와도 그거 잡아서 정상흐름으로 반환한다.
    }
}
