package eu.pstdio;

import java.util.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Main {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private static char[] array = {
            ' ', ' ', ' ', ' ', ' ',
            ' ', 'O', 'O', 'O', ' ',
            ' ', ' ', ' ', ' ', 'O',
            ' ', 'O', 'O', 'O', ' ',
            ' ', ' ', 'O', ' ', ' ',
            ' ', ' ', 'O', ' ', 'O',
            ' ', ' ', 'O', ' ', ' ',
            ' ', ' ', ' ', 'O', ' ',
            ' ', ' ', ' ', ' ', ' '
    };

    private static int rowLen = 5;
    private static int colLen = 9;
    private static int missingElem = 666;
    private static int[] exists = new int[array.length];

    private static char floor = ' ';

    private static List<Integer>[] edges = new ArrayList[array.length];
    private static List<Integer> rout = new ArrayList<>();

    //zwraca id wierzcholka po lewej
    public static Integer getLeftElem(int id) {
        if (id % rowLen > 0) {
            return id - 1;
        }
        return missingElem;
    }

    public static Integer getRightElem(int id) {
        if (id % rowLen < 4) {
            return id + 1;
        }
        return missingElem;
    }

    public static Integer getUpperElem(int id) {
        if (((int) (id / rowLen)) > 0) {
            return id - rowLen;
        }
        return missingElem;
    }

    public static Integer getLowerElem(int id) {
        if (((int) id / rowLen) < 8) {
            return id + rowLen;
        }
        return missingElem;
    }

    public static void setElem(List<Integer> list, int id) {
        if (id != missingElem && array[id] == floor) {
            list.add(id);
        }
    }

    private static void setEdges() {
        int left, right, upper, lower;

        for (int id = 0; id < array.length; id++) {
            if (array[id] == floor) {

                edges[id] = new ArrayList<>();

                left = getLeftElem(id);
                right = getRightElem(id);
                upper = getUpperElem(id);
                lower = getLowerElem(id);

                setElem(edges[id], left);
                setElem(edges[id], right);
                setElem(edges[id], upper);
                setElem(edges[id], lower);

            }
        }
    }

    //zwraca liste wierzcholkow przez ktore trzeba przejsc
    public static boolean setRoute(List<Integer> branch, Integer from, Integer to) {
        List<Integer> branchTmp = new ArrayList<>();
        List<Integer> branchTmp2 = new ArrayList<>(branch);
        branchTmp2.add(from);
        exists[from] = 1;
        boolean out = false;

        if (from.equals(to)) {
            branchTmp = branchTmp2;
            out = true;
        } else {
            for (Integer child : edges[from]) {

                if (exists[child] == 0) {

                    if (setRoute(branchTmp2, child, to)) {
                        out = true;
                        if (branchTmp.isEmpty()) {
                            branchTmp = branchTmp2;
                        } else if (branchTmp.size() > branchTmp2.size()) {
                            branchTmp = branchTmp2;
                        }

                        branchTmp2 = new ArrayList<>(branch);
                        branchTmp2.add(from);

                    }

                }

            }
        }
        if (out == true) {
            branch.clear();
            branch.addAll(branchTmp);
        }

        exists[from] = 0;

        return out;
    }

    public static void startRoute(Integer from, Integer to) {
        rout = new ArrayList<>();
        List tmpRout = new ArrayList<>();
        List tmpRout2 = new ArrayList<>(rout);
        tmpRout2.add(from);

        for (int i = 0; i < exists.length; i++)
            exists[i] = 0;

        exists[from] = 1;

        for (Integer child : edges[from]) {

            if (setRoute(tmpRout2, child, to)) {

                if (tmpRout.isEmpty()) {
                    tmpRout = tmpRout2;
                } else if (tmpRout.size() > tmpRout2.size()) {
                    tmpRout = tmpRout2;
                }

                tmpRout2 = new ArrayList<>(rout);
                tmpRout2.add(from);

            }

        }

        rout = tmpRout;

    }

    public static void main(String[] args) throws InterruptedException {

        String breakStr = "";

        for (int i = 0; i < 1000; i++) {
            breakStr += '\n';
        }

        for (int i = 5; i > 0; i--) {
            System.out.print(breakStr);
            System.out.println(ANSI_GREEN + "Tests will starts for: " + i + ANSI_RESET);

            TimeUnit.SECONDS.sleep(1);
        }

        System.out.print(breakStr);

        setEdges();
        int idTargets[] = new int[]{26, 13, 13, 26, 9};
        int idStarts[] = new int[]{19, 19, 26, 13, 19};
        long milisStart, milisEnd;

        for (int ids = 0; ids < idTargets.length; ids++) {

            System.out.println(ANSI_GREEN + "Starting test (delay two seconds)" + ANSI_RESET);

            TimeUnit.SECONDS.sleep(2);

            milisStart = System.currentTimeMillis();
            System.out.println("Test Starts at: " + milisStart);
            startRoute(idStarts[ids], idTargets[ids]);
            milisEnd = System.currentTimeMillis();

            System.out.println("Rout generated at: " + milisEnd + ". It took " + (milisEnd - milisStart) + " milliseconds.");

            TimeUnit.SECONDS.sleep(5);

            for (int idR = 0; idR < rout.size(); idR++) {

                System.out.print(breakStr);

                System.out.print("_______\n");

                for (int id = 0; id < array.length; id++) {
                    if (id % rowLen == 0) {
                        System.out.print("1");
                    }

                    if (rout.get(idR).equals(id)) {
                        System.out.print(ANSI_RED + "^" + ANSI_RESET);
                    } else if (idTargets[ids] == id) {
                        System.out.print(ANSI_RED + "$" + ANSI_RESET);
                    } else {
                        if (array[id] == 'O') {
                            System.out.print(ANSI_GREEN + array[id] + ANSI_RESET);
                        } else {
                            System.out.print(array[id]);
                        }
                    }

                    if (id % rowLen == 4) {
                        System.out.print("1\n");
                    }

                }

                System.out.print("-------\n");

                TimeUnit.SECONDS.sleep(1);
            }

            System.out.print(breakStr);

        }
    }
}
