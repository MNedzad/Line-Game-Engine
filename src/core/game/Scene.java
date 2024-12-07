package core.game;



import java.util.ArrayList;
import java.util.List;

import core.rendering.Renderable;
import core.component.GameObject;
import core.editor.Gui;

public class Scene  {
    public enum State {Play, Pause, Stop};
    List<GameObject> GameObject = new ArrayList<>();
    List<Collider> Collider = new ArrayList<>(); 
    List<Renderable> Renderer = new ArrayList<>(); 
    Camera cam;

    State state;

    float time;

    public void setStatePlay()
    {
        if(state == State.Play)
        {
            state = State.Stop;
        }else
        {
            state = State.Play;
        }
    }

    public List<Collider> getCollider() 
    {
      
       return Collider;
    }
    public void setCam(Camera cam) {
        this.cam = cam;
    }
    public Camera getCam() {
        return cam;
    }
    public void AddGameObject(GameObject  object)
    {
        this.GameObject.add(object);
    }
    public void AddCollider(GameObject  collider)
    {
        this.Collider.add(collider.getComponent(Collider.class));
    }
    public void addToRender(GameObject Render) 
    {
  
        Renderer.add(Render.getComponent(Renderable.class));
        
    }
    public List<GameObject> getGameObject() {
        return GameObject;
    }
    public void Start()
    {
        state = State.Stop;
        GameObject.forEach(e ->
        {
            e.start(1);
        });
    }
    public void Update(float dt)
    {
        if(state == State.Stop ) return;
        
        GameObject.forEach(e ->
        {
        
            e.update(dt);
        });
      
    }
    public boolean isOnScene(GameObject GameO)
    {
       for (int i = 0; i <GameObject.size(); i++) 
       {
            
            if(GameO.equals(GameObject.get(i)))
            {
                return false;
            }
       }
       return true;
    }
    public void DrawFromRender(float dt)
    {   
        GameObject.forEach(e ->
        {
           e.draw(dt);
            
        });
    }


}
