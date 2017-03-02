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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



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

    Random mRandom;
    List<Enemy> mEnemys;
    Player mPlayer;

    int mGameState;

    Vector3 mTouchPoint; //継続してタッチされた座標を保持するメンバ変数
    Vector3 beginTouchPoint; //最初にタッチされた座標を保持するメンバ変数


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

        // メンバ変数の初期化
        mRandom = new Random();
        mEnemys = new ArrayList<Enemy>();

        mTouchPoint = new Vector3();
        beginTouchPoint = new Vector3();

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

        // Enemy
        //ｚは捕まえた敵の数を保持して後続の描画で後ろにつなげる際に利用
        int z = 1;
        for (int i=0; i < mEnemys.size(); i++){
            //20170301
            if ( mEnemys.get(i).mState == Enemy.ENEMY_TYPE_MOVING){
                mEnemys.get(i).draw(mGame.batch);
            }else {
                //捕まった敵をX軸はプレイヤーと一緒。Y軸はプレイヤーの位置から捕まえた敵の数分引いていく
                mEnemys.get(i).setPosition(mPlayer.getX(), mPlayer.getY() - mEnemys.get(i).getHeight()*z);
                mEnemys.get(i).draw(mGame.batch);
                z++;
            }

        }

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
        Texture enemysTexture = new Texture("enemy.png");


        // TODO 初期の位置を考える( WORLD_HEIGHT /2は仮)　Playerを配置
        mPlayer = new Player(playerTexture, 0, 0, 72, 72);
  //      mPlayer.setPosition(WORLD_WIDTH / 2 - mPlayer.getWidth() / 2, WORLD_HEIGHT /2);
        mPlayer.setPosition(WORLD_WIDTH / 2 - mPlayer.getWidth() / 2, 5.0f);

        // TODO Enemyをゴールの高さまで配置していく(配置ロジック検討)
        float y = 0;
        while ( y < WORLD_HEIGHT -5 ){
            float x = mRandom.nextFloat() * (WORLD_WIDTH - Enemy.ENEMY_WIDTH);

            if (mRandom.nextFloat() > 0.5f){
                Enemy enemy = new Enemy(enemysTexture, 0, 0, 120, 74);
                //敵はPlayerより上に配置
                enemy.setPosition(x, y + mPlayer.getY());
                mEnemys.add(enemy);
            }

            y++;
        }

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

        if (Gdx.input.justTouched()){
            // 始点を保持
            mGuiViewPort.unproject(beginTouchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        }

        if (Gdx.input.isTouched()) {
            mGuiViewPort.unproject(mTouchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

            if( (mTouchPoint != null) && (beginTouchPoint != null) ) {

                Vector3 diff = mTouchPoint.sub(beginTouchPoint);

                if (Math.abs(diff.x) > Math.abs(diff.y)) {

                    if (diff.x > 10) {
                        right();
                    } else if (diff.x < -10) {
                        left();
                    }
                } else {
                    if (diff.y > 10) {
                        upper();
                    } else {
                        lower();
                    }
                }
            }

        }

        //Enemy
        for (int i =0; i < mEnemys.size(); i++){
            mEnemys.get(i).update(delta);
//20170301
            if ( mEnemys.get(i).mState == Enemy.ENEMY_TYPE_CAUGHT){
                mEnemys.get(i).setPosition(mPlayer.getX(), mPlayer.getY() - mEnemys.get(i).getHeight());
            }


         }


        // Player
        mPlayer.update(delta);

        // 当たり判定を行う
        checkCollision();

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

    private void checkCollision(){

        //Enemyとの当たり判定)
        for (int i = 0; i < mEnemys.size(); i++){
            Enemy enemy = mEnemys.get(i);

            if (enemy.mState == Enemy.ENEMY_TYPE_CAUGHT){
                continue;
            }

            if (mPlayer.getBoundingRectangle().overlaps(enemy.getBoundingRectangle())){
                enemy.chatched();
                break;
            }
        }

    }

}
