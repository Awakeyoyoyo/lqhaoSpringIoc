package com.awakeyoyoyo.core;

import com.awakeyoyoyo.config.Bean;
import com.awakeyoyoyo.config.Property;
import com.awakeyoyoyo.config.parsing.ConfigurationManager;
import org.apache.commons.beanutils.BeanUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassPathXmlApplicationContext implements BeanFactory {
    // 存放配置文件信息
    private Map<String, Bean> config;
    // 存放bean对象的容器
    private Map<String, Object> context = new HashMap<>();


    public ClassPathXmlApplicationContext(String path) {
        // 读取配置文件中bean的信息
        config = ConfigurationManager.getBeanConfig(path);
        // 遍历初始化bean
        if (config != null) {
            for (Map.Entry<String, Bean> e : config.entrySet()) {
                // 获取bean信息
                String beanName = e.getKey();
                Bean bean = e.getValue();
                // 如果设置成单例的才创建好bean对象放进容器中
                if (bean.getScope().equals(Bean.SINGLETON)) {
                    Object beanObj = createBeanByConfig(bean);
                    context.put(beanName, beanObj);
                }
            }

        }
    }
    private Object createBeanByConfig(Bean bean) {
        // 根据bean信息创建对象
        Class clazz = null;
        Object beanObj = null;
        try {
            //以下用到一些反射知识
            clazz = Class.forName(bean.getClassName());

            // 创建bean对象
            beanObj = clazz.newInstance();
            // 获取bean对象中的property配置
            List<Property> properties = bean.getProperties();
            // 遍历bean对象中的property配置,并将对应的value或者ref注入到bean对象中
            for (Property prop : properties) {
                if (prop.getValue() != null) {
                    // 将value值注入到bean对象中  根据变量名字 注入值。
                    BeanUtils.setProperty(beanObj, prop.getName(),prop.getValue());
                } else if (prop.getRef() != null) {
                    Object ref = context.get(prop.getRef()); //直接容器里面 beanname 找实例对象
                    // 如果依赖对象还未被加载则递归创建依赖的对象
                    if (ref == null) {
                        ref = createBeanByConfig(config.get(prop.getRef())); //根据类名从bean信息里面根据beanname获取该bean信息 然后新建一个bean对象
                    }
                    // 将ref对象注入bean对象中
                    BeanUtils.setProperty(beanObj, prop.getName(),ref);
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            throw new RuntimeException("创建" + bean.getClassName() + "对象失败");
        }
        return beanObj;
    }

    @Override
    public Object getBean(String name) {
        Bean bean = config.get(name);
        Object beanObj = null;
        if (bean.getScope().equals(Bean.SINGLETON)) {
            // 如果将创建bean设置成单例则在容器中找
            beanObj = context.get(name);
        } else if (bean.getScope().equals(Bean.PROTOTYPE)) {
            // 如果是prototype则新创建一个对象
            beanObj = createBeanByConfig(bean);
        }
        return beanObj;
    }
}
