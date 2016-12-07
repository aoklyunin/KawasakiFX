package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import kawasakiControll.KawasakiSocketServer;

import java.util.Timer;
import java.util.TimerTask;

public class Controller {
    KawasakiSocketServer kServer;
    KawasakiSocketServer aServer;
    Timer mTimer;
    @FXML
    public void initialize() {
        System.out.println("asfasfas");
        kServer = new KawasakiSocketServer("Kawasaki");
        aServer = new KawasakiSocketServer("Android");

        new Thread(() -> kServer.openSocket(40000)).start();	//Запуск потока
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(() -> aServer.openSocket(40001)).start();	//Запуск потока

        mTimer = new Timer();
        // delay 1000ms, repeat in 5000ms
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //kServer.setState(aServer.getState());
                for (int a:aServer.getState()){
                  //  System.out.print(a+" ");
                }
                //System.out.println();

            }
        }, 1000, 500);


        btnHome.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                kServer.home1();
            }
        });
        btnHome2.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                kServer.home2();
            }
        });
        btnAccess.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setSliders();
            }
        });

        jBtnMove.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                kServer.runInPointA((int)jPosScroll1.getValue(),
                        (int)jPosScroll2.getValue(),
                        (int)jPosScroll3.getValue(),
                        (int)jPosScroll4.getValue(),
                        (int)jPosScroll5.getValue(),
                        (int)jPosScroll6.getValue(),20);
            }
        });
        dBtnMove.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                kServer.runInPointD((int)dPosScroll1.getValue(),
                        (int)dPosScroll2.getValue(),
                        (int)dPosScroll3.getValue(),
                        (int)dPosScroll4.getValue(),
                        (int)dPosScroll5.getValue(),
                        (int)dPosScroll6.getValue(),20);
            }
        });
    }

    void setSliders(){
        int rot [] = kServer.getRotations();
        int pos [] = kServer.getPositions();
        jPosScroll1.setValue(rot[0]);
        jPosScroll2.setValue(rot[1]);
        jPosScroll3.setValue(rot[2]);
        jPosScroll4.setValue(rot[3]);
        jPosScroll5.setValue(rot[4]);
        jPosScroll6.setValue(rot[5]);

        dPosScroll1.setValue(pos[0]);
        dPosScroll2.setValue(pos[1]);
        dPosScroll3.setValue(pos[2]);
        dPosScroll4.setValue(pos[3]);
        dPosScroll5.setValue(pos[4]);
        dPosScroll6.setValue(pos[5]);
    }

    @FXML
    Button btnHome;
    @FXML
    Button btnHome2;
    @FXML
    Button btnAccess;

    @FXML
    Slider jPosScroll1;
    @FXML
    Slider jPosScroll2;
    @FXML
    Slider jPosScroll3;
    @FXML
    Slider jPosScroll4;
    @FXML
    Slider jPosScroll5;
    @FXML
    Slider jPosScroll6;
    @FXML
    Button jBtnMove;

    @FXML
    Slider dPosScroll1;
    @FXML
    Slider dPosScroll2;
    @FXML
    Slider dPosScroll3;
    @FXML
    Slider dPosScroll4;
    @FXML
    Slider dPosScroll5;
    @FXML
    Slider dPosScroll6;
    @FXML
    Button dBtnMove;



}
