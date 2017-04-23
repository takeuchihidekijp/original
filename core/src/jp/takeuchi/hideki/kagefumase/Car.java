package jp.takeuchi.hideki.kagefumase;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by OWNER on 2017/03/08.
 */

public class Car extends GameObject {
    // 横幅、高さ(いったんLesson７の星に合わせる
    public static final float CAR_WIDTH = 1.0f;
    public static final float CAR_HEIGHT = 1.0f;

    // 速度
    public static final float CAR_VELOCITY = 2.0f;

    public Car(Texture texture, int srcX, int srcY, int srcWidth, int srcHeght){
        super(texture, srcX, srcY, srcWidth, srcHeght);
        setSize(CAR_WIDTH,CAR_HEIGHT);
        velocity_c.y = CAR_VELOCITY;
    }

    // 座標を更新する
    public void update(float deltaTime) {

        setY(getY() - velocity_c.y * deltaTime);

        }

}
