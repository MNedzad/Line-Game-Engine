package core.events;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

import static org.lwjgl.system.MemoryUtil.NULL;

import core.window.Window;
import core.component.component;
import glm_.vec2.Vec2;



public class CharacterController extends component implements  KeyInput  {

    int Speed;
    Vec2 Velocity;
    int speed;
    int maxVelocity = 20;
    boolean isGround;

    public CharacterController(int speed)
    {
        this.speed = speed;
    }
    @Override
    public void start() {

        Velocity = new Vec2(0, 0 );
    }
    public void platformMove(Float deltaTime)
    {
        if(getKeys(GLFW_KEY_D))
        {
          
            if(Velocity.getX() > -maxVelocity)
            {
         
                Velocity = Velocity.plus(speed / deltaTime, 0);
            }
                
        }
        if(getKeys(GLFW_KEY_A))
        {
            if(Velocity.getX() < maxVelocity)
                Velocity.setX(Velocity.getX() -15 / deltaTime);
        }

        
    }

    
    @Override
    public void update(float dt) 
    {
        
        if(Velocity.getX() > 0 || Velocity.getX() < -1)
        {
            
            float posX = gameObject.getPosition().getX() + Velocity.getX() ;
            gameObject.SetPosition(posX, NULL);
        
        }
            


        this.SetListener(Window.window);
        platformMove(dt);

        
        if(Velocity.getX() > 0)
        {
            Velocity.setX( Velocity.getX() - 5 / dt);
        }
        else if(Velocity.getX() < -1)
        {
            Velocity.setX( Velocity.getX() + 5 / dt);
        }
        else if(Velocity.getX() < 5 && Velocity.getX() > -5 )
        {
            Velocity.setX(0);
        }
    }
    @Override
    public int compareTo(component o) {
        return 0;
    }


    
}
