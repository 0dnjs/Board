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
public class ArgsAop {

    @Pointcut("@annotation(com.korit.board.aop.annotation.ArgsAop)")
    private void pointCut() {}

        @Around("pointCut()")
        // Around는 ProceedingJoinPoint를 첫번째 파라미터로 전달받는데 해당 인터페이스가 프록시대상 객체를 호출할 수 있는 proceed()메서드를 제공
        // ProceedingJoinPoint : 핵심기능을 호출하는 기능을 가지고 있음 (around  사용 시 필수!)
        public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{

        //  getSignature() : 호출되는 메서드에 대한 정보를 구함
            Signature signature = proceedingJoinPoint.getSignature();
            CodeSignature codeSignature = (CodeSignature) signature;

            String className = codeSignature.getDeclaringTypeName(); // 클래스명
            String methodName = codeSignature.getName(); // 메소드명
            System.out.println("=====================================================");
            System.out.println("클래스명: " + className + ", 메소드명: " + methodName);
            for(Object arg : proceedingJoinPoint.getArgs()) {
                //get.Args() : 메서드 아규먼트 반환
                System.out.println(arg);
            }
            System.out.println("=====================================================");

            Object target = proceedingJoinPoint.proceed(); // 핵심 기능 호출

            return proceedingJoinPoint.proceed();
        }
}

