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
public class OutInterceptors extends Interceptor {
public OutInterceptors(){super();}
    public OutInterceptors(Bean inlineBean) {
        super(inlineBean);
    }

    public OutInterceptors(HandlerRef beanReference) {
        super(beanReference);
    }

    @Override
    public Direction getDirection() {
        return Direction.OUT;
    }

}
