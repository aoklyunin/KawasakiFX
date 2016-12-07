package kawasakiControll;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class KawasakiSocketServer {

    String name;
    boolean flgOpenSocket = false;
    ServerSocket serverSocket;
    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    Timer time;
    int pos = 0;
    boolean flgInPosition = false;
    boolean flgSpace = false;
    boolean flgFirstSpace = true;
    int[] lst = new int[9];

    int rotations[] = new int[6];
    int positions[] = new int[6];

    int errors[] = new int[6];
    int uregs[] = new int[6];

    String inStr = "";

    public void openSocket(int port) {
        try {


            serverSocket = new ServerSocket(port); // создаем сокет сервера и привязываем его к вышеуказанному порту
            System.out.println("Waiting for a client...");

            socket = serverSocket.accept(); // заставляем сервер ждать подключений и выводим сообщение когда кто-то связался с сервером
            System.out.println("Got a client :) ... Finally, someone saw me through all the cover!");
            System.out.println();

            // Берем входной и выходной потоки сокета, теперь можем получать и отсылать данные клиенту.
            InputStream sin = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();

            // Конвертируем потоки в другой тип, чтоб легче обрабатывать текстовые сообщения.
            in = new DataInputStream(sin);
            out = new DataOutputStream(sout);

            flgOpenSocket = true;

        } catch (Exception e) {
        }
    }

    public KawasakiSocketServer(String name) {
        this.name = name;
        comFifo = new LinkedList<>();
        time = new Timer();
        time.schedule(new TimerTask() {
            @Override
            public void run() { //ПЕРЕЗАГРУЖАЕМ МЕТОД RUN В КОТОРОМ ДЕЛАЕТЕ ТО ЧТО ВАМ НАДО
                getChars();
                sendCommands();
            }
        }, 200, 200);
    }

    LinkedList<ArrayList<Integer>> comFifo;

    private void sendCommands() {
        boolean flgCommand = false;
        while (comFifo.size() > 0) {
            ArrayList<Integer> lst = comFifo.remove();
            Integer arr[] = lst.toArray(new Integer[lst.size()]);
            sendVals2(arr);
            flgCommand = true;
        }
        if (!flgCommand) {
            Integer[] arr = {0, Constants.C_SENSOR_VALS, 0, 0, 0, 0, 0, 0, 0};
            sendVals2(arr);
        }
    }

    public void addToComFifo(int[] arr) {
        ArrayList<Integer> lst = new ArrayList<>();
        for (int a : arr) {
            lst.add(a);
        }
        comFifo.add(lst);
    }

    public void setState(int[] state) {
        this.state = state;
    }

    public int[] getState() {
        return state;
    }

    int[] state = new int[6];

    public void getChars() {
        if (flgOpenSocket) {
            if (!socket.isConnected()) {
                System.out.println("Client disconnected");
                closeSocket();
            } else
                try {
                    if (flgOpenSocket) {
                        while (in.available()!=-0) {
                            char c = (char) in.readByte();
                            if (Objects.equals(name, "Android"))
                                System.out.print(c);
                            if (c == ' ') {
                                if (!flgSpace && !flgFirstSpace) {
                                    pos++;
                                    lst[pos - 1] = Integer.parseInt(inStr);
                                    inStr = "";
                                    if (pos >= 9) {
                                        switch (lst[1]) {
                                            case Constants.C_GetPositionAxis:
                                                System.arraycopy(lst, 3, rotations, 0, 6);
                                                break;
                                            case Constants.C_GetPosition:
                                                System.arraycopy(lst, 3, positions, 0, 6);
                                                break;
                                            case Constants.C_ERR:
                                                switch (lst[2]) {
                                                    case Constants.ERR_NOT_INRANGE:
                                                        System.out.println("Заданная точка находится вне досягаемости");
                                                        break;
                                                }
                                                break;
                                            case Constants.C_U_REGULATOR:
                                                System.arraycopy(lst, 3, uregs, 0, 6);
                                                break;
                                            case Constants.C_ERR_REGULATOR:
                                                System.arraycopy(lst, 3, errors, 0, 6);
                                                break;
                                            case Constants.C_IN_POS:
                                                flgInPosition = true;
                                                break;
                                            case Constants.A_DELTA_STATE:
                                                System.arraycopy(lst, 3, state, 0, 6);
                                                break;
                                        }
                                        pos = 0;
                                    }
                                }
                                flgSpace = true;
                            } else {
                                inStr += c;
                                flgSpace = false;
                                flgFirstSpace = false;
                            }
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    void sendVals2(Integer[] iVal) {
        if (flgOpenSocket) {
            String s = "";
            for (int i = 0; i < 9; i++)
                s += Constants.getVali(iVal[i], 6) + " ";
            //s+="/"
            sendPackage(s);
        }
    }

    String prevRec = "";

    void sendPackage(String s) {

        try {
            prevRec = s;
            //System.out.println(s.length()+"");
            //System.out.println(s);
            for (int i = 0; i < s.length(); i++) {
                out.writeByte((byte) s.charAt(i));
            }

            //out.writeUTF(s);
            //if(!s.contains("000000 000018 000001"))
            //System.out.println("sended:"+s);
        } catch (IOException e) {
            System.out.println("IO Error");
            e.printStackTrace();
        }
    }

    public int[] getRotations() {
        return Arrays.copyOf(rotations, 6);
    }

    public int[] getPositions() {
        return Arrays.copyOf(positions, 6);
    }

    void closeSocket() {
        int[] arr = {0, Constants.C_STOP, 0, 0, 0, 0, 0, 0, 0};
        addToComFifo(arr);
        if (flgOpenSocket)
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        flgOpenSocket = false;
        System.out.println("Socket closed");

    }


    public void runInPointD(int x, int y, int z, int o, int a, int t, int speed) {
        int[] arr = {0, Constants.C_D_POINT, speed, x, y, z, o, a, t};
        for (int ar : arr) {
            System.out.print(ar);
        }
        System.out.println();
        addToComFifo(arr);
    }

    public void runInPointA(int j1, int j2, int j3, int j4, int j5, int j6, int speed) {
        int[] arr = {0, Constants.C_J_POINT, speed, j1, j2, j3, j4, j5, j6};
        for (int ar : arr) {
            System.out.print(ar);
        }
        System.out.println();
        addToComFifo(arr);
    }

    public void home1() {
        int[] arr = {0, Constants.C_HOME1, 0, 0, 0, 0, 0, 0, 0};
        addToComFifo(arr);
    }

    public void home2() {
        int[] arr = {0, Constants.C_HOME2, 0, 0, 0, 0, 0, 0, 0};
        addToComFifo(arr);
    }

}
