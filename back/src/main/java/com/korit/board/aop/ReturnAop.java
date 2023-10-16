package com.korit.board.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ReturnAop {

    @Pointcut("@annotation(com.korit.board.aop.annotation.ReturnAop)")
    private void pointCut(){}

    @Around("pointCut()")
    // Around는 ProceedingJoinPoint를 첫번쨰 파라미터로 전달받는데 해당 인터페이스가 프록시대상 객체를 호출할 수 있는 proceed()메서드를 제공
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Object target = proceedingJoinPoint.proceed();

        Object[] args = proceedingJoinPoint.getArgs();
        for(Object arg : proceedingJoinPoint.getArgs()) {
            //get.Args() : 메서드 아규먼트 반환
            System.out.println(arg);
        }

        return target;
    }
}
