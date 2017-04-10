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
public class InFaultInteceptors extends Interceptor {
public InFaultInteceptors(){super();}
    public InFaultInteceptors(Bean inlineBean) {
        super(inlineBean);
    }

    public InFaultInteceptors(HandlerRef beanReference) {
        super(beanReference);
    }

    @Override
    public Direction getDirection() {
        return Direction.IN_FAULT;
    }

}
