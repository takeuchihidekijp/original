package jp.takeuchi.hideki.kagefumase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Created by OWNER on 2017/02/22.
 */

public class GameScreen extends ScreenAdapter {

    static final float CAMERA_WIDTH = 10;
    static final float CAMERA_HEIGHT = 15;

    static final float WORLD_WIDTH = 10;
    static final float WORLD_HEIGHT = 15 * 20; // 20画面分登れば終了

    static final int GAME_STATE_READY = 0;
    static final int GAME_STATE_PLAYING = 1;
    static final int GAME_STATE_GAMEOVER = 2;

    static final float GUI_WIDTH = 320;
    static final float GUI_HEIGHT = 480;

    //画面の縦の三分の一（横に移動させるために利用）
    static final float GUI_HEIGHT3 = 160;

    private Kagefumase mGame;

    Sprite mBg;
    OrthographicCamera mCamera;
    OrthographicCamera mGuiCamera;

    FitViewport mViewPort;
    FitViewport mGuiViewPort;

    Player mPlayer;

    int mGameState;

    Vector3 mTouchPoint; //タッチされた座標を保持するメンバ変数


    public GameScreen(Kagefumase game){
        mGame = game;

        // TODO 背景の準備 まだ画像を用意できず
        Texture bgTexture = new Texture("back.png");
        // TextureRegionで切り出す時の原点は左上
        mBg = new Sprite( new TextureRegion(bgTexture, 0, 0, 540, 810));
        mBg.setPosition(0, 0);

        // カメラ、ViewPortを生成、設定する
        mCamera = new OrthographicCamera();
        mCamera.setToOrtho(false, CAMERA_WIDTH, CAMERA_HEIGHT);
        mViewPort = new FitViewport(CAMERA_WIDTH, CAMERA_HEIGHT, mCamera);

        // GUI用のカメラを設定する
        mGuiCamera = new OrthographicCamera();
        mGuiCamera.setToOrtho(false, GUI_WIDTH, GUI_HEIGHT);
        mGuiViewPort = new FitViewport(GUI_WIDTH, GUI_HEIGHT, mGuiCamera);

        mTouchPoint = new Vector3();

        createStage();

    }

    @Override
    public void render(float delta){

        // 状態を更新する
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // カメラの中心を超えたらカメラを上に移動させる つまりキャラが画面の上半分には絶対に行かない
        if (mPlayer.getY() > mCamera.position.y) {
            mCamera.position.y = mPlayer.getY();
        }

        // カメラの座標をアップデート（計算）し、スプライトの表示に反映させる
        mCamera.update();
        mGame.batch.setProjectionMatrix(mCamera.combined);

        mGame.batch.begin();

        // 原点は左下
        mBg.setPosition(mCamera.position.x - CAMERA_WIDTH / 2, mCamera.position.y - CAMERA_HEIGHT / 2);
        mBg.draw(mGame.batch);

        //Player
        mPlayer.draw(mGame.batch);

        mGame.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        mViewPort.update(width, height);
        mGuiViewPort.update(width, height);
    }

    // ステージを作成する
    private void createStage(){
        // TODO Playerの画像の用意とアニメーション検討 テクスチャの準備
        Texture playerTexture = new Texture("uma.png");

        // TODO 初期の位置を考える( WORLD_HEIGHT /2は仮)　Playerを配置
        mPlayer = new Player(playerTexture, 0, 0, 72, 72);
        mPlayer.setPosition(WORLD_WIDTH / 2 - mPlayer.getWidth() / 2, WORLD_HEIGHT /2);
    }

    // それぞれのオブジェクトの状態をアップデートする
    private void update(float delta) {
        switch (mGameState) {
            case GAME_STATE_READY:
                updateReady();
                break;
            case GAME_STATE_PLAYING:
                updatePlaying(delta);
                break;
            case GAME_STATE_GAMEOVER:
                updateGameOver();
                break;
        }
    }

    private void updateReady() {
        if (Gdx.input.justTouched()) {
            mGameState = GAME_STATE_PLAYING;
        }
    }

    private void updatePlaying(float delta){
        if (Gdx.input.isTouched()) {
            mGuiViewPort.unproject(mTouchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            Rectangle left = new Rectangle(0, GUI_HEIGHT /3, GUI_WIDTH / 2, GUI_HEIGHT3);
            Rectangle right = new Rectangle(GUI_WIDTH / 2, GUI_HEIGHT /3, GUI_WIDTH / 2, GUI_HEIGHT3);
            Rectangle upper = new Rectangle(0,GUI_HEIGHT - GUI_HEIGHT3,GUI_WIDTH, GUI_HEIGHT /3);
            Rectangle lower = new Rectangle(0,0,GUI_WIDTH,GUI_HEIGHT /3);
            if (left.contains(mTouchPoint.x, mTouchPoint.y)) {
                left();
            }
            if (right.contains(mTouchPoint.x, mTouchPoint.y)) {
                right();
            }
            if (upper.contains(mTouchPoint.x,mTouchPoint.y)){
                upper();
            }
            if (lower.contains(mTouchPoint.x,mTouchPoint.y)){
                lower();
            }
        }

        // Player
        mPlayer.update(delta);
    }

    private void updateGameOver() {

    }

    private void left(){
        mPlayer.setPosition(mPlayer.getX()-0.1f,mPlayer.getY());

    }
    private void right(){
        mPlayer.setPosition(mPlayer.getX()+0.1f,mPlayer.getY());

    }

    private void upper(){
        mPlayer.setPosition(mPlayer.getX(),mPlayer.getY()+0.1f);

    }

    private void lower(){
        mPlayer.setPosition(mPlayer.getX(),mPlayer.getY()-0.1f);

    }

}
