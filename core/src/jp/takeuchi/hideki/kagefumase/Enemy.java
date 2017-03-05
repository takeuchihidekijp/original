package jp.takeuchi.hideki.kagefumase;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

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
    public static final float ENEMY_VELOCITY_X = 2.0f;
    public static final float ENEMY_VELOCITY_Y = 2.5f;

    //逃げ方（右に逃げるか左に逃げるかといったバリエーション)
    public static final int ENEMY_MOVING_TYPE_NORMAL = 0;
    public static final int ENEMY_MOVING_TYPE_UPPER = 1;
    public static final int ENEMY_MOVING_TYPE_LOWER = 2;
    public static final int ENEMY_MOVING_TYPE_RIGHT = 3;
    public static final int ENEMY_MOVING_TYPE_LEFT = 4;
    public static final int ENEMY_MOVING_TYPE_STOP = 5;

    int mState;
    Random mRandom;

    int mType;


    public Enemy(int type, Texture texture, int srcX, int srcY, int srcWidth, int srcHeight) {
        super(texture, srcX, srcY, srcWidth, srcHeight);
        setSize(ENEMY_WIDTH, ENEMY_HEIGHT);
        mState = ENEMY_TYPE_MOVING;
        velocity.x = ENEMY_VELOCITY_X;
        velocity.y = ENEMY_VELOCITY_Y;

        mRandom = new Random();

        mType = type;


    }

    // TODO
    public void update(float deltaTime){
        if (mState == ENEMY_TYPE_MOVING) {

            //TODO キャラを動かして逃げる処理
            switch (mType){
                case ENEMY_MOVING_TYPE_NORMAL:
                    setX(getX() + velocity.x * deltaTime);

                    if (getX() < ENEMY_WIDTH / 2) {
                        velocity.x = -velocity.x;
                        setX(ENEMY_WIDTH / 2);
                    }

                    if (getX() > GameScreen.WORLD_WIDTH - ENEMY_WIDTH / 2) {
                        velocity.x = -velocity.x;
                        setX(GameScreen.WORLD_WIDTH - ENEMY_WIDTH / 2);
                    }

                    if (mRandom.nextFloat() > 0.3f) {
                        setY(getY() + velocity.y * deltaTime);
                    }
                    break;

                case ENEMY_MOVING_TYPE_RIGHT:
                    setX(getX() + velocity.x * deltaTime*2);

                    if (getX() < ENEMY_WIDTH / 2) {
                        velocity.x = -velocity.x;
                        setX(ENEMY_WIDTH / 2);
                    }

                    if (getX() > GameScreen.WORLD_WIDTH - ENEMY_WIDTH / 2) {
                        velocity.x = -velocity.x;
                        setX(GameScreen.WORLD_WIDTH - ENEMY_WIDTH / 2);
                    }

                    if (mRandom.nextFloat() > 0.6f) {
                        setY(getY() + velocity.y * deltaTime);
                    }
                    break;
                case ENEMY_MOVING_TYPE_LEFT:
                    setX(getX() - velocity.x * deltaTime*2);

                    if (getX() < ENEMY_WIDTH / 2) {
                        velocity.x = -velocity.x;
                        setX(ENEMY_WIDTH / 2);
                    }

                    if (getX() > GameScreen.WORLD_WIDTH - ENEMY_WIDTH / 2) {
                        velocity.x = -velocity.x;
                        setX(GameScreen.WORLD_WIDTH - ENEMY_WIDTH / 2);
                    }

                    if (mRandom.nextFloat() > 0.6f) {
                        setY(getY() + velocity.y * deltaTime);
                    }
                    break;
            }

        }
    }

    // TODO
    public void chatched(){

        mState = ENEMY_TYPE_CAUGHT;
    //これがあると捕まえた敵が画面に表示されない。つまり後ろにつかない    setAlpha(0);
    }
}
