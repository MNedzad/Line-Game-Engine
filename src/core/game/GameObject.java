package core.game;

import java.util.ArrayList;
import java.util.List;

import core.component.component;

public class GameObject 
{
   private List<component> Obj;

    public GameObject()
    {
        Obj = new ArrayList<>();
    }


    public void addObject(component object)
    {
        Obj.add(object);
    }

    public void Start()
    {
        for (int i = 0; i < Obj.size(); i++) 
        {
            Obj.get(i).start();
        }
    }
    public void Update(float dt)
    {
        for (int i = 0; i < Obj.size(); i++) 
        {
            Obj.get(i).update(dt);;
        }
    }
}
