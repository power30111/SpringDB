package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired  MemberRepository memberRepository;
    @Autowired LogRepository logRepository;

    /**
     * memberService        @Transaction : OFF
     * memberRepository     @Transaction : ON
     * logRepository        @Transaction : ON
     */
    @Test
    void outerTxOff_success(){
        //given
        String username = "outerTxOff_success";

        //when  모든 데이터가 정상 저장되는 경우
        memberService.joinV1(username);

        //Then  memberRepository와 logRepository는 정상 동작한다.
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
        //지금까지 사용하던 assert가 아닌 JUnit에 있는 기능으로써 존재하는지에 대한 메서드
    }

    /**
     * memberService        @Transaction : OFF
     * memberRepository     @Transaction : ON
     * logRepository        @Transaction : ON -> Exception(Runtime)
     */
    @Test
    void outerTxOff_fail(){
        //given
        String username = "로그예외 outerTxOff_fail";

        //when  logRepository에서 예외가 터짐
        assertThatThrownBy(() ->
                memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        //Then  memberRepository는 정상동작하지만 logRepository는 예외가 발생하여 동작하지않는다.
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());
        //logRepository-> RuntimeExcpetion
    }
    /**
     * memberService        @Transaction : ON
     * memberRepository     @Transaction : OFF
     * logRepository        @Transaction : OFF
     * 이 경우 memberRepo, logRepo 둘다 @Transaction을 주석처리 해주었다.
     */
    @Test
    void singleTx(){
        //given
        String username = "singleTx";

        //when  모든 데이터가 정상 저장되는 경우
        memberService.joinV1(username);

        //Then  memberRepository와 logRepository는 정상 동작한다.
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
        //지금까지 사용하던 assert가 아닌 JUnit에 있는 기능으로써 존재하는지에 대한 메서드
    }
    /**
     * memberService        @Transaction : ON
     * memberRepository     @Transaction : ON
     * logRepository        @Transaction : ON
     * 트랜잭션 전파
     */
    @Test
    void outerTxOn_success(){
        //given
        String username = "outerTxOn_success";

        //when  모든 데이터가 정상 저장되는 경우
        memberService.joinV1(username);

        //Then  memberRepository와 logRepository는 정상 동작한다.
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }
    /**
     * memberService        @Transaction : ON
     * memberRepository     @Transaction : ON
     * logRepository        @Transaction : ON -> Exception(Runtime)
     */
    @Test
    void outerTxOn_fail(){
        //given
        String username = "로그예외 outerTxOn_fail";

        //when  logRepository에서 예외가 터짐
        assertThatThrownBy(() ->
                memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        //Then 트랜잭션 전파로 인해 logRepo가 예외가 터져서 Rollback되므로
        //memberRepo또한 Rollback된다. 그러므로 Empty
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }
    /**
     * memberService        @Transaction : ON
     * memberRepository     @Transaction : ON
     * logRepository        @Transaction : ON -> Exception(Runtime)
     */
    @Test
    void recoverException_fail(){
        //given
        String username = "로그예외_recoverException_fail";

        //when  logRepository에서 예외가 터짐
        //joinV2는 예외를 잡아서 처리하도록 만든 로직이다.
        assertThatThrownBy(() ->
                memberService.joinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);

        //예외를 잡아서 정상흐름으로 복구할수있을까? rollback-only가 걸려있어서 rollback된다.
        //logRepo에서 올라오는 예외를 처리했기때문에 MemberService에서는 commit을 수행하려하지만,
        //이미 rollback-only가 걸려있기때문에 UnexpectedRollbackException 예외가 나오게된다.
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }
    /**
     * memberService        @Transaction : ON
     * memberRepository     @Transaction : ON
     * logRepository        @Transaction : ON(ReQUIRES_NEW) -> Exception(Runtime)
     */
    @Test
    void recoverException_success(){
        //given
        String username = "로그예외_recoverException_success";

        //when  logRepository에서 예외가 터짐
        memberService.joinV2(username);
        //Then: member 저장, log rollback
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());

        //logRepository 에는 REQUIRES_NEW 옵션을 넣어주었기 떄문에 logRepo에서 터진것과는
        //관게없이 memberRepository는 commit된다.
    }
}