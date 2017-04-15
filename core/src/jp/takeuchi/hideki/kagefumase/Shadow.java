package jp.takeuchi.hideki.kagefumase;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by takeuchi on 2017/04/15.
 */

public class Shadow extends GameObject {

    // 横幅、高さ
    public static final float SHADOW_WIDTH = 1.0f;
    public static final float SHADOW_HEIGHT = 1.0f;

    public Shadow(Texture texture, int srcX, int srcY, int srcWidth, int srcHeight){
        super(texture, srcX, srcY, srcWidth, srcHeight);
        setSize(SHADOW_WIDTH,SHADOW_HEIGHT);
    }
}
