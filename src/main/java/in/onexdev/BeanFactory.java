package in.onexdev;

public interface BeanFactory {
    public void registerBean(String name,Object instance);
    public Object getBean(String beanName);
}
