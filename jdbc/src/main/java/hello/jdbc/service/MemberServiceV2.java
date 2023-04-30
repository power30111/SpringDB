package hello.jdbc.service;


import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 개념 도입, 파아미터 연동, 풀을 고려한 종료
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV2 {

    private final MemberRepositoryV2 memberRepository;
    private final DataSource dataSource;
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        Connection con = dataSource.getConnection();
        try {
            con.setAutoCommit(false);   //auto commit 끔으로써 트랜잭션 시작.
            //비즈니스 로직 수행
            bizLogic(con, fromId, toId, money);
            con.commit();       //만약 정상적으로 동작하였다면 commit ;
        }catch (Exception e){
            //만일 도중 예외가 발생한다면 rollback
            con.rollback();
            throw new IllegalStateException(e);
        }finally {
            release(con);
        }

    }

    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con,fromId, fromMember.getMoney()- money);
        validation(toMember);
        memberRepository.update(con,toId,toMember.getMoney()+ money);
    }


    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외가 발생하였습니다.");
        }
    }

    private void release(Connection con) {
        if (con != null){
            try{
                con.setAutoCommit(true);    //커넥션 풀 고려
                con.close();                //커넥션 풀에 반환
            }catch (Exception e){
                log.info("error",e);
            }
        }
    }
}
