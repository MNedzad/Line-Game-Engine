package core.game;




import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import core.game.Colliders.Box;
import core.rendering.Mesh;
import core.rendering.Renderable;
import core.component.Shade;
import core.component.Sprite;
import core.component.Texture2D;
import core.component.component;
import glm_.vec2.Vec2;





public class ParticleSystem extends Renderable
{   
    public final float Tick = 1000;
    List<Particle> particles;
    Texture2D texture;
    Sprite sprite;
    Vec2 size ;
    Vec2 Pos;
    public Particle particle = new Particle();
    Shade shade;
    float time[] = new float[4];
    Random random = new Random();
    @Override
    public int compareTo(component o) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'compareTo'");
    }
    public void setTexture(Texture2D texture)
    {
       this.texture = texture;
    }
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }
    public void setShade(Shade shade)
    {
        this.shade = shade;
    }
    public Mesh getMesh()
    {
        return particle.mesh;
    }
    @Override
    public void start() 
    {
        particles= new ArrayList<>();



        
    }   
    public void Draw()
    {
        
        
         for (int i = 0; i < particles.size(); i++) 
         {
            Particle particle = particles.get(i); 
       


            particle.mesh.setPosition(particle.Position.getX(), particle.Position.getY());
            particle.mesh.Draw();       
         }
        
         

  
        
    }
    @Override
    public void update(float dt) 
    {  
        size = this.gameObject.getComponent(Collider.class).getShape().Size;
        Pos = this.gameObject.getPosition();

        Duration(dt);

        Add(dt);
    }
    private void Add(float dt)
    {
        while(time[0] >= 0)
        {
            time[0] -= dt; 
            return;
        }
        time[0] = 1f * (Tick);
      
        for (int i = 0; i < 1; i++) 
        {
            particle = new Particle();
            float minX = Pos.getX();
            float maxX = Pos.getX() +( size.getX() / 2 );
            float minY = Pos.getY() - (size.getY() / 2);
            float maxY = Pos.getY() ;
          
            float posX = random.nextInt(Math.round((maxX - minX + 1) + minX));
            float posY =  random.nextInt(Math.round( maxY + 9)) + minY;
            System.out.println(minY + " " + maxY + " " + posY);
            particle.mesh = new Mesh(texture);
            particle.mesh.setShade(shade);
         
            particle.mesh.setShape(new Box(8,8));
            particle.mesh.setSprite(sprite);
            particle.Position = new Vec2(posX, posY);
            particle.mesh.gameObject = this.gameObject;
            
            particle.mesh.start();
        
            particles.add(particle);
        }
    
        
   
    }
    private void Duration(float dt)
    {
        while(time[1] >= 0)
        {
            time[1] -= dt; 
            return;
        }
        time[1] = 1f * (Tick);
        System.out.println("One Second");
        for (int i = 0; i < particles.size(); i++) {
            particles.get(i).decreas(1);

            if(particles.get(i).duration <= 0)
                particles.remove(i);
        }
    
    }
}
 class Particle
{
    Mesh mesh;
    float duration = 3;
    Vec2 Position;
    int size;
    public void Particle(Vec2 size, float duration)
    {

    }
    public void setDuration(float time)
    {

    }
    public void decreas(float time)
    {
        duration = duration - time;
    }
    public Mesh getMesh()
    {
        return mesh;
    }
}