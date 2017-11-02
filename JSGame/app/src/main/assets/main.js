enchant();

//乱数の取得
function rand(num) {
    return Math.floor(Math.random() * num);
}

//メインプログラム
window.onload = function() {
    //シーン定数
    var S_TITLE = 0;
    var S_HELP = 1;
    var S_PLAY = 2;
    var S_GAMEOVER = 3;
    
    //ゲーム定数
    var R = 2.0;
    var HOLE_X = 58*R;
    var HOLE_Y = (58-12)*R;
    var HOLE_R = 40*R;
    var OUT_DISTANCE = 60;

    var MEDAKA_SCORE = [10, 50, 100];
    var TIME_MAX = 60;
    
    //ゲームオブジェクトの生成
    var game = new Game(320*R, 320*R);

    //変数
    var scene;
    var count = [0, 0, 0];
    var time;
    
    //オブジェクト
    var title;
    var button;
    var hole;
    var help;
    var timeBg;
    var countLabel;
    var timeLabel;
    var getLabel;
    var medakas;
    var effects;
    var resultBg;
    
    //画像の読み込み
    game.preload('logo.png');
    game.preload('bg.png');
    game.preload('button.png');
    game.preload('hole.png');
    game.preload('get.png');
    game.preload('hamon.png');
    game.preload('help.png');
    game.preload('kekka.png');
    game.preload('medaka.png');
    game.preload('result_bg.png');
    game.preload('time_bg.png');
    game.preload('title.png');
    
    //ゲームの前処理完了時に呼ばれる
    game.onload = function() {
        //背景の生成
        var bg = new Sprite(320*R, 320*R);
        bg.image = game.assets['bg.png'];
        game.rootScene.addChild(bg); 
        
        //穴の生成
        hole = new Sprite(116*R, 116*R);
        hole.image = game.assets['hole.png'];
        hole.x = 0;
        hole.y = -12*R;
        game.rootScene.addChild(hole);
        
        //カウントラベルの生成
        countLabel = new Label();
        countLabel.textAlign = "center"
        countLabel.color = "#ffffff";
        countLabel.font = (30*R)+"px 'Century Gothic'";
        countLabel.text = "0";
        countLabel.x = 0;
        countLabel.y = (-12+(116-30)/2)*R;
        countLabel.width = 116*R;
        countLabel.height = 30*R;
        game.rootScene.addChild(countLabel);  

        //めだか郡の生成
        medakas = new Group();
        game.rootScene.addChild(medakas);

        //エフェクト郡の生成
        effects = new Group();
        game.rootScene.addChild(effects);

        //ヘルプの生成
        help = new Sprite(320*R, 320*R);
        help.image = game.assets['help.png'];
        help.visible = false;
        game.rootScene.addChild(help);        

        //時間背景の生成
        timeBg = new Sprite(115*R, 45*R);
        timeBg.image = game.assets['time_bg.png'];
        timeBg.x = 205*R;
        game.rootScene.addChild(timeBg);
    
        //時間ラベルの生成
        timeLabel = new Label();
        timeLabel.textAlign = "center"
        timeLabel.color = "#000000";
        timeLabel.font = (20*R)+"px 'Century Gothic'";
        timeLabel.text = "0";
        timeLabel.x = 250*R;
        timeLabel.y = ((45-20)/2)*R;
        timeLabel.width = 40*R;
        timeLabel.height = 20*R;
        timeLabel.tick = 0;
        timeLabel.onenterframe = function() {         
            if (scene == S_PLAY) {
                this.tick++;
                var time = TIME_MAX-parseInt(this.tick/game.fps);
                if (time <= 0) time = 0;
                this.text = ""+time;         
                if (time == 0) {
                    game.initScene(S_GAMEOVER);
                }
            }
        }
        game.rootScene.addChild(timeLabel);  
        
        //タイトルの生成
        title = new Sprite(225*R, 140*R);
        title.image = game.assets['title.png'];
        title.x = 47*R;
        title.y = 60*R;
        title.visible = false;
        game.rootScene.addChild(title);  
        
        //ボタンの生成
        button = new Sprite(225*R, 45*R);
        button.image = game.assets['button.png'];
        button.x = 47*R;
        button.y = 260*R;
        button.visible = false;
        game.rootScene.addChild(button);
        button.ontouchstart = function(e) {
            if (scene == S_TITLE) {
                game.initScene(S_HELP);
            } else if (scene == S_HELP) {
                game.initScene(S_PLAY);
            } else if (scene == S_GAMEOVER) {
                game.initScene(S_TITLE);
            }
        }        

        //シーンの初期化
        game.initScene(S_TITLE);
        
        //ロゴの表示
        game.addLogoBg();
    };
    
    //シーンの初期化
    game.initScene = function(nextScene) {
        scene = nextScene;
        //タイトル
        if (scene == S_TITLE) {
            title.visible = true;
            button.visible = true;
            button.frame = 0;
            help.visible = false;
            game.clearAll();
            game.addMedakas();
            
            //穴・時間
            hole.opacity = 0; 
            countLabel.opacity = 0; 
            timeBg.opacity = 0; 
            timeLabel.opacity = 0;           
        }
        //ヘルプ
        else if (scene == S_HELP) {
            title.visible = false;
            button.visible = true;
            button.frame = 1;
            help.visible = true;
            
            //穴・時間
            count = [0, 0, 0];
            time = 0;
            countLabel.text = "0";
            timeLabel.text = ""+TIME_MAX;
            timeLabel.tick = 0;
            hole.opacity = 1; 
            countLabel.opacity = 1; 
            timeBg.opacity = 1; 
            timeLabel.opacity = 1;
        }
        //プレイ
        else if (scene == S_PLAY) {
            title.visible = false;
            button.visible = false;
            help.visible = false;
        }
        //ゲームオーバー
        else if (scene == S_GAMEOVER) {
            button.visible = false;
            button.frame = 2;
            game.addResultBg();  

            //穴・時間
            hole.tl.fadeOut(30); 
            countLabel.tl.fadeOut(30); 
            timeBg.tl.fadeOut(30); 
            timeLabel.tl.fadeOut(30); 
        }
    }
    
    //タッチ時に呼ばれる
    game.rootScene.ontouchstart = function(e) {
        if (scene != S_PLAY) return;
        var x = e.x;
        var y = e.y;        
        game.addHamon(x, y);        
        for (var i = 0; i < medakas.childNodes.length; i++) {
            medakas.childNodes[i].ontap(x,y);            
        }       
    }    

    //めだかの追加
    game.addMedakas = function() {
        for (var i = 0; i < 30; i++) {
            game.addMedakaInScreen();
        }
    }

    //めだかの追加
    game.addMedakaInScreen = function() {
        while (true) {
            var x = rand(360*R);
            var y = rand(360*R);
            if (x < 50 && y < 50) continue;
            game.addMedaka(x, y, rand(360*R));            
            break;
        }
    }

    //めだかの追加
    game.addMedakaInEdge = function() {
        var dir = 0;//rand(4);
        var x = 0;
        var y = 0;
        var orientation = 0;
        if (dir == 0) {
            x = (100+rand(360-100))*R;
            y = (360+rand(OUT_DISTANCE))*R;
            orientation = 0+rand(160)-80;
        } else if (dir == 1) {
            x = (-OUT_DISTANCE+rand(OUT_DISTANCE))*R;
            y = (100+rand(360-100))*R;
            orientation = 90+rand(160)-80;
        } else if (dir == 2) {
            x = rand(360)*R;
            y = (-OUT_DISTANCE+rand(OUT_DISTANCE))*R;
            orientation = 180+rand(160)-80;
        } else if (dir == 3) {
            x = (360+rand(OUT_DISTANCE))*R;
            y = rand(360)*R;
            orientation = 270+rand(160)-80;
        }
        game.addMedaka(x, y, orientation);            
    }

    //めだかの追加
    game.addMedaka = function(x, y, orientation) {
        //めだかの生成
        var medaka = new Sprite(16*R, 41*R);
        medaka.image = game.assets['medaka.png'];        
        medaka.x = x;
        medaka.y = y;
        var idx = rand(10);
        if (idx < 6) {
            medaka.type = 0;
        } else if (idx < 9) {
            medaka.type = 1;
        } else {
            medaka.type = 2;
        }
        medaka.frameTick = 0;
        medaka.frame = medaka.type*2;
        medaka.rotationTick = 60;
        medaka.rotationTo = orientation;
        medaka.rotation = medaka.rotationTo;
        medaka.speedMin = (0.1+rand(10)/10.0)*R;
        medaka.speedMax = (medaka.speedMin+0.5+rand(20)/10.0)*R;
        medaka.speedDec = 0;
        medaka.speed = medaka.speedMin;
        medaka.active = true;
        
        //定期処理
        medaka.onenterframe = function() {  
            if (!this.active) {
                this.frameTick++;
                if (this.frameTick >= 2) this.frameTick = 0;
                this.frame = this.type*2+this.frameTick;
                return;
            }
            
            //フレーム
            this.frameTick += 0.08+(this.speed/10.0);
            if (this.frameTick >= 3) this.frameTick = this.frameTick%3;
            var idx = parseInt(this.frameTick);
            if (idx == 0) this.frame = this.type*2;
            if (idx == 1 || idx == 2) this.frame = this.type*2+1;
            
            //向き
            this.rotationTick--;
            if (this.rotationTick < 0) {
                this.rotationTick = 300;
                this.rotationTo = this.rotation+rand(60)-30;
            }
            if (this.rotationTick%2 == 0) {
                if (this.rotation < this.rotationTo) {
                    this.rotation += 1;
                } else if (this.rotation > this.rotationTo) {
                    this.rotation -= 1;
                }                 
            } 
            
            //スピード調整
            this.speed -= this.speedDec;
            if (this.speed < this.speedMin) {
                this.speed = this.speedMin;
            }
            
            //移動
            this.x += this.speed*Math.cos((this.rotation-90)/180*Math.PI);
            this.y += this.speed*Math.sin((this.rotation-90)/180*Math.PI);
            
            //穴
            if (scene == S_PLAY) {
                var distance = Math.sqrt(
                    (this.x-HOLE_X)*(this.x-HOLE_X)+
                    (this.y-HOLE_Y)*(this.y-HOLE_Y));
                if (distance < HOLE_R) {
                    this.inhole();
                }
            }
            
            //画面外
            if (this.x < -OUT_DISTANCE*R || 
                (320+OUT_DISTANCE)*R < this.x ||
                this.y < -OUT_DISTANCE*R || 
                (320+OUT_DISTANCE)*R < this.y) {

                //新規
                medakas.removeChild(this);
                game.addMedakaInEdge();
            }
        }
        
        //穴に吸い込まれる
        medaka.inhole = function() {
            if (scene != S_PLAY || !this.active) return;
            this.active = false;

            //加点
            game.addGetLabel(this.x, this.y);
            count[this.type]++;
            countLabel.text = ""+(count[0]+count[1]+count[2]);
            this.tl.
                moveTo(HOLE_X, HOLE_Y, 30).and().fadeOut(30).
                then(function() {
                    //新規
                    medakas.removeChild(this);
                    game.addMedakaInEdge();
                    game.addMedakaInEdge();
                    game.addMedakaInEdge();
                    game.addMedakaInEdge();
                });
        }
        
        //タップ
        medaka.ontap = function(x, y) {
            if (scene != S_PLAY || !this.active) return;
            var distance = Math.sqrt(
                (this.x-x)*(this.x-x)+
                (this.y-y)*(this.y-y));        
            if (distance > 100) return;
            
            //スピード
            this.speedDec = (this.speedMax-this.speedMin)/(10.0+rand(100));
            this.speed = this.speedMax; 
            
            //向き
            var radian = Math.atan2(this.y-y, this.x-x);
            this.rotationTick = 0;
            this.rotationTo = (radian*180/Math.PI)+90;      
            this.rotation = this.rotationTo;
        }
    
        //めだかの追加
        medakas.addChild(medaka);     
    }
    
    //全クリア
    game.clearAll = function() {
        for (var i = medakas.childNodes.length-1; i >= 0; i--) {
            medakas.removeChild(medakas.childNodes[i]);            
        }
        for (var i = effects.childNodes.length-1; i >= 0; i--) {
            effects.removeChild(effects.childNodes[i]);            
        }
    }
    
    //波紋の追加
    game.addHamon = function(x, y) {
        //波紋の生成
        var hamon = new Sprite(100*R, 100*R);
        hamon.image = game.assets['hamon.png'];        
        hamon.tick = 0;
        hamon.x = x-50*R;
        hamon.y = y-50*R;
        hamon.frame = 0;

        //定期処理
        hamon.onenterframe = function() {  
            this.tick += 0.6;
            if (this.tick >= 4) {
                effects.removeChild(this);
                return;
            }
            var idx = parseInt(this.tick);
            if (idx == 0 || idx == 1) this.frame = 0;
            if (idx == 2) this.frame = 1;
            if (idx == 3) this.frame = 2;
        }

        //波紋の追加
        effects.addChild(hamon);     
    }
    
    //ゲットラベルの追加
    game.addGetLabel = function(x, y) {
        //ゲットラベルの生成
        getLabel = new Sprite(50*R, 25*R);
        getLabel.image = game.assets['get.png'];
        getLabel.x = x+(8-25)*R;
        getLabel.y = y+(48-60)*R;
        
        //ゲットラベルの追加
        effects.addChild(getLabel); 
        
        //ゲットラベルのアニメ
        getLabel.tl.
            moveBy(0, -5*R, 5). 
            fadeOut(5).
            then(function(){
                effects.removeChild(this);
            });
    }
    
    //結果背景の追加
    game.addResultBg = function() {
        //結果背景の生成
        resultBg = new Group();
        var bg = new Sprite(320*R, 320*R);
        bg.image = game.assets['result_bg.png'];
        resultBg.addChild(bg);
        
        //スコア0
        var lblScore0 = new Label();
        lblScore0.color = "#000000";
        lblScore0.font = (10*R)+"px 'Century Gothic'";
        lblScore0.text = MEDAKA_SCORE[0]+"点";
        lblScore0.x = 80*R;
        lblScore0.y = 100*R;
        resultBg.addChild(lblScore0);
        
        //数0
        var lblCount0 = new Label();
        lblCount0.color = "#000000";
        lblCount0.font = (20*R)+"px 'Century Gothic'";
        lblCount0.text = "☓"+count[0];
        lblCount0.x = 80*R;
        lblCount0.y = 114*R;
        resultBg.addChild(lblCount0);
        
        //スコア1
        var lblScore1 = new Label();
        lblScore1.color = "#000000";
        lblScore1.font = (10*R)+"px 'Century Gothic'";
        lblScore1.text = MEDAKA_SCORE[1]+"点";
        lblScore1.x = 150*R;
        lblScore1.y = 100*R;
        resultBg.addChild(lblScore1);

        //数1
        var lblCount1 = new Label();
        lblCount1.color = "#000000";
        lblCount1.font = (20*R)+"px 'Century Gothic'";
        lblCount1.text = "☓"+count[1];
        lblCount1.x = 150*R;
        lblCount1.y = 114*R;
        resultBg.addChild(lblCount1);

        //スコア2
        var lblScore2 = new Label();
        lblScore2.color = "#000000";
        lblScore2.font = (10*R)+"px 'Century Gothic'";
        lblScore2.text = MEDAKA_SCORE[2]+"点";
        lblScore2.x = 220*R;
        lblScore2.y = 100*R;
        resultBg.addChild(lblScore2);
        
        //数2
        var lblCount2 = new Label();
        lblCount2.color = "#000000";
        lblCount2.font = (20*R)+"px 'Century Gothic'";
        lblCount2.text = "☓"+count[2];
        lblCount2.x = 220*R;
        lblCount2.y = 114*R;
        resultBg.addChild(lblCount2);

        //スコア
        var lblScore = new Label();
        lblScore.textAlign = "center"
        lblScore.color = "#000000";
        lblScore.font = (40*R)+"px 'Century Gothic'";
        lblScore.text = (
            MEDAKA_SCORE[0]*count[0]+
            MEDAKA_SCORE[1]*count[1]+
            MEDAKA_SCORE[2]*count[2])+"点";
        lblScore.x = 0*R;
        lblScore.y = 160*R;
        lblScore.width = 320*R;
        resultBg.addChild(lblScore);
        
        //結果背景の追加
        effects.addChild(resultBg);
        resultBg.y = -320*R;
        resultBg.tl.moveTo(0, 0, 30).
            moveTo(0, -10*R, 10).
            moveTo(0, 0, 10).
            then(function(){
            button.visible = true;
        });
    }
    
    //ロゴの追加
    game.addLogoBg = function() {
        //ロゴ背景の生成
        var logoBg = new Group();

        //背景の生成
        var bg = new Sprite(320*R, 320*R);
        bg.image = new Surface(320*R, 320*R);
        var context = bg.image.context;
        context.fillStyle = "rgb(255, 255, 255)";
        context.fillRect(0, 0, 320*R, 320*R);
        logoBg.addChild(bg);
        
        //ロゴの生成
        var logo = new Sprite(200*R, 45*R);
        logo.x = ((320-200)/2)*R;
        logo.y = ((320-45)/2)*R;
        logo.opacity = 0;
        logo.image = game.assets['logo.png'];
        logoBg.addChild(logo);
        
        //ロゴ背景の追加
        game.rootScene.addChild(logoBg);
        
        //アニメ
        logo.tl.
            delay(10).
            fadeIn(20).
            delay(10).
            fadeOut(20).
            delay(20).
            then(function() {
            game.rootScene.removeChild(logoBg);
        })
        logoBg.ontouchstart = function(e) {
            game.rootScene.removeChild(logoBg);
        }        
    }    
    
    //ゲームの開始
    game.start();
};
