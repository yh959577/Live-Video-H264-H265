package com.example.hy.liveexampleandroid.LivePlayer;

/**
 * Created by Hamik Young on 2018/1/19.
 */

public class PlayPresenterImp implements  PlayPresenter {
    private PlayView mPlayView;
    private PlayModel mPlayModel;

    PlayPresenterImp(PlayView playView){
        mPlayView=playView;
        mPlayModel=new PlayModelImp();
    }

    @Override
    public void startPlay(String address) {
          mPlayModel.startPlay(address);
    }

    @Override
    public void startPlay(String address, int port) {

    }

    @Override
    public void stopPlay() {

    }

    @Override
    public void takePic() {

    }
}
