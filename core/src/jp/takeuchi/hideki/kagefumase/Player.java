package jp.takeuchi.hideki.kagefumase;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by OWNER on 2017/02/24.
 */

public class Player extends GameObject{

    // 横幅、高さ
    public static final float PLAYER_WIDTH = 1.0f;
    public static final float PLAYER_HEIGHT = 1.0f;

    // 状態
    public static final int PLAYER_STATE_UPPER = 0;
    public static final int PLAYER_STATE_LOWER = 1;
    public static final int PLAYER_STATE_RIGHT = 2;
    public static final int PLAYER_STATE_LEFT = 3;

    // 速度
    public static final float PLAYER_MOVE_VELOCITY = 20.0f;


    public Player(Texture texture, int srcX, int srcY, int srcWidth, int srcHeight) {
        super(texture, srcX, srcY, srcWidth, srcHeight);
        setSize(PLAYER_WIDTH, PLAYER_HEIGHT);
    }

    public void update (float delta){

        setPosition(getX() + velocity.x * delta, getY() + velocity.y * delta);

        // 画面の端まで来たら反対側に移動させる
        if (getX() + PLAYER_WIDTH / 2 < 0) {
            setX(GameScreen.WORLD_WIDTH - PLAYER_WIDTH / 2);
        } else if (getX() + PLAYER_WIDTH / 2 > GameScreen.WORLD_WIDTH) {
            setX(0);
        }
    }

}
