package jp.takeuchi.hideki.kagefumase;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by OWNER on 2017/03/05.
 */

public class School extends GameObject {
    // 横幅、高さ
    public static final float SCHOOL_WIDTH = 2.0f;
    public static final float SCHOOL_HEIGHT = 1.3f;

    public School(Texture texture, int srcX, int srcY, int srcWidth, int srcHeight) {
        super(texture, srcX, srcY, srcWidth, srcHeight);
        setSize(SCHOOL_WIDTH, SCHOOL_HEIGHT );
    }
}
