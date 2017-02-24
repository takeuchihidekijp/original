package jp.takeuchi.hideki.kagefumase;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by OWNER on 2017/02/24.
 */

public class Player extends GameObject{

    // 横幅、高さ
    public static final float PLAYER_WIDTH = 1.0f;
    public static final float PLAYER_HEIGHT = 1.0f;


    public Player(Texture texture, int srcX, int srcY, int srcWidth, int srcHeight) {
        super(texture, srcX, srcY, srcWidth, srcHeight);
        setSize(PLAYER_WIDTH, PLAYER_HEIGHT);
    }
}
