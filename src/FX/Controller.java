package FX;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import Servers.KawasakiSocketServer;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.RunnableFuture;

public class Controller {
    private KawasakiSocketServer kServer;
    private Timer mTimer;

    // остановка контроллера
    public void close() {
        // останавливаем сервер кавасаки
        if (kServer != null) kServer.close();
        // останавливаем таймер контроллера
        if (mTimer != null){
            mTimer.cancel();
            mTimer.purge();
        }
    }
    private void onTime() {
        // будет выполняться по таймеру
    }


    @FXML
    public void initialize() {
        kServer = new KawasakiSocketServer("Kawasaki");

        new Thread(() -> kServer.openSocket(40000)).start();	//Запуск потока
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        initTimer();
        addEvents();
    }

    // инициализация таймера
    private void initTimer() {
        int firstDelay = 1000; // задержка перед первым срабатыванием таймера
        int repeat = 500; // время между срабатываниями таймера
        mTimer = new Timer();
        // задержка 1000ms, repeat in 5000ms
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                onTime();
            }
        }, firstDelay, repeat);
    }

    // обработчики событий
    private void addEvents() {
        // нажатие на кнопку Home
        btnHome.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> kServer.home1(updateSlidersRunnable));
        // нажатие на кнопку Home2
        btnHome2.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> kServer.home2(updateSlidersRunnable));
        // нажатие на кнопку Совместить
        btnAccess.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> setSliders());
        // нажатие на кнопку двигаться по джоинтам
        jBtnMove.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent ->
                kServer.runInPointA((int) jPosScroll1.getValue(),
                (int) jPosScroll2.getValue(),
                (int) jPosScroll3.getValue(),
                (int) jPosScroll4.getValue(),
                (int) jPosScroll5.getValue(),
                (int) jPosScroll6.getValue(), 20,updateSlidersRunnable));
        // нажатие на кнопку двигаться по декарту
        dBtnMove.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent ->
                kServer.runInPointD((int) dPosScroll1.getValue(),
                (int) dPosScroll2.getValue(),
                (int) dPosScroll3.getValue(),
                (int) dPosScroll4.getValue(),
                (int) dPosScroll5.getValue(),
                (int) dPosScroll6.getValue(), 20,updateSlidersRunnable));
    }

    // задаём значения слайдеров
    private void setSliders() {
        // если кавасаки не создан или нельзя получить от него данные
        if (kServer == null||!kServer.flgOpenSocket)
            // обрываем выполнение функции
            return;
        int rot[] = kServer.getRotations();
        int pos[] = kServer.getPositions();
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
    /*
        Переменные, связывающие объекты в файле разметки и объекты на форме
     */
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

    Runnable updateSlidersRunnable = new Runnable() {
        @Override
        public void run() {
            setSliders();
        }
    };
}
