/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia.tools;

import java.util.Iterator;

/**
 *
 * @author kaisky89
 */
public class SmartIterable<T> implements Iterable<T>{
    
    private Iterator<T> iterator;
    
    private SmartIterable(){
    }
    
    public SmartIterable(Iterator iterator){
        this.iterator = iterator;
    }

    @Override
    public Iterator<T> iterator() {
        return iterator;
    }
    
}
