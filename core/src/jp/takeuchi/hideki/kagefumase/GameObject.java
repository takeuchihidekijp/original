package jp.takeuchi.hideki.kagefumase;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by OWNER on 2017/02/24.
 */

public class GameObject extends Sprite {
    public final Vector2 velocity;  // 敵のx方向、y方向の速度を保持する
    public final Vector2 velocity_c;  // 車のx方向、y方向の速度を保持する


    public GameObject(Texture texture, int srcX, int srcY, int srcWidth, int srcHeight) {
        super(texture, srcX, srcY, srcWidth, srcHeight);

        velocity = new Vector2();
        velocity_c = new Vector2();
    }

    public Vector2 GetPosition(){
        return new Vector2(this.getX(), this.getY());
    }

    public void SetPosition( Vector2 pos ) {
        this.setPosition( pos.x, pos.y );
    }
}
