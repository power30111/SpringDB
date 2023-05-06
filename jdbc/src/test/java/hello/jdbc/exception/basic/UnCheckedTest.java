package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UnCheckedTest {

    @Test
    void unchecked_catch(){
        Service service = new Service();
        service.callCatch();
    }
    @Test
    void unchecked_throw(){
        Service service = new Service();
        Assertions.assertThatThrownBy(() -> service.callThrow())
                .isInstanceOf(MyUncheckedException.class);
    }

    /**
     * RuntimeException을 상속받은 예외는 언체크 예외가 된다.
     */
    static class MyUncheckedException extends RuntimeException{
        public MyUncheckedException(String message) {
            super(message);
        }
    }

    /**
     * Unchecked 예외는    (Runtime)
     * 예외를 잡거나 던질수있다    (Check와 달리 필수x)
     * 예외를 잡지않아도 자동으로 밖으로 던져진다.
     */

    static class Service {
        Repository repository = new Repository();

        /**
         * 필요한 경우 예외를 잡아서 처리한다.
         */
        public void callCatch(){
            try {
                repository.call();
            }catch (MyUncheckedException e){
                //예외 처리 로직
                log.info("예외 처리 message={}",e.getMessage(),e);
            }
        }

        /**
         * 예외를 잡지않아도 됨. ex) 체크예외의 경우 Throw선언을 해야만함
         */
        public void callThrow() {
            repository.call();
        }
    }

    static class Repository{
        public void call(){
            throw new MyUncheckedException("ex");
        }
    }
}
