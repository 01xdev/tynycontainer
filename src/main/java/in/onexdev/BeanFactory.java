package in.onexdev;

public interface BeanFactory {
    public void registerBean(String name,Object instance);
    public Bean getBean(String beanName);
}
