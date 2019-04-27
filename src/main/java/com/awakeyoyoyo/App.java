package com.awakeyoyoyo;

import com.awakeyoyoyo.bean.A;
import com.awakeyoyoyo.bean.B;
import com.awakeyoyoyo.config.Bean;
import com.awakeyoyoyo.config.parsing.ConfigurationManager;
import com.awakeyoyoyo.core.BeanFactory;
import com.awakeyoyoyo.core.ClassPathXmlApplicationContext;

import java.util.Map;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
//        Map<String,Bean> beanCofig= ConfigurationManager.getBeanConfig("/applicationContext.xml");
//        for (Map.Entry<String,Bean> e:beanCofig.entrySet()){
//            System.out.println(e.getKey()+":"+e.getValue());
//        }

        BeanFactory ac = new ClassPathXmlApplicationContext("/applicationContext.xml");//单例的会先注册进去
        A a = (A) ac.getBean("A");
        A a1 = (A) ac.getBean("A");
        B b = (B) ac.getBean("B");
        B b1 = (B) ac.getBean("B");
        System.out.println(a.getB());
        System.out.println("a==a1 : "+(a==a1));
        System.out.println("b==b1 : "+(b==b1));

    }
}
