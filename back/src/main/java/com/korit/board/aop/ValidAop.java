package com.korit.board.aop;

import com.korit.board.exception.ValidException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;

import java.beans.BeanProperty;
import java.util.HashMap;
import java.util.Map;

/**
 * AOP는 필터와 같은 역할을 한다
 * */

@Aspect // 메소드전용 필터
@Component
public class ValidAop {

//    @Pointcut("execution(* com.korit.board.controller.*.*(..))")
    @Pointcut("@annotation(com.korit.board.aop.annotation.ValidAop)")
    private void pointCut() {}

    @Around("pointCut()")/* 포인트컷 */
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{

        System.out.println("전처리");

        Object[] args = proceedingJoinPoint.getArgs();
        BeanPropertyBindingResult bindingResult = null;

        for(Object arg : args) {
            if(arg.getClass() == BeanPropertyBindingResult.class) {
                bindingResult = (BeanPropertyBindingResult) arg;
                break;
            }
        }

        if(bindingResult == null) {
            return proceedingJoinPoint.proceed();
        }

        if(bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                //   변수명                 에러메세지
                errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            throw new ValidException(errorMap);
        }


        // .proceed() 메소드가 호줄되기 전이 전처리
        Object target = proceedingJoinPoint.proceed(); // 메소드의 body
        // .proceed() 메소드가 리턴된 후가 후처리
        System.out.println("후처리");
        System.out.println(target);

        return target; // 메소드의 리턴값이 타겟
    };
}

