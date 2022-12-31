package in.onexdev;

public class Bean {
    public String className;
    public Object instance;

    Bean(Object instance){
        this.className = instance.getClass().getName();
        this.instance = instance;
    }
}
