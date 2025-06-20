package org.example.extension;

/**
 * 一个泛型的线程安全值容器，它的作用是：在多线程之间安全地共享某个对象值（T）。
 * 懒加载（Lazy Loading）是一种设计模式，指的是：
 * 在需要使用对象时，才去创建或加载它，而不是一开始就创建。
 * @param <T>
 */
public class Holder<T>{
    private T value;
    public void setValue(T value) {this.value = value;}
    public T getValue() {return value;}

}
