package jp.takeuchi.hideki.kagefumase;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by OWNER on 2017/02/27.
 */

public class Enemy extends GameObject{

    // 横幅、高さ 敵のサイズはUFOと同じとする
    public static final float ENEMY_WIDTH = 2.0f;
    public static final float ENEMY_HEIGHT = 1.3f;

    //状態（逃げているのと捕まった状態)
     public static final int ENEMY_TYPE_MOVING = 0;
     public static final int ENEMY_TYPE_CAUGHT = 1;

    // 速度
    public static final float ENEMY_VELOCITY = 2.0f;

    int mState;

    public Enemy(Texture texture, int srcX, int srcY, int srcWidth, int srcHeight) {
        super(texture, srcX, srcY, srcWidth, srcHeight);
        setSize(ENEMY_WIDTH, ENEMY_HEIGHT);
        mState = ENEMY_TYPE_MOVING;

    }

    // TODO
    public void update(float deltaTime){
        if (mState == ENEMY_TYPE_MOVING){
            //TODO キャラを動かして逃げる処理


        }

    }

    // TODO
    public void chatched(){

        mState = ENEMY_TYPE_CAUGHT;
        setAlpha(0);
    }
}
