package pia.tools;

import java.util.Iterator;

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
