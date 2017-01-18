package Servers;

import java.util.*;

public class KawasakiSocketServer extends SocketServer {
    boolean flgInPosition = false;
    int rotations[] = new int[6];
    int positions[] = new int[6];
    int errors[] = new int[6];
    int uregs[] = new int[6];

    public void setState(int[] state) {
        this.state = state;
    }

    public int[] getState() {
        return state;
    }
    int[] state = new int[6];

    public KawasakiSocketServer(String name) {
        super(name);
    }

    @Override
    void sendDefaultCommand() {
        Integer[] arr = {0, Constants.C_SENSOR_VALS, 0, 0, 0, 0, 0, 0, 0};
        sendValsi(arr);
    }

    @Override
    void processIncomingCommand() {
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
    }

    public int[] getRotations() {
        return Arrays.copyOf(rotations, 6);
    }
    public int[] getPositions() {
        return Arrays.copyOf(positions, 6);
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
