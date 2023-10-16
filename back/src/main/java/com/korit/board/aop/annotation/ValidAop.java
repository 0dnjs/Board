package com.korit.board.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 런타임, 프로그램 실행중에
@Target(ElementType.METHOD) // 이 메소드를 읽어들여라
public @interface ValidAop {

}
