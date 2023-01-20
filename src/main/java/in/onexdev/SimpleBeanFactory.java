package in.onexdev;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SimpleBeanFactory implements BeanFactory{
    private Map<String,Bean> beanContainer;
    private static volatile SimpleBeanFactory beanFactory;
    SimpleBeanFactory(){
        this.beanContainer = new HashMap<>();
    }
    @Override
    public void registerBean(String name,Object instance){
        Bean bean = new Bean(instance);
        beanContainer.put(name, bean);
    }
    public Bean getBean(String beanName) {
       Bean bean = Optional.ofNullable(beanContainer.get(beanName))
               .orElseThrow(()->new RuntimeException("Bean not found " + beanName));
       return bean;
    }
}
