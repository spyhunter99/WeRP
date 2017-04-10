/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.spyhunter99.werp.beans;

/**
 *
 * @author AO
 */
public class InInterceptors extends Interceptor {

    public InInterceptors(){super();}
    public InInterceptors(Bean inlineBean) {
        super(inlineBean);
    }

    public InInterceptors(HandlerRef beanReference) {
        super(beanReference);
    }

    @Override
    public Direction getDirection() {
        return Direction.IN;
    }

}
