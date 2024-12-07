package core.component;

import java.util.List;

public abstract class  component implements Comparable<component> {
    public GameObject gameObject = null ;

    private String Name;

    public void SetName(String Name)
    {
        this.Name = Name;
    }
    
    public String getName() 
    {
        return Name;
    }


    public abstract void start( );
 
    public abstract  void update(float dt );

}
