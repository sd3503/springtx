package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    LogRepository logRepository;

    /**
     * memberService    @Transactional:Off
     * memberRepository @Transactional:On
     * logRepository    @Transactional:On
     */

    @Test
    void outerTxOff_success(){
        //given
        String username = "outerTxOff_success";
        //when
        memberService.joinV1(username);
        //then
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService    @Transactional:Off
     * memberRepository @Transactional:On
     * logRepository    @Transactional:On Exception
     */

    @Test
    void outerTxOff_fail(){
        //given
        String username = "로그예외outerTxOff_fail";
        //when
        assertThatThrownBy(()->memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);
        //then
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * memberService    @Transactional:On
     * memberRepository @Transactional:Off
     * logRepository    @Transactional:Off
     */

    @Test
    void singleTx(){
        //given
        String username = "singleTx";
        //when
        memberService.joinV1(username);
        //then
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService    @Transactional:On
     * memberRepository @Transactional:On
     * logRepository    @Transactional:On
     */

    @Test
    void outerTxOn_success(){
        //given
        String username = "outerTxOn_success";
        //when
        memberService.joinV1(username);
        //then
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isPresent());
    }
}
